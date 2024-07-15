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
    data class Success(val books: List<Book>, val scrollPosition: Int = 0) : BooksUiState()
    data class Error(val message: String) : BooksUiState()
    data class BookDetails(val book: Book) : BooksUiState()

}

sealed class BookshelfIntent {
    data class SearchBooks(val query: String) : BookshelfIntent()
    data class SelectBook(val bookId: String) : BookshelfIntent()
    object BackToList : BookshelfIntent()
    data class UpdateScrollPosition(val position: Int) : BookshelfIntent()
}
@HiltViewModel
class BookshelfViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<BooksUiState>(BooksUiState.Loading)
    val uiState: StateFlow<BooksUiState> = _uiState.asStateFlow()

    private var currentQuery: String? = null
    private var lastSuccessState: BooksUiState.Success? = null
    private var lastScrollPosition = 0

    fun updateScrollPosition(position: Int) {
        lastScrollPosition = position
    }

    fun processIntent(intent: BookshelfIntent) {
        when (intent) {
            is BookshelfIntent.SearchBooks -> searchBooks(intent.query)
            is BookshelfIntent.SelectBook -> selectBook(intent.bookId)
            is BookshelfIntent.BackToList -> backToList()
            is BookshelfIntent.UpdateScrollPosition -> updateScrollPosition(intent.position)
        }
    }
    private fun selectBook(bookId: String) {
        val currentState = _uiState.value

        if (currentState is BooksUiState.Success) {
            val selectedBook = currentState.books.find { it.id == bookId }
            if (selectedBook != null ) {
                _uiState.value = BooksUiState.BookDetails(selectedBook)
            }
        }
    }

    private fun backToList() {
        if (lastSuccessState != null) {
            _uiState.value = BooksUiState.Success(lastSuccessState!!.books, lastScrollPosition)
        } else if (currentQuery != null) {
            searchBooks(currentQuery!!)
        } else
        {
            loadRandomBooks()
        }
    }

    init {
        loadRandomBooks()
    }

    fun loadRandomBooks() {
        viewModelScope.launch {
            _uiState.value = BooksUiState.Loading
            val result = GoogleBooksApi.searchBooks(getRandomSearchTerm())
            _uiState.value = result.fold(
                onSuccess = {
                    val succesState = BooksUiState.Success(it)
                    lastSuccessState = succesState
                    succesState
                            },
                onFailure = { BooksUiState.Error(it.message ?: "Unknown error occurred") }
            )
        }
    }

    fun searchBooks(query: String) {
        currentQuery  = query
        if (query.isBlank()) {
            loadRandomBooks()
        } else {
            viewModelScope.launch {
                _uiState.value = BooksUiState.Loading
                val result = GoogleBooksApi.searchBooks(query)
                _uiState.value = result.fold(
                    onSuccess = {
                        val succesState = BooksUiState.Success(it)
                        lastSuccessState = succesState
                        succesState
                                },
                    onFailure = { BooksUiState.Error(it.message ?: "Unknown error occurred") }
                )
            }
        }
    }

    private fun getRandomSearchTerm(): String {
        val terms = listOf("fiction", "science", "history", "art", "technology", "philosophy", "sports", "food", "motivation", "peace")
        return terms.random()
    }
}