package com.qiubo.meli

import com.qiubo.meli.routes.Route
import kotlinx.coroutines.flow.StateFlow

interface MainViewProvider {
    val navigationFlow: StateFlow<Route>
}