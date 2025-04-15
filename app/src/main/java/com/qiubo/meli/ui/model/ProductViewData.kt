package com.qiubo.meli.ui.model

data class ProductViewData(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val pictures: List<String> = emptyList()
)
