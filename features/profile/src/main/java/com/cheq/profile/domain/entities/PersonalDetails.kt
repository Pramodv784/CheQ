package com.cheq.profile.domain.entities

import android.os.Parcelable
import androidx.annotation.Keep
import com.cheq.profile.data.models.UserSettingsResponse
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Created by Sanket Mendon on 18,May,2023.
 * sanket@cheq.one
 */
@Keep
@Parcelize
data class PersonalDetails(
    val initials: String,
    val firstName: String,
    val lastName: String,
    val fullname: String,
    val mobile: String,
    val email: String,
    val activeEmails: @RawValue List<ActiveEmails>
) : Parcelable {
    constructor(firstName: String, lastName: String, mobile: String, email: String, activeEmails: List<ActiveEmails>) :
            this(
                initials = "${firstName[0]}${lastName[0]}".uppercase(),
                firstName = firstName,
                lastName = lastName,
                fullname = "$firstName $lastName",
                mobile = "+91 $mobile",
                email = email,
                activeEmails = activeEmails
            )

    data class ActiveEmails(
        val id: String,
        val email: String,
        val emailLinkingStatus: EmailLinkingStatus,
        val firstName: String,
        val lastName: String,
    )
}
