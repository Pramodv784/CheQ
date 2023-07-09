package com.cheq.retail.inappratings.actions

import com.cheq.retail.api.EncryptionProvider
import com.cheq.retail.api.RestClient
import com.cheq.retail.inappratings.models.request.FeedbackRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Sanket Mendon on 22,June,2023.
 * sanket@cheq.one
 */
class S2SAction(url: String, requestbody: FeedbackRequest?) : BaseAction {
    private val api = RestClient.getInstance().service
    private var postUrl: String = url
    private var request: FeedbackRequest? = requestbody
    override fun execute() {
        request?.let {
            CoroutineScope(Dispatchers.IO).launch {
                api.submitFeedback(postUrl, EncryptionProvider(it))
            }
        }
    }
}