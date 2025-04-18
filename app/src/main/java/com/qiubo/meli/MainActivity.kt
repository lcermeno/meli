package com.qiubo.meli

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.qiubo.meli.common.UiFeedback
import com.qiubo.meli.routes.Route
import com.qiubo.meli.ui.navigation.dashboardScreenNavigation
import com.qiubo.meli.ui.navigation.splashScreenNavigation
import com.qiubo.meli.ui.navigation.loginScreenNavigation
import com.qiubo.meli.ui.navigation.productScreenNavigation
import com.qiubo.meli.ui.theme.MeliTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MeliTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }

                val route by viewModel.navigationFlow.collectAsState()
                val uiState by viewModel.uiState.collectAsState()

                LaunchedEffect(Unit) {
                    handleDeepLinkIntent(intent)
                }

                LaunchedEffect(route) {
                    navigateTo(route, navController)
                }

                LaunchedEffect(uiState) {
                    when (val state = uiState) {
                        is UiFeedback.Error -> {
                            snackbarHostState.showSnackbar(getString(state.message))
                            viewModel.clearUiState()
                        }

                        is UiFeedback.Feedback -> {
                            snackbarHostState.showSnackbar(getString(state.message))
                            viewModel.clearUiState()
                        }

                        else -> Unit
                    }
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },

                ) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = Route.Splash
                    ) {
                        splashScreenNavigation(innerPadding)
                        loginScreenNavigation(innerPadding)
                        dashboardScreenNavigation { productId ->
                            navController.navigate(
                                Route.Product(
                                    productId = productId
                                )
                            )
                        }
                        productScreenNavigation(innerPadding)
                    }
                }
            }
        }
    }

    private fun navigateTo(
        route: Route,
        navController: NavHostController
    ) {

        when (route) {
            Route.Login,
            Route.Dashboard -> navController.navigate(route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
            is Route.Product -> navController.navigate(route)
            Route.Splash -> Timber.d("Splash Screen")
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLinkIntent(intent)
    }

    private fun handleDeepLinkIntent(intent: Intent?) {
        val uri = intent?.data
        if (uri?.scheme == SCHEMA_KEY && uri.host == HOST_KEY) {
            val code = uri.getQueryParameter(CODE_KEY)
            if (!code.isNullOrEmpty()) {
                Timber.d("AuthCode", "Received code: $code")
                viewModel.onAuthorizationCodeReceived(code)
            }
        }
    }

    companion object {
        private const val SCHEMA_KEY = "qiubo"
        private const val HOST_KEY = "auth"
        private const val CODE_KEY = "code"
    }
}
