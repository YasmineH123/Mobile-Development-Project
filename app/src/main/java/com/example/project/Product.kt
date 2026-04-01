package com.example.project

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageRes: Int,    // drawable resource id
    val rating: Float,
    val isPopular: Boolean
)