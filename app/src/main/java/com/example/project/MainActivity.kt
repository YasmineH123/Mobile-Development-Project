package com.example.project

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.project.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: FlyDatabaseHelper
    private lateinit var productsAdapter: ProductsAdapter

    private var allProducts: List<Product> = emptyList()
    private var selectedCategory: String = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.searchBar.setHintTextColor(getColor(R.color.light_blue))

        val toolbar = binding.toolbar.root as androidx.appcompat.widget.Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Logout button
        val btnLogout = binding.toolbar.root.findViewById<Button>(R.id.btnLogout)
        btnLogout?.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout") { _, _ ->
                    FirebaseAuth.getInstance().signOut()
                    startActivity(android.content.Intent(this, LoginActivity::class.java))
                    finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // Welcome message showing logged-in user's email
        val currentUser = FirebaseAuth.getInstance().currentUser
        val tvWelcome = binding.tvWelcomeUser
        if (currentUser != null) {
            tvWelcome.text = "Logged in as ${currentUser.email}"
        }

        // Cart icon → CartActivity
        val btnCart = binding.toolbar.root.findViewById<ImageView>(R.id.btnCart)
        btnCart?.setOnClickListener {
            startActivity(android.content.Intent(this, CartActivity::class.java))
        }
        binding.toolbar.root.findViewById<android.view.View>(R.id.cartIconWrapper)?.setOnClickListener {
            startActivity(android.content.Intent(this, CartActivity::class.java))
        }

        dbHelper = FlyDatabaseHelper(this)
        allProducts = dbHelper.getAllProducts()

        android.util.Log.d("FLY_DEBUG", "Total products: ${allProducts.size}")

        productsAdapter = ProductsAdapter(
            productList = allProducts,
            context = this,
            onProductClick = { product ->
                val intent = android.content.Intent(this, FoodItemDetailActivity::class.java)
                intent.putExtra("PRODUCT_ID", product.id)
                startActivity(intent)
            },
            onAddToCartClick = { product ->
                CartManager.addItem(product)
                updateCartBadge()
                Toast.makeText(
                    this,
                    getString(R.string.added_to_cart, product.name),
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        binding.productsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.productsRecyclerView.adapter = productsAdapter
        binding.productsRecyclerView.isNestedScrollingEnabled = false

        buildCategoryChips()
        buildFeaturedRow()

        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // show all 12 on launch
        productsAdapter.refreshData(allProducts)
    }

    override fun onResume() {
        super.onResume()
        updateCartBadge()
    }

    private fun updateCartBadge() {
        val badge = binding.toolbar.root.findViewById<android.widget.TextView>(R.id.cartBadge)
        val count = CartManager.getItemCount()
        if (badge != null) {
            if (count > 0) {
                badge.visibility = android.view.View.VISIBLE
                badge.text = if (count > 99) "99+" else count.toString()
            } else {
                badge.visibility = android.view.View.GONE
            }
        }
    }

    private fun buildCategoryChips() {
        val poppins = ResourcesCompat.getFont(this, R.font.poppins)
        binding.categoryChipsContainer.removeAllViews()

        for (category in dbHelper.getCategories()) {
            val chip = TextView(this)
            chip.text = category
            chip.textSize = 13f
            chip.typeface = poppins
            chip.gravity = Gravity.CENTER
            chip.setPadding(32, 16, 32, 16)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            chip.layoutParams = params

            updateChipStyle(chip, category == selectedCategory)

            chip.setOnClickListener {
                selectedCategory = category
                updateAllChipStyles()
                filterProducts(binding.searchBar.text.toString())
            }

            binding.categoryChipsContainer.addView(chip)
        }
    }

    private fun updateChipStyle(chip: TextView, isSelected: Boolean) {
        if (isSelected) {
            chip.setBackgroundResource(R.drawable.chip_selected_bg)
            chip.setTextColor(getColor(R.color.white))
        } else {
            chip.setBackgroundResource(R.drawable.chip_unselected_bg)
            chip.setTextColor(getColor(R.color.navy))
        }
    }

    private fun updateAllChipStyles() {
        for (i in 0 until binding.categoryChipsContainer.childCount) {
            val chip = binding.categoryChipsContainer.getChildAt(i) as TextView
            updateChipStyle(chip, chip.text.toString() == selectedCategory)
        }
    }

    private fun buildFeaturedRow() {
        binding.featuredContainer.removeAllViews()
        for (product in dbHelper.getPopularProducts()) {
            val itemView = LayoutInflater.from(this)
                .inflate(R.layout.featured_item, binding.featuredContainer, false)

            itemView.findViewById<TextView>(R.id.featuredName).text = product.name
            itemView.findViewById<TextView>(R.id.featuredPrice).text =
                getString(R.string.product_price, product.price)
            itemView.findViewById<TextView>(R.id.featuredRating).text =
                getString(R.string.product_rating, product.rating)

            if (product.imageRes != 0) {
                val featuredImage = itemView.findViewById<android.widget.ImageView>(R.id.featuredImage)
                featuredImage.setImageResource(product.imageRes)
            }

            itemView.setOnClickListener {
                val intent = android.content.Intent(this, FoodItemDetailActivity::class.java)
                intent.putExtra("PRODUCT_ID", product.id)
                startActivity(intent)
            }
            binding.featuredContainer.addView(itemView)
        }
    }

    private fun filterProducts(query: String) {
        val filtered = allProducts.filter { product ->
            val matchesCategory = selectedCategory == "All" ||
                    product.category == selectedCategory
            val matchesSearch = query.isEmpty() ||
                    product.name.contains(query, ignoreCase = true) ||
                    product.description.contains(query, ignoreCase = true)
            matchesCategory && matchesSearch
        }

        if (filtered.isEmpty()) {
            binding.productsRecyclerView.visibility = android.view.View.GONE
            binding.emptyState.visibility = android.view.View.VISIBLE
        } else {
            binding.productsRecyclerView.visibility = android.view.View.VISIBLE
            binding.emptyState.visibility = android.view.View.GONE
        }

        productsAdapter.refreshData(filtered)
    }
}