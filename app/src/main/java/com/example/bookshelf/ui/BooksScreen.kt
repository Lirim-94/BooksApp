package com.example.bookshelf.ui



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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowWidthSizeClass
import coil.compose.AsyncImage
import com.example.bookshelf.data.Book


@Composable
fun BookApp(windowSizeClass: androidx.compose.material3.windowsizeclass.WindowSizeClass) {
    val viewModel: BookshelfViewModel = hiltViewModel()

    when (windowSizeClass.widthSizeClass) {
        androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Compact -> {
            BookshelfScreenCompact(viewModel = viewModel)
        }
        androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Medium -> {
            BookshelfScreenMedium(viewModel = viewModel)
        }
        androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Expanded -> {
            BookshelfScreenExpanded(viewModel = viewModel)
        }
    }
}


@Composable
fun BookshelfScreenCompact(viewModel: BookshelfViewModel) {

    Column {
        SearchBar(
            onSearch = { query -> viewModel.searchBooks(query)},
            weight = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        BookGrid(
            viewModel = viewModel ,
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize()
        )

    }
}

@Composable
fun BookshelfScreenMedium(viewModel: BookshelfViewModel) {

    Column {
        SearchBar(
            onSearch = { query -> viewModel.searchBooks(query)},
            weight = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        BookGrid(
            viewModel = viewModel ,
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize()
        )
    }

}

@Composable
fun BookshelfScreenExpanded(viewModel: BookshelfViewModel) {

    Column {
        SearchBar(
            onSearch = { query -> viewModel.searchBooks(query)},
            weight = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        BookGrid(
            viewModel = viewModel ,
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxSize()
        )
    }

}

@Composable
fun SearchBar(onSearch: (String) -> Unit, weight: Modifier) {
    var text by remember { mutableStateOf("") }

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
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 56.dp),
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
            onClick = { onSearch(text) },
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
    viewModel: BookshelfViewModel,
    columns: GridCells,
    modifier: Modifier = Modifier
)
{
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is BooksUiState.Loading -> LoadingIndicator()
        is BooksUiState.Success -> {
            LazyVerticalGrid(
                columns = columns,
                modifier = modifier,
                contentPadding = PaddingValues(16.dp)
            ) {
                items(state.books) { book ->
                    BookCard(book = book)
                }
            }
        }
        is BooksUiState.Error -> ErrorMessage(message = state.message)
    }
}

@Composable
fun ErrorMessage(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, color = Color.Red)
    }
}

@Composable
fun BookCard(book: Book) {
    Card(
        modifier = Modifier
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
                contentScale = ContentScale.Crop
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