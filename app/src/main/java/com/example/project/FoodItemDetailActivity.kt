package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.example.project.databinding.ActivityFoodItemDetailBinding

class FoodItemDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFoodItemDetailBinding
    private var quantity = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ── Receive product ID passed from MainActivity ──────────────
        val productId = intent.getIntExtra("PRODUCT_ID", -1)
        if (productId == -1) { finish(); return }

        val db = FlyDatabaseHelper(this)
        val product = db.getProductById(productId) ?: run { finish(); return }

        // ── Populate UI ──────────────────────────────────────────────
        binding.tvFoodName.text        = product.name
        binding.tvFoodPrice.text       = getString(R.string.product_price, product.price)
        binding.tvFoodDescription.text = product.description
        binding.tvFoodRating.text      = getString(R.string.product_rating, product.rating)
        binding.tvFoodCategory.text    = product.category

        if (product.imageRes != 0) {
            binding.imgFoodDetail.setImageResource(product.imageRes)
        }

        // ── Build ingredient chips from description ───────────────────
        val ingredients = product.description
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        for (ingredient in ingredients) {
            val chip = Chip(this).apply {
                text = ingredient
                isClickable = false
                isCheckable = false
                chipBackgroundColor = ContextCompat.getColorStateList(
                    this@FoodItemDetailActivity, R.color.pale_blue
                )
                setTextColor(ContextCompat.getColor(
                    this@FoodItemDetailActivity, R.color.navy
                ))
                textSize = 12f
                chipCornerRadius = 32f
            }
            binding.chipGroupIngredients.addView(chip)
        }

        // ── Back button ──────────────────────────────────────────────
        binding.btnBack.setOnClickListener {
            finish()
        }

        // ── Cart icon → open CartActivity ────────────────────────────
        binding.btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        // ── Quantity controls ────────────────────────────────────────
        binding.btnDecrease.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.tvQuantity.text = quantity.toString()
            }
        }

        binding.btnIncrease.setOnClickListener {
            quantity++
            binding.tvQuantity.text = quantity.toString()
        }

        // ── Add to Cart ──────────────────────────────────────────────
        binding.btnAddToCart.setOnClickListener {
            repeat(quantity) { CartManager.addItem(product) }
            quantity = 1
            binding.tvQuantity.text = "1"
            Toast.makeText(
                this,
                getString(R.string.added_to_cart, product.name),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}