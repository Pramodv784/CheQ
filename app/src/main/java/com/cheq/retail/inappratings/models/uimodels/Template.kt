package com.cheq.retail.inappratings.models.uimodels

import android.os.Parcelable


/**
 * Created by Sanket Mendon on 21,June,2023.
 * sanket@cheq.one
 */
abstract class Template : Parcelable {
    abstract val type: String?
    abstract val title: String?
    abstract val action: Template?
    abstract val actions: List<Template>?
}