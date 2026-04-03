package com.example.project

object CartManager {

    private val cartItems = mutableListOf<CartItem>()

    fun addItem(product: Product) {
        val existing = cartItems.find { it.product.id == product.id }
        if (existing != null) {
            existing.quantity++
        } else {
            cartItems.add(CartItem(product))
        }
    }

    fun removeItem(productId: Int) {
        cartItems.removeAll { it.product.id == productId }
    }

    fun increaseQuantity(productId: Int) {
        cartItems.find { it.product.id == productId }?.quantity++
    }

    fun decreaseQuantity(productId: Int) {
        val item = cartItems.find { it.product.id == productId }
        if (item != null) {
            if (item.quantity > 1) {
                item.quantity--
            } else {
                cartItems.remove(item)
            }
        }
    }

    fun getItems(): List<CartItem> = cartItems.toList()

    fun getItemCount(): Int = cartItems.sumOf { it.quantity }

    fun getSubtotal(): Double = cartItems.sumOf { it.totalPrice }

    fun getDeliveryFee(): Double = if (cartItems.isEmpty()) 0.0 else 2.99

    fun getTotal(): Double = getSubtotal() + getDeliveryFee()

    fun clear() = cartItems.clear()

    fun isEmpty(): Boolean = cartItems.isEmpty()
}