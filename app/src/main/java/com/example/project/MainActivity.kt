package com.example.project

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.project.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // ── ViewBinding + DB + Adapter ───────────────────────────
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: FlyDatabaseHelper
    private lateinit var productsAdapter: ProductsAdapter

    private var allProducts: List<Product> = emptyList()
    private var selectedCategory: String = "All"

    // ── onCreate ─────────────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Bind the layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.searchBar.setHintTextColor(getColor(R.color.light_blue))

        // 2. Set up toolbar
        val toolbar = binding.toolbar as androidx.appcompat.widget.Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // 3. Create DB helper instance
        dbHelper = FlyDatabaseHelper(this)

        // 4. Load all products from DB
        allProducts = dbHelper.getAllProducts()

        // 5. Set up RecyclerView with 2-column grid
        productsAdapter = ProductsAdapter(
            productList = allProducts,
            context = this,
            onProductClick = { product ->
                // TODO: teammate will create SingleProductActivity
                // val intent = Intent(this, SingleProductActivity::class.java)
                // intent.putExtra("PRODUCT_ID", product.id)
                // startActivity(intent)
            },
            onAddToCartClick = { product ->
                // cart logic — teammate handles this
                Toast.makeText(
                    this,
                    getString(R.string.added_to_cart, product.name),
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        binding.productsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.productsRecyclerView.adapter = productsAdapter

        // 6. Build category chips
        buildCategoryChips()

        // 7. Build featured horizontal row
        buildFeaturedRow()

        // 8. Search bar listener
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // ── onResume: refresh like the Dr. did ───────────────────
    override fun onResume() {
        super.onResume()
        allProducts = dbHelper.getAllProducts()
        filterProducts(binding.searchBar.text.toString())
    }

    // ── Build Category Chips ─────────────────────────────────
    private fun buildCategoryChips() {
        val categories = dbHelper.getCategories()
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

            // style: selected = navy filled, unselected = outlined
            updateChipStyle(chip, category == selectedCategory)

            chip.setOnClickListener {
                selectedCategory = category
                updateAllChipStyles()
                filterProducts(binding.searchBar.text.toString())
            }

            binding.categoryChipsContainer.addView(chip)
        }
    }

    // ── Update single chip style ─────────────────────────────
    private fun updateChipStyle(chip: TextView, isSelected: Boolean) {
        if (isSelected) {
            chip.setBackgroundResource(R.drawable.chip_selected_bg)
            chip.setTextColor(getColor(R.color.white))
        } else {
            chip.setBackgroundResource(R.drawable.chip_unselected_bg)
            chip.setTextColor(getColor(R.color.navy))
        }
    }

    // ── Update all chips after selection changes ─────────────
    private fun updateAllChipStyles() {
        val categories = dbHelper.getCategories()
        for (i in 0 until binding.categoryChipsContainer.childCount) {
            val chip = binding.categoryChipsContainer.getChildAt(i) as TextView
            updateChipStyle(chip, chip.text.toString() == selectedCategory)
        }
    }

    // ── Build Featured Horizontal Row ────────────────────────
    private fun buildFeaturedRow() {
        val featured = dbHelper.getPopularProducts()
        binding.featuredContainer.removeAllViews()

        for (product in featured) {
            val itemView = LayoutInflater.from(this)
                .inflate(R.layout.featured_item, binding.featuredContainer, false)

            itemView.findViewById<TextView>(R.id.featuredName).text = product.name
            if (product.imageRes != 0) {
                itemView.findViewById<ImageView>(R.id.featuredImage)
                    .setImageResource(product.imageRes)
            }
            itemView.findViewById<TextView>(R.id.featuredPrice).text =
                getString(R.string.product_price, product.price)
            itemView.findViewById<TextView>(R.id.featuredRating).text =
                getString(R.string.product_rating, product.rating)

            itemView.setOnClickListener {
                // TODO: teammate will create SingleProductActivity
                // val intent = Intent(this, SingleProductActivity::class.java)
                // intent.putExtra("PRODUCT_ID", product.id)
                // startActivity(intent)
            }
            binding.featuredContainer.addView(itemView)
        }
    }

    // ── Filter products by search + category ─────────────────
    private fun filterProducts(query: String) {
        val filtered = allProducts.filter { product ->
            val matchesCategory = selectedCategory == "All" ||
                    product.category == selectedCategory
            val matchesSearch = product.name.contains(query, ignoreCase = true) ||
                    product.description.contains(query, ignoreCase = true)
            matchesCategory && matchesSearch
        }
        productsAdapter.refreshData(filtered)
    }
}