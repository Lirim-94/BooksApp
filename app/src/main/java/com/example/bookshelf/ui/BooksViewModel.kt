package com.example.bookshelf.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshelf.data.Book
import com.example.bookshelf.network.GoogleBooksApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BooksUiState {
    object Loading : BooksUiState()
    data class Success(val books: List<Book>) : BooksUiState()
    data class Error(val message: String) : BooksUiState()

}

@HiltViewModel
class BookshelfViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<BooksUiState>(BooksUiState.Loading)
    val uiState: StateFlow<BooksUiState> = _uiState.asStateFlow()

    init {
        loadRandomBooks()
    }

    fun loadRandomBooks() {
        viewModelScope.launch {
            _uiState.value = BooksUiState.Loading
            val result = GoogleBooksApi.searchBooks(getRandomSearchTerm())
            _uiState.value = result.fold(
                onSuccess = { BooksUiState.Success(it) },
                onFailure = { BooksUiState.Error(it.message ?: "Unknown error occurred") }
            )
        }
    }

    fun searchBooks(query: String) {
        viewModelScope.launch {
            _uiState.value = BooksUiState.Loading
            val result = GoogleBooksApi.searchBooks(query)
            _uiState.value = result.fold(
                onSuccess = { BooksUiState.Success(it) },
                onFailure = { BooksUiState.Error(it.message ?: "Unknown error occurred") }
            )
        }
    }

    private fun getRandomSearchTerm(): String {
        val terms = listOf("fiction", "science", "history", "art", "technology", "philosophy")
        return terms.random()
    }
}