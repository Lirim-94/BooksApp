package com.example.bookshelf.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshelf.data.Book
import com.example.bookshelf.network.BookApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookshelfViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<BooksUiState>(BooksUiState.Loading)
    val uiState: StateFlow<BooksUiState> = _uiState.asStateFlow()

    private var lastSuccessState: BooksUiState.Success? = null
    private var lastScrollPosition = 0

    init {
        loadAllBooks()
    }

    fun loadAllBooks() {
        viewModelScope.launch {
            _uiState.value = BooksUiState.Loading
            try {
                val result = BookApiService.getAllBooks()
                _uiState.value = result.fold(
                    onSuccess = { BooksUiState.Success(it) },
                    onFailure = { BooksUiState.Error(it.message ?: "Unknown error occurred") }
                )
            } catch (e: Exception) {
                _uiState.value = BooksUiState.Error("Failed to load books: ${e.message}")
            }
        }
    }

    fun searchBooks(query: String) {
        viewModelScope.launch {
            _uiState.value = BooksUiState.Loading
            val result = BookApiService.searchBooks(query)
            _uiState.value = result.fold(
                onSuccess = {
                    val successState = BooksUiState.Success(it)
                    lastSuccessState = successState
                    successState
                },
                onFailure = { BooksUiState.Error(it.message ?: "Unknown error occurred") }
            )
        }
    }

    fun addBook(book: Book) {
        viewModelScope.launch {
            _uiState.value = BooksUiState.Loading
            val result = BookApiService.addBook(book)
            result.onSuccess { loadAllBooks() }
                .onFailure { _uiState.value = BooksUiState.Error(it.message ?: "Failed to add book") }
        }
    }

    fun updateBook(id: Int, book: Book) {
        viewModelScope.launch {
            _uiState.value = BooksUiState.Loading
            val result = BookApiService.updateBook(id, book)
            result.onSuccess { loadAllBooks() }
                .onFailure { _uiState.value = BooksUiState.Error(it.message ?: "Failed to update book") }
        }
    }

    fun deleteBook(id: Int) {
        viewModelScope.launch {
            _uiState.value = BooksUiState.Loading
            val result = BookApiService.deleteBook(id)
            result.onSuccess { loadAllBooks() }
                .onFailure { _uiState.value = BooksUiState.Error(it.message ?: "Failed to delete book") }
        }
    }

    fun getBookDetails(id: Int) {
        viewModelScope.launch {
            _uiState.value = BooksUiState.Loading
            val result = BookApiService.getBookById(id)
            _uiState.value = result.fold(
                onSuccess = { BooksUiState.BookDetails(it) },
                onFailure = { BooksUiState.Error(it.message ?: "Failed to get book details") }
            )
        }
    }

    fun updateScrollPosition(position: Int) {
        lastScrollPosition = position
    }

    fun processIntent(intent: BookshelfIntent) {
        when (intent) {
            is BookshelfIntent.SearchBooks -> searchBooks(intent.query)
            is BookshelfIntent.SelectBook -> getBookDetails(intent.bookId)
            is BookshelfIntent.BackToList -> backToList()
            is BookshelfIntent.UpdateScrollPosition -> updateScrollPosition(intent.position)
            is BookshelfIntent.AddBook -> addBook(intent.book)
            is BookshelfIntent.UpdateBook -> updateBook(intent.id, intent.book)
            is BookshelfIntent.DeleteBook -> deleteBook(intent.id)
        }
    }

    private fun backToList() {
        lastSuccessState?.let { _uiState.value = it.copy(scrollPosition = lastScrollPosition) }
            ?: loadAllBooks()
    }
}

sealed class BookshelfIntent {
    data class SearchBooks(val query: String) : BookshelfIntent()
    data class SelectBook(val bookId: Int) : BookshelfIntent()
    object BackToList : BookshelfIntent()
    data class UpdateScrollPosition(val position: Int) : BookshelfIntent()
    data class AddBook(val book: Book) : BookshelfIntent()
    data class UpdateBook(val id: Int, val book: Book) : BookshelfIntent()
    data class DeleteBook(val id: Int) : BookshelfIntent()
}

sealed class BooksUiState {
    object Loading : BooksUiState()
    data class Success(val books: List<Book>, val scrollPosition: Int = 0) : BooksUiState()
    data class Error(val message: String) : BooksUiState()
    data class BookDetails(val book: Book) : BooksUiState()
}