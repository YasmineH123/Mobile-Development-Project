package com.example.project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductsAdapter(
    private var productList: List<Product>,
    private val context: Context,
    private val onProductClick: (Product) -> Unit,
    private val onAddToCartClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    // ── ViewHolder ──────────────────────────────────────────
    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView   = itemView.findViewById(R.id.productName)
        val productRating: TextView = itemView.findViewById(R.id.productRating)
        val productPrice: TextView  = itemView.findViewById(R.id.productPrice)
        val addToCartBtn: ImageView = itemView.findViewById(R.id.addToCartBtn)
    }

    // ── onCreateViewHolder ───────────────────────────────────
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    // ── getItemCount ─────────────────────────────────────────
    override fun getItemCount(): Int = productList.size

    // ── onBindViewHolder ─────────────────────────────────────
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.productName.text = product.name
        holder.productRating.text = context.getString(R.string.product_rating, product.rating)
        holder.productPrice.text  = context.getString(R.string.product_price, product.price)
        if (product.imageRes != 0) {
            holder.productImage.setImageResource(product.imageRes)
        } else {
            holder.productImage.setImageResource(R.drawable.ic_home)
        }
        holder.itemView.setOnClickListener {
            onProductClick(product)
        }

        holder.addToCartBtn.setOnClickListener {
            onAddToCartClick(product)
        }
    }

    // ── refreshData ──────────────────────────────────────────
    fun refreshData(newList: List<Product>) {
        val oldSize = productList.size
        productList = newList
        val newSize = productList.size
        if (oldSize == newSize) {
            notifyItemRangeChanged(0, newSize)
        } else if (newSize > oldSize) {
            notifyItemRangeChanged(0, oldSize)
            notifyItemRangeInserted(oldSize, newSize - oldSize)
        } else {
            notifyItemRangeChanged(0, newSize)
            notifyItemRangeRemoved(newSize, oldSize - newSize)
        }
    }

} // ← end of class