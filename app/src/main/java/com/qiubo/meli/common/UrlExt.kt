package com.qiubo.meli.common

fun String.normalizeUrl(): String = replace("http://", "https://")