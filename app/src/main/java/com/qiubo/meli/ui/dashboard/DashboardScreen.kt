package com.qiubo.meli.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.qiubo.meli.R
import com.qiubo.meli.common.UiFeedback
import com.qiubo.meli.ui.compose.components.ApplyStatusBarStyle
import com.qiubo.meli.ui.model.ProductViewData

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    stateProvider: DashboardStateProvider,
    navigateToProduct: (String) -> Unit
) {
    val searchQuery by stateProvider.searchQuery.collectAsState()
    val items = stateProvider.pagedProducts.collectAsLazyPagingItems()
    val uiState by stateProvider.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    ApplyStatusBarStyle()

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is UiFeedback.Error -> {
                snackbarHostState.showSnackbar(context.getString(state.message))
            }

            is UiFeedback.Feedback -> {
                snackbarHostState.showSnackbar(context.getString(state.message))
                stateProvider.clearUiState()
            }

            else -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = modifier.fillMaxSize()) {

            SearchView(searchQuery, stateProvider)
            ContentView(items, uiState, navigateToProduct) { stateProvider.retry() }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    bottom = WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding() + 8.dp
                )
        )
    }
}

@Composable
private fun ContentView(
    items: LazyPagingItems<DashboardUiItem>,
    uiState: UiFeedback,
    navigateToProduct: (String) -> Unit,
    onRetry: () -> Unit,
) {
    val loadState = items.loadState.refresh
    val isLoading = loadState is LoadState.Loading && uiState !is UiFeedback.Error
    val isError = loadState is LoadState.Error || uiState is UiFeedback.Error

    Progress(isLoading)

    when {
        isError -> {
            RetryView {
                onRetry()
            }
        }

        loadState is LoadState.NotLoading -> {
            ProductList(items, navigateToProduct)
        }
    }
}


@Composable
private fun SearchView(
    searchQuery: String,
    stateProvider: DashboardStateProvider
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(0.dp)
            )
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            shape = RoundedCornerShape(50.dp),
            onValueChange = stateProvider::updateSearchQuery,
            placeholder = { Text(stringResource(R.string.search)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                    bottom = 16.dp
                ),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                disabledContainerColor = Color.White
            )
        )
    }
}

@Composable
private fun ProductList(
    items: LazyPagingItems<DashboardUiItem>,
    navigateToProduct: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(items.itemCount) { index ->
            items[index]?.let { item ->
                when (item) {
                    is DashboardUiItem.ProductItem -> ProductView(navigateToProduct, item.product)
                    is DashboardUiItem.MessageItem -> MessageView(item.message)
                }
            }
        }
    }
}

@Composable
private fun ProductView(
    navigateToProduct: (String) -> Unit,
    product: ProductViewData
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navigateToProduct(product.id) },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = product.name)
    }
}

@Composable
private fun MessageView(message: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(message))
    }
}

@Composable
fun RetryView(
    message: String = stringResource(R.string.something_went_wrong),
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text(text = stringResource(R.string.retry))
            }
        }
    }
}

@Composable
fun Progress(show: Boolean) {
    if (show) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )
        }
    }
}

