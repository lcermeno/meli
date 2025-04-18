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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch
import androidx.compose.material3.rememberDrawerState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    stateProvider: DashboardStateProvider,
    navigateToProduct: (String) -> Unit
) {
    val searchQuery by stateProvider.searchQuery.collectAsState()
    val items = stateProvider.pagedProducts.collectAsLazyPagingItems()
    val uiState by stateProvider.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val username by stateProvider.username.collectAsState()


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

    DashboardDrawer(
        username = username,
        onLogout = { scope.launch { stateProvider.logout() } },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = stringResource(R.string.menu)
                            )
                        }
                    }
                )
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .padding(
                            bottom = WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding() + 8.dp
                        )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding(),
                    start = 0.dp,
                    end = 0.dp
                )
            ) {
                SearchView(searchQuery, stateProvider)
                ContentView(
                    items = items,
                    uiState = uiState,
                    navigateToProduct = navigateToProduct,
                    onRetry = { stateProvider.retry() },
                    onRefresh = { stateProvider.refresh() })
            }
        }
    }
}

@Composable
private fun ContentView(
    items: LazyPagingItems<DashboardUiItem>,
    uiState: UiFeedback,
    navigateToProduct: (String) -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
) {
    val loadState = items.loadState.refresh
    val isLoading = loadState is LoadState.Loading && uiState !is UiFeedback.Error
    val isError = loadState is LoadState.Error || uiState is UiFeedback.Error
    val isRefreshing = uiState is UiFeedback.Loading

    Progress(isLoading)

    when {
        isError -> {
            RetryView {
                onRetry()
            }
        }

        loadState is LoadState.NotLoading -> {
            ProductList(items, navigateToProduct, onRefresh, isRefreshing)
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
            .padding(horizontal = 16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            shape = RoundedCornerShape(50.dp),
            onValueChange = stateProvider::updateSearchQuery,
            placeholder = { Text(stringResource(R.string.search)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ProductList(
    items: LazyPagingItems<DashboardUiItem>,
    navigateToProduct: (String) -> Unit,
    onRefresh: () -> Unit,
    isRefreshing: Boolean
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items.itemCount) { index ->
                items[index]?.let { item ->
                    when (item) {
                        is DashboardUiItem.ProductItem -> ProductView(
                            navigateToProduct,
                            item.product
                        )

                        is DashboardUiItem.MessageItem -> MessageView(item.message)
                    }
                }
            }
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
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

@Composable
fun DashboardDrawer(
    username: String,
    onLogout: () -> Unit,
    drawerState: DrawerState,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = username,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                NavigationDrawerItem(
                    label = { Text(text = stringResource(R.string.logout)) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            onLogout()
                            drawerState.close()
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = stringResource(R.string.logout)
                        )
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        },
        content = content
    )
}
