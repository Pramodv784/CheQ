package com.cheq.retail.api.errormodel

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class BlockData(
    val image: String?,
    val primaryCta: PrimaryCta?,
    val secondaryCta: SecondaryCta?,
    val subtitle: String?,
    val title: String?,
    val type: String?
) : Parcelable

@Keep
@kotlinx.parcelize.Parcelize
data class EmailMetadata(
    val body: String?,
    val receipientAddress: String?,
    val subject: String?
) : Parcelable

@Keep
@Parcelize
data class Metadata(
    val action: String,
    val actionType: String,
    val emailMetadata: EmailMetadata?
) : Parcelable

@Keep
@Parcelize
data class MetadataX(
    val action: String?,
    val actionType: String?,
    val emailMetadata: EmailMetadata?
) : Parcelable

@Keep
@Parcelize
data class PrimaryCta(
    val metadata: Metadata?,
    val title: String?,
    val visible: Boolean?
) : Parcelable

@Keep
@Parcelize
data class SecondaryCta(
    val description: String?,
    val image: String?,
    val metadata: MetadataX?,
    val visible: Boolean?
) : Parcelable