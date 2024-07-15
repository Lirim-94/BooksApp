package com.example.bookshelf.data

data class Book(
    val id: String,
    val title: String,
    val authors: List<String>,
    val coverImageUrl: String?,
    val description: String?
)

