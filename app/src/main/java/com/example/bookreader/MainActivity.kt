package com.example.bookreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.bookreader.library.ui.LibraryScreen
import com.example.bookreader.library.ui.LibraryViewModel

/**
 * Main activity entry point.
 * Sets up the ViewModel and launches the library screen with Compose.
 */
class MainActivity : ComponentActivity() {

    private val fakeRepo = FakeLibraryRepository()
    private val viewModelFactory = LibraryViewModel.Factory(fakeRepo)
    private val viewModel: LibraryViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BookReaderTheme {
                LibraryScreen(
                    viewModel = viewModel,
                    onOpenReader = { fileId ->
                        // TODO: Navigate to ReaderScreen with fileId
                    },
                )
            }
        }
    }

    /** Wraps content with Material Design theme. */
    @Composable
    private fun BookReaderTheme(content: @Composable () -> Unit) {
        MaterialTheme(content = content)
    }
}
