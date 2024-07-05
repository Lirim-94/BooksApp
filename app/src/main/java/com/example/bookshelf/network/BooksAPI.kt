package com.example.bookshelf.network

import com.example.bookshelf.data.Book
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object GoogleBooksApi {

    private val client: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                }
            )
        }

    }
        suspend fun searchBooks(query: String) : Result<List<Book>> {
            return try {
                val response: BooksResponse = client.get("https://www.googleapis.com/books/v1/volumes") {
                    parameter("q", query)
                    parameter("maxResults", "40")
                }.body()
                val books = response.items?.mapNotNull { item ->
                    item.volumeInfo?.let {info ->
                        Book(
                            id = item.id,
                            title = info.title,
                            authors = info.authors ?: emptyList(),
                            coverImageUrl = info.imageLinks?.thumbnail?.replace("http:", "https:")
                                            ?: info.imageLinks?.smallThumbnail?.replace("http:", "https:")
                        )

                    }
                } ?: emptyList()

                Result.success(books)

            }
            catch(e: Exception) {
                Result.failure(e)
            }
        }

    @Serializable
    data class BooksResponse(
        val items: List<VolumeItem>? = null
    )

    @Serializable
    data class VolumeItem(
        val id: String,
        val volumeInfo: VolumeInfo?=null
    )

    @Serializable
    data class VolumeInfo(
        val title: String,
        val authors: List<String>? = null,
        val imageLinks: ImageLinks? = null
    )

    @Serializable
    data class ImageLinks(
        val thumbnail: String? = null,
        val smallThumbnail: String? = null
    )

}