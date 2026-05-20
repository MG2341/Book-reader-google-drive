package com.example.bookreader.library.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.bookreader.library.data.model.LibraryItem
import com.example.bookreader.library.ui.state.Breadcrumb
import com.example.bookreader.library.ui.state.LibraryUiState
import com.example.bookreader.library.ui.theme.LibraryColorTokens
import java.time.Duration
import java.time.Instant
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

/**
 * Main library screen.
 * Renders different content based on ViewModel state (Loading/Success/Error).
 *
 * @param viewModel Provides library UI state
 * @param onOpenReader Callback when user selects a document to read
 */
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    onOpenReader: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LibraryColorTokens.Background,
    ) {
        when (val state = uiState) {
            is LibraryUiState.Loading -> LoadingState()
            is LibraryUiState.Error -> ErrorState(state.message)
            is LibraryUiState.Success -> LibraryContent(
                state = state,
                onCollectionClick = { viewModel.onCollectionClicked(it) },
                onBreadcrumbClick = { viewModel.onBreadcrumbClicked(it) },
                onDocumentClick = { onOpenReader(it.id) },
            )
        }
    }
}
 
/** Shows spinner while data loads. */
@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LibraryColorTokens.Background),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = LibraryColorTokens.Accent)
    }
}

/** Shows error message when load fails. */
@Composable
private fun ErrorState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LibraryColorTokens.Background)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = LibraryColorTokens.SecondaryText,
        )
    }
}

/**
 * Main content: breadcrumbs + scrollable list of files/folders.
 * Handles click routing to ViewModel.
 */
@Composable
private fun LibraryContent(
    state: LibraryUiState.Success,
    onCollectionClick: (LibraryItem) -> Unit,
    onBreadcrumbClick: (Breadcrumb) -> Unit,
    onDocumentClick: (LibraryItem) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LibraryColorTokens.Background),
    ) {
        MinimalTopBar(
            currentCollectionName = state.currentCollectionName,
            breadcrumbs = state.breadcrumbs,
            onBreadcrumbClick = onBreadcrumbClick,
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp, 0.dp, 24.dp, 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            items(
                items = state.items,
                key = { item -> item.id },
            ) { item ->
                if (item.isCollection()) {
                    CollectionRow(item = item, onClick = onCollectionClick)
                } else if (item.isDocument()) {
                    DocumentRow(item = item, onClick = onDocumentClick)
                }
            }
        }
    }
}

/**
 * Header: breadcrumb navigation + folder name.
 * Breadcrumbs are clickable for quick navigation back.
 */
@Composable
private fun MinimalTopBar(
    currentCollectionName: String,
    breadcrumbs: List<Breadcrumb>,
    onBreadcrumbClick: (Breadcrumb) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp, 28.dp, 24.dp, 18.dp),
    ) {
        if (breadcrumbs.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                breadcrumbs.forEachIndexed { index, crumb ->
                    Text(
                        text = crumb.label,
                        modifier = Modifier.clickable { onBreadcrumbClick(crumb) },
                        style = MaterialTheme.typography.labelMedium,
                        color = LibraryColorTokens.SecondaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (index != breadcrumbs.size - 1) {
                        Text(
                            text = "  /  ",
                            style = MaterialTheme.typography.labelMedium,
                            color = LibraryColorTokens.SecondaryText,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Text(
            text = currentCollectionName,
            style = MaterialTheme.typography.headlineMedium,
            color = LibraryColorTokens.PrimaryText,
        )
    }
}

/** Visual row for a folder. Indicator + title. */
@Composable
private fun CollectionRow(
    item: LibraryItem,
    onClick: (LibraryItem) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(item) }
            .padding(4.dp, 0.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(14.dp)
                .height(1.dp)
                .background(LibraryColorTokens.Accent),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            color = LibraryColorTokens.Accent,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Visual row for a document.
 * Shows title, reading progress bar, and last-opened time.
 */
@Composable
private fun DocumentRow(
    item: LibraryItem,
    onClick: (LibraryItem) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(item) }
            .padding(0.dp, 2.dp),
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleLarge,
            color = LibraryColorTokens.PrimaryText,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(8.dp))

        val progress = item.readingProgressPercent
            ?.let { Math.min(100, Math.max(0, it)) }
            ?: 0
        LinearProgressIndicator(
            progress = progress / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp),
            color = LibraryColorTokens.Accent,
            trackColor = LibraryColorTokens.ProgressTrack,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Last opened ${item.lastReadAt.toRelativeTime()}",
            style = MaterialTheme.typography.labelMedium,
            color = LibraryColorTokens.SecondaryText,
        )
    }
}

/** Converts an Instant to relative time string (e.g., "2 min ago", "1 d ago"). */
private fun Instant?.toRelativeTime(): String {
    if (this == null) return "never"
    val now = Instant.now()
    if (isAfter(now)) return "just now"
    val elapsed = Duration.between(this, now)

    val minutes = elapsed.toMinutes()
    val hours = elapsed.toHours()
    val days = elapsed.toDays()

    return when {
        minutes < 1 -> "just now"
        minutes < 60 -> "$minutes min ago"
        hours < 24 -> "$hours h ago"
        days < 7 -> "$days d ago"
        else -> "${days / 7} wk ago"
    }
}
