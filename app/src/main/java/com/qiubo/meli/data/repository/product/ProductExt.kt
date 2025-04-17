package com.qiubo.meli.data.repository.product

import com.qiubo.meli.common.normalizeUrl
import com.qiubo.meli.data.remote.model.ItemDetail

internal fun ItemDetail.normalizeUrls(): ItemDetail {
    return copy(
        thumbnail = thumbnail.normalizeUrl()
    )
}