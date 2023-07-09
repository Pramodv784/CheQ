package com.cheq.retail.ui.loans.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable
@Keep
data class CustomerParam(
    @field:SerializedName("cheqParam")
    val cheqParam: String,
    @field:SerializedName("dataType")
    val dataType: String,
    @field:SerializedName("maxLength")
    val maxLength: Int,
    @field:SerializedName("minLength")
    val minLength: Int,
    @field:SerializedName("optional")
    val optional: String,
    @field:SerializedName("paramName")
    val paramName: String,
    @field:SerializedName("regex")
    val regex: String?,
    @field:SerializedName("requiredIn")
    val requiredIn: String,

    @field:SerializedName("value")
    var value: String?,
    @field:SerializedName("values")
    var values: String?,
    @field:SerializedName("visibility")
    val visibility: Boolean

): Serializable