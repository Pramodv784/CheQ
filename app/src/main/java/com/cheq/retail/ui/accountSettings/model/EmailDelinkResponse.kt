package com.cheq.retail.ui.accountSettings.model

import androidx.annotation.Keep

@Keep
data class EmailDelinkResponse(var data: DataEntity) {
    @Keep
    data class DataEntity(var isActive: Boolean)
}