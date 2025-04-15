package com.qiubo.meli.ui.product

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.qiubo.meli.common.UiFeedback

@Composable
fun ProductScreen(
    modifier: Modifier = Modifier,
    stateProvider: ProductStateProvider,
) {
    val product = stateProvider.product.collectAsState().value
    val uiFeedback = stateProvider.uiFeedback.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiFeedback) {
        when (uiFeedback) {
            is UiFeedback.Error -> {
                snackbarHostState.showSnackbar(context.getString(uiFeedback.message))
                stateProvider.clearUiFeedback()
            }

            is UiFeedback.Feedback -> {
                snackbarHostState.showSnackbar(context.getString(uiFeedback.message))
                stateProvider.clearUiFeedback()
            }

            else -> Unit
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (product != null) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (product.pictures.isNotEmpty()) {
                    val pagerState = rememberPagerState(pageCount = { product.pictures.size })

                    HorizontalPager(state = pagerState) { index ->
                        AsyncImage(
                            model = product.pictures[index],
                            contentDescription = product.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = product.name,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
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
