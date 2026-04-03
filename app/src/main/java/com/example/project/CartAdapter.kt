package com.example.project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(
    private var cartItems: MutableList<CartItem>,
    private val context: Context,
    private val onQuantityChanged: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.cartItemImage)
        val productName: TextView   = itemView.findViewById(R.id.cartItemName)
        val productPrice: TextView  = itemView.findViewById(R.id.cartItemPrice)
        val itemTotal: TextView     = itemView.findViewById(R.id.cartItemTotal)
        val btnDecrease: TextView   = itemView.findViewById(R.id.cartBtnDecrease)
        val tvQuantity: TextView    = itemView.findViewById(R.id.cartItemQuantity)
        val btnIncrease: TextView   = itemView.findViewById(R.id.cartBtnIncrease)
        val btnRemove: ImageView    = itemView.findViewById(R.id.cartBtnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int = cartItems.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        val product = item.product

        holder.productName.text  = product.name
        holder.productPrice.text = context.getString(R.string.product_price, product.price)
        holder.tvQuantity.text   = item.quantity.toString()
        holder.itemTotal.text    = context.getString(R.string.product_price, item.totalPrice)

        if (product.imageRes != 0) {
            holder.productImage.setImageResource(product.imageRes)
        }

        holder.btnIncrease.setOnClickListener {
            CartManager.increaseQuantity(product.id)
            refreshData(CartManager.getItems().toMutableList())
            onQuantityChanged()
        }

        holder.btnDecrease.setOnClickListener {
            CartManager.decreaseQuantity(product.id)
            refreshData(CartManager.getItems().toMutableList())
            onQuantityChanged()
        }

        holder.btnRemove.setOnClickListener {
            CartManager.removeItem(product.id)
            refreshData(CartManager.getItems().toMutableList())
            onQuantityChanged()
        }
    }

    fun refreshData(newList: MutableList<CartItem>) {
        cartItems = newList
        notifyDataSetChanged()
    }
}