package com.example.project

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        binding.btnStartOrdering.setOnClickListener { finish() }

        cartAdapter = CartAdapter(
            cartItems = CartManager.getItems().toMutableList(),
            context = this,
            onQuantityChanged = { updateSummary() }
        )
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.cartRecyclerView.adapter = cartAdapter

        updateSummary()

        binding.btnCheckout.setOnClickListener {
            if (!CartManager.isEmpty()) {
                startActivity(Intent(this, PaymentActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cartAdapter.refreshData(CartManager.getItems().toMutableList())
        updateSummary()
    }

    private fun updateSummary() {
        binding.tvSubtotal.text  = getString(R.string.product_price, CartManager.getSubtotal())
        binding.tvDelivery.text  = getString(R.string.product_price, CartManager.getDeliveryFee())
        binding.tvTotal.text     = getString(R.string.product_price, CartManager.getTotal())
        binding.tvItemCount.text = getString(R.string.cart_item_count, CartManager.getItemCount())

        if (CartManager.isEmpty()) {
            binding.emptyCartState.visibility   = View.VISIBLE
            binding.cartRecyclerView.visibility = View.GONE
            binding.cartSummaryCard.visibility  = View.GONE
            binding.btnCheckout.isEnabled       = false
            binding.btnCheckout.alpha           = 0.5f
        } else {
            binding.emptyCartState.visibility   = View.GONE
            binding.cartRecyclerView.visibility = View.VISIBLE
            binding.cartSummaryCard.visibility  = View.VISIBLE
            binding.btnCheckout.isEnabled       = true
            binding.btnCheckout.alpha           = 1.0f
        }
    }
}