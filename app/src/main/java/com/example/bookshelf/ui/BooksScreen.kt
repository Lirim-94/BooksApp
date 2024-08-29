package com.example.bookshelf.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.bookshelf.data.Book
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

@Composable
fun BookApp(
    windowSizeClass: WindowSizeClass,
    viewModel: BookshelfViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            BookshelfScreenCompact(
                uiState = uiState,
                onSearchBooks = { query -> viewModel.processIntent(BookshelfIntent.SearchBooks(query)) },
                onBookSelected = { bookId -> viewModel.processIntent(BookshelfIntent.SelectBook(bookId)) },
                onBackToList = { viewModel.processIntent(BookshelfIntent.BackToList) },
                onScrollPositionChanged = { position -> viewModel.processIntent(BookshelfIntent.UpdateScrollPosition(position)) }
            )
        }
        WindowWidthSizeClass.Medium -> {
            BookshelfScreenMedium(
                uiState = uiState,
                onSearchBooks = { query -> viewModel.processIntent(BookshelfIntent.SearchBooks(query)) },
                onBookSelected = { bookId -> viewModel.processIntent(BookshelfIntent.SelectBook(bookId)) },
                onBackToList = { viewModel.processIntent(BookshelfIntent.BackToList) },
                onScrollPositionChanged = { position -> viewModel.processIntent(BookshelfIntent.UpdateScrollPosition(position)) }
            )
        }
        WindowWidthSizeClass.Expanded -> {
            BookshelfScreenExpanded(
                uiState = uiState,
                onSearchBooks = { query -> viewModel.processIntent(BookshelfIntent.SearchBooks(query)) },
                onBookSelected = { bookId -> viewModel.processIntent(BookshelfIntent.SelectBook(bookId)) },
                onBackToList = { viewModel.processIntent(BookshelfIntent.BackToList) },
                onScrollPositionChanged = { position -> viewModel.processIntent(BookshelfIntent.UpdateScrollPosition(position)) }
            )
        }
    }
}

@Composable
fun BookshelfScreenCompact(
    uiState: BooksUiState,
    onSearchBooks: (String) -> Unit,
    onBookSelected: (Int) -> Unit,
    onBackToList: () -> Unit,
    onScrollPositionChanged: (Int) -> Unit
) {
    Column {
        SearchBar(
            onSearch = onSearchBooks,
            modifier = Modifier.fillMaxWidth()
        )
        when (uiState) {
            is BooksUiState.Loading -> LoadingIndicator()
            is BooksUiState.Success -> BookGrid(
                books = uiState.books,
                onBookSelected = onBookSelected,
                columns = 2,
                modifier = Modifier.weight(1f),
                initialScrollPosition = uiState.scrollPosition,
                onScrollPositionChanged = onScrollPositionChanged
            )
            is BooksUiState.Error -> ErrorMessage(uiState.message)
            is BooksUiState.BookDetails -> BookDetailsScreen(
                book = uiState.book,
                onBackPressed = onBackToList
            )
        }
    }
}

@Composable
fun BookshelfScreenMedium(
    uiState: BooksUiState,
    onSearchBooks: (String) -> Unit,
    onBookSelected: (Int) -> Unit,
    onBackToList: () -> Unit,
    onScrollPositionChanged: (Int) -> Unit
) {
    Row {
        SearchBar(
            onSearch = onSearchBooks,
            modifier = Modifier.weight(1f)
        )
        when (uiState) {
            is BooksUiState.Loading -> LoadingIndicator()
            is BooksUiState.Success -> BookGrid(
                books = uiState.books,
                onBookSelected = onBookSelected,
                columns = 3,
                modifier = Modifier.weight(2f),
                initialScrollPosition = uiState.scrollPosition,
                onScrollPositionChanged = onScrollPositionChanged
            )
            is BooksUiState.Error -> ErrorMessage(uiState.message)
            is BooksUiState.BookDetails -> BookDetailsScreen(
                book = uiState.book,
                onBackPressed = onBackToList,
                modifier = Modifier.weight(2f)
            )
        }
    }
}

@Composable
fun BookshelfScreenExpanded(
    uiState: BooksUiState,
    onSearchBooks: (String) -> Unit,
    onBookSelected: (Int) -> Unit,
    onBackToList: () -> Unit,
    onScrollPositionChanged: (Int) -> Unit
) {
    Column {
        SearchBar(
            onSearch = onSearchBooks,
            modifier = Modifier.fillMaxWidth()
        )
        when (uiState) {
            is BooksUiState.Loading -> LoadingIndicator()
            is BooksUiState.Success -> BookGrid(
                books = uiState.books,
                onBookSelected = onBookSelected,
                columns = 4,
                modifier = Modifier.weight(1f),
                initialScrollPosition = uiState.scrollPosition,
                onScrollPositionChanged = onScrollPositionChanged
            )
            is BooksUiState.Error -> ErrorMessage(uiState.message)
            is BooksUiState.BookDetails -> BookDetailsScreen(
                book = uiState.book,
                onBackPressed = onBackToList
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    book: Book,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Book Details") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = book.title,
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = book.coverImageUrl,
                        contentDescription = book.title,
                        modifier = Modifier.size(200.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = book.description ?: "No description available",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SearchBar(
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Search books") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch(text)
                    keyboardController?.hide()
                }
            ),
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 56.dp)
                .onPreviewKeyEvent {
                    if (it.key == Key.Enter) {
                        onSearch(text)
                        keyboardController?.hide()
                        true
                    } else {
                        false
                    }
                },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = {
                onSearch(text)
                keyboardController?.hide()
            },
            modifier = Modifier.height(56.dp)
        ) {
            Text("Search")
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun BookGrid(
    books: List<Book>,
    onBookSelected: (Int) -> Unit,
    columns: Int,
    initialScrollPosition: Int = 0,
    onScrollPositionChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val gridState = rememberLazyGridState(
        initialFirstVisibleItemIndex = initialScrollPosition
    )

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.firstVisibleItemIndex }
            .collect { onScrollPositionChanged(it) }
    }

    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
    ) {
        items(books) { book ->
            BookCard(
                book = book,
                onClick = { book.id?.let { onBookSelected(it) } }
            )
        }
    }
}

@Composable
fun ErrorMessage(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, color = Color.Red)
    }
}

@Composable
fun BookCard(book: Book, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
            .fillMaxWidth()
            .aspectRatio(0.67f)
    ) {
        Column {
            AsyncImage(
                model = book.coverImageUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentScale = ContentScale.Crop,
            )
            Text(
                text = book.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}