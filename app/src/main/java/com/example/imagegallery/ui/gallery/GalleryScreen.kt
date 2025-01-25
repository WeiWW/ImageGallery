package com.example.imagegallery.ui.gallery

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.imagegallery.data.common.TestTags
import com.example.imagegallery.ui.login.LoginScreen
import com.example.imagegallery.ui.previewdata.GalleryUiStatePreviewProvider
import com.example.imagegallery.ui.theme.ImageGalleryTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    GalleryScreen(
        uiState,
        onRefresh = { viewModel.fetchImages() },
        onUpdateAuthState = { viewModel.updateAuthState(it) },
        onUpLoadImage = { context, uri -> viewModel.uploadImage(context, uri) })
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GalleryScreen(
    uiState: GalleryUiState,
    onUpdateAuthState: (Boolean) -> Unit,
    onUpLoadImage: (Context, Uri) -> Unit,
    onRefresh: () -> Unit
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri.value = uri
    }
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .testTag(TestTags.GALLERY_SCREEN),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.testTag(TestTags.FAB_ADD_IMAGE)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Image")
            }
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) {
        uiState.error?.let { error ->
            LaunchedEffect(error, snackBarHostState) {
                snackBarHostState.showSnackbar(error, duration = SnackbarDuration.Short)
            }
        }

        if (uiState.hasAuthError) {
            Dialog(
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                ),
                onDismissRequest = {}
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .testTag(TestTags.DIALOG_LOGIN),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    LoginScreen(onLoginSuccess = { onUpdateAuthState(false) })
                }
            }
        }

        selectedImageUri.value?.let { uri ->
            LaunchedEffect(uri) {
                onUpLoadImage(context, uri)
                selectedImageUri.value = null
            }
        }

        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = onRefresh,
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize().testTag(TestTags.IMAGE_GRID)
            ) {
                items(uiState.images) { image ->
                    AsyncImage(
                        model = image.url,
                        contentDescription = image.caption,
                        alignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .border(1.dp, Color.LightGray)
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun GalleryScreenPreview(
    @PreviewParameter(GalleryUiStatePreviewProvider::class) uiState: GalleryUiState
) {
    ImageGalleryTheme {
        GalleryScreen(
            uiState,
            onUpdateAuthState = {},
            onUpLoadImage = { _, _ -> },
            onRefresh = {}
        )
    }
}