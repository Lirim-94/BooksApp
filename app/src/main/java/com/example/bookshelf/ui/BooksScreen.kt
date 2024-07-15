package com.example.bookshelf.ui



import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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


@Composable
fun BookApp(
    windowSizeClass: androidx.compose.material3.windowsizeclass.WindowSizeClass,
    viewModel: BookshelfViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    when (windowSizeClass.widthSizeClass) {
        androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Compact -> {
            BookshelfScreenCompact(
                uiState = uiState,
                onSearchBooks = { query -> viewModel.processIntent(BookshelfIntent.SearchBooks(query))},
                onBookSelected = { bookId -> viewModel.processIntent(BookshelfIntent.SelectBook(bookId))},
                onBackTolist = {viewModel.processIntent(BookshelfIntent.BackToList)},
                onScrollPositionChanged = { position -> viewModel.processIntent(BookshelfIntent.UpdateScrollPosition(position))}

            )
        }
        androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Medium -> {
            BookshelfScreenMedium(
                uiState = uiState,
                onSearchBooks = { query -> viewModel.processIntent(BookshelfIntent.SearchBooks(query))},
                onBookSelected = { bookId -> viewModel.processIntent(BookshelfIntent.SelectBook(bookId))},
                onBackTolist = {viewModel.processIntent(BookshelfIntent.BackToList)},
                onScrollPositionChanged = { position -> viewModel.processIntent(BookshelfIntent.UpdateScrollPosition(position))}
            )
        }
        androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Expanded -> {
            BookshelfScreenExpanded(
                uiState = uiState,
                onSearchBooks = { query -> viewModel.processIntent(BookshelfIntent.SearchBooks(query))},
                onBookSelected = { bookId -> viewModel.processIntent(BookshelfIntent.SelectBook(bookId))},
                onBackTolist = {viewModel.processIntent(BookshelfIntent.BackToList)},
                onScrollPositionChanged = { position -> viewModel.processIntent(BookshelfIntent.UpdateScrollPosition(position))}
            )
        }
    }
}


@Composable
fun BookshelfScreenCompact(
    uiState: BooksUiState,
    onSearchBooks: (String) -> Unit,
    onBookSelected: (String) -> Unit,
    onBackTolist: () -> Unit,
    onScrollPositionChanged: (Int) -> Unit,
) {
    Column {
        SearchBar(
            onSearch = onSearchBooks,
            modifier = Modifier.weight(1f)
        )
        when (uiState) {
            is BooksUiState.Loading -> LoadingIndicator()
            is BooksUiState.Success -> BookGrid(
                books = uiState.books,
                onBookSelected = onBookSelected,
                columns = 2,
                modifier = Modifier,
                initialScrollPosition = uiState.scrollPosition,
                onScrollPositionChanged = onScrollPositionChanged
            )
            is BooksUiState.Error -> ErrorMessage(uiState.message)
            is BooksUiState.BookDetails -> BookDetailsScreen(
                book = uiState.book,
                onBackPressed = onBackTolist
            )
        }
    }
}

@Composable
fun BookshelfScreenMedium(
    uiState: BooksUiState,
    onSearchBooks: (String) -> Unit,
    onBookSelected: (String) -> Unit,
    onBackTolist: () -> Unit,
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
                columns =3,
                modifier = Modifier.weight(2f),
                initialScrollPosition = uiState.scrollPosition,
                onScrollPositionChanged = onScrollPositionChanged
            )
            is BooksUiState.Error -> ErrorMessage(uiState.message)
            is BooksUiState.BookDetails -> BookDetailsScreen(
                book = uiState.book,
                onBackPressed = onBackTolist,
                modifier = Modifier.weight(2f)
            )
        }
    }
}

@Composable
fun BookshelfScreenExpanded(
    uiState: BooksUiState,
    onSearchBooks: (String) -> Unit,
    onBookSelected: (String) -> Unit,
    onBackTolist: () -> Unit,
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
                modifier = Modifier,
                initialScrollPosition = uiState.scrollPosition,
                onScrollPositionChanged = onScrollPositionChanged
            )
            is BooksUiState.Error -> ErrorMessage(uiState.message)
            is BooksUiState.BookDetails -> BookDetailsScreen(
                book = uiState.book,
                onBackPressed = onBackTolist
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
                    text = book.authors.joinToString(),
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
fun SearchBar(onSearch: (String) -> Unit, modifier: Modifier) {
    var text by remember { mutableStateOf("") }
    val viewModel: BookshelfViewModel = hiltViewModel()
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier
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
                onSearch = { viewModel.searchBooks(text)
                    if (keyboardController != null) {
                        keyboardController.hide()
                    }
                }
            ),

            modifier = Modifier
                .weight(1f)
                .heightIn(min = 56.dp)
                .onPreviewKeyEvent {
                    if (it.key == Key.Enter) {
                        viewModel.searchBooks(text)
                        if (keyboardController != null) {
                            keyboardController.hide()
                        }
                        true
                    } else {
                        false
                    }
                }
                ,
            colors =OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            colors = ButtonDefaults.buttonColors(
              containerColor =  MaterialTheme.colorScheme.primary,
              contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            onClick = {
                onSearch(text)
                if (keyboardController != null) {
                    keyboardController.hide()
                }
                      },
            modifier = Modifier
                .height(56.dp)) {
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
    onBookSelected: (String) -> Unit,
    columns: Int,
    initialScrollPosition: Int = 0,
    onScrollPositionChanged: (Int) -> Unit,
    modifier: Modifier) {

    val gridState = rememberLazyGridState(
        initialFirstVisibleItemIndex = initialScrollPosition
    )

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.firstVisibleItemIndex }
            .collect {onScrollPositionChanged(it)}

    }
    LazyVerticalGrid(
        state = gridState,
        modifier = modifier,
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(books) { book ->
            BookCard(
                book = book,
                onClick = {
                    onBookSelected(book.id)
                }
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
fun BookCard(book: Book, onClick: () -> Unit ) {
    Card(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
            .fillMaxWidth()
            .aspectRatio(0.67f) // maintain aspect ratio for different screen sizes
    ) {
        Column {
            AsyncImage(
                model = book.coverImageUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), //take up available space
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