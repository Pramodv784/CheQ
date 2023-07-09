package com.cheq.profile.data.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Akash Khatkale on 17th May, 2023.
 * akash.k@cheq.one
 */
data class TransactionHistoryDetailRequest(
    @SerializedName("bill_id") val billId: String?,
    @SerializedName("cheq_uni_txn_id") val cheqUniTxnId: String?
)
