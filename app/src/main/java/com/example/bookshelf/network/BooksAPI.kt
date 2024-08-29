package com.example.bookshelf.network

import com.example.bookshelf.data.Book
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object BookApiService {
    private const val BASE_URL = "http://10.0.2.2:8080/api/books"


    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }

    suspend fun searchBooks(query: String): Result<List<Book>> {
        return try {
            val response: List<Book> = client.get("$BASE_URL/search") {
                parameter("query", query)
            }.body()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllBooks(): Result<List<Book>> {
        return try {
            val response: List<Book> = client.get(BASE_URL).body()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch books: ${e.message}"))
        }
    }

    suspend fun getBookById(id: Int): Result<Book> {
        return try {
            val response: Book = client.get("$BASE_URL/$id").body()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addBook(book: Book): Result<Book> {
        return try {
            val response: Book = client.post(BASE_URL) {
                contentType(ContentType.Application.Json)
                setBody(book)
            }.body()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBook(id: Int, book: Book): Result<Book> {
        return try {
            val response: Book = client.put("$BASE_URL/$id") {
                contentType(ContentType.Application.Json)
                setBody(book)
            }.body()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBook(id: Int): Result<Unit> {
        return try {
            client.delete("$BASE_URL/$id")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}