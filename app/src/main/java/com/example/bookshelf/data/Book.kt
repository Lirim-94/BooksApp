package com.example.bookshelf.data

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: Int? = null,
    val title: String,
    val author: String,
    val coverImageUrl: String?,
    val description: String?
)

