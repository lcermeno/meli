package com.qiubo.meli.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.qiubo.meli.routes.Route
import com.qiubo.meli.ui.dashboard.DashboardScreen
import com.qiubo.meli.ui.dashboard.DashboardViewModel
import com.qiubo.meli.ui.login.LoginScreen
import com.qiubo.meli.ui.product.ProductScreen
import com.qiubo.meli.ui.product.ProductViewModel
import com.qiubo.meli.ui.splash.SplashScreen

fun NavGraphBuilder.loadingScreenNavigation(
    innerPadding: PaddingValues,
) {
    composable<Route.Splash> {
        SplashScreen(
            modifier = Modifier.padding(innerPadding)
        )
    }
}

fun NavGraphBuilder.loginScreenNavigation(
    innerPadding: PaddingValues
) {
    composable<Route.Login> {
        LoginScreen(
            modifier = Modifier.padding(innerPadding),
        )
    }
}

fun NavGraphBuilder.dashboardScreenNavigation(
    innerPadding: PaddingValues,
    onNavigateToProduct: (String) -> Unit
) {
    composable<Route.Dashboard> {
        val viewModel = hiltViewModel<DashboardViewModel>()
        DashboardScreen(
            stateProvider = viewModel,
            navigateToProduct = onNavigateToProduct
        )
    }
}

fun NavGraphBuilder.productScreenNavigation(innerPadding: PaddingValues) {
    composable<Route.Product> {
        val viewModel = hiltViewModel<ProductViewModel>()
        ProductScreen(
            modifier = Modifier.padding(innerPadding),
            stateProvider = viewModel
        )
    }
}

