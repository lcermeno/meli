package com.qiubo.meli.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.qiubo.meli.R
import com.qiubo.meli.common.UiFeedback
import com.qiubo.meli.ui.model.ProductViewData
import com.qiubo.meli.ui.theme.YellowML

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

            when {
                items.loadState.refresh is LoadState.Loading && uiState !is UiFeedback.Error -> {
                    MessageView(R.string.loading)
                }
                uiState is UiFeedback.Error || items.loadState.refresh is LoadState.Error -> {
                    RetryView {
                        stateProvider.clearUiState()
                        stateProvider.retry()
                    }
                }
                items.loadState.refresh is LoadState.NotLoading -> {
                    ProductList(items, navigateToProduct)
                }
            }
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
@Suppress("DEPRECATION")
fun ApplyStatusBarStyle() {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = true
    val backgroundColor = YellowML

    SideEffect {
        systemUiController.setStatusBarColor(
            color = backgroundColor,
            darkIcons = useDarkIcons
        )
    }
}
