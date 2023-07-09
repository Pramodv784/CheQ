package com.cheq.retail.ui.landing.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheq.retail.ui.landing.model.*
import com.cheq.retail.ui.login.model.UpdateProfileResponse
import com.cheq.retail.utils.ImageUtils.bankLogo
import com.cheq.retail.utils.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LandingActivityViewModel : ViewModel() {


    val view = MutableLiveData<Resource<LandingEntity>>(Resource.Initial(null))

    private val _event = Channel<LandingEvent>()
    val event = _event.receiveAsFlow()

    fun loadData(
        creditResponse: UpdateProfileResponse.CreditResponse?, userName: String
    ) {
        view.postValue(Resource.Pending(null))
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
            t.printStackTrace()
            view.postValue(Resource.Error(t))
        }) {

            if (creditResponse != null) {

                val loansClubbed = mutableMapOf<String, List<LandingProduct.Loan>>()
                val cardsClubbed = mutableMapOf<String, List<LandingProduct.Card>>()

                creditResponse.products?.forEach {

                    it.bankMasterRecord?.bankName?.let { bankName ->
                        val list = (cardsClubbed[bankName] ?: emptyList()).toMutableList()

                        list.add(
                            LandingProduct.Card(
                                last4Digits = it.last4 ?: "",
                                id = it._id ?: "",
                                imageUrl = it.bankMasterRecord.id.bankLogo,
                            )
                        )

                        cardsClubbed[bankName] = list
                    }
                }

                creditResponse.loans?.forEach {
                    it.bankName.let { institutionName ->
                        val list = (loansClubbed[institutionName] ?: emptyList()).toMutableList()
                        list.add(
                            LandingProduct.Loan(
                                id = it.id ?: "",
                                number = it.productNumber ?: "",
                                imageUrl = it.id ?: ""
                            )
                        )
                    }
                }

                val products = mutableListOf<LandingProductCategory>()

                cardsClubbed.entries.forEach {
                    products.add(
                        LandingProductCategory(
                            title = it.key,
                            imageUrl = it.value.first().imageUrl ?: "",
                            products = it.value,
                            type = LandingProductType.CARD
                        )
                    )
                }

                loansClubbed.entries.forEach {
                    products.add(
                        LandingProductCategory(
                            title = it.key,
                            imageUrl = it.value.first().imageUrl,
                            products = it.value,
                            type = LandingProductType.LOAN
                        )
                    )
                }

                view.postValue(
                    Resource.Loaded(
                        LandingEntity(
                            name = userName,
                            creditScore = if (creditResponse.creditScore == null || creditResponse.creditScore == 0) {
                                null
                            } else {
                                CreditScore(
                                    score = creditResponse.creditScore,
                                    provider = creditResponse.bureauContext,
                                    leadingHtml = creditResponse.bureauReport?.getDescriptionAsHtml
                                )
                            },
                            products = products
                        )
                    )
                )


            } else {
                view.postValue(
                    Resource.Loaded(
                        LandingEntity(
                            name = userName,
                            creditScore = null,
                            products = emptyList()
                        )
                    )
                )
            }
        }


    }

    fun doExploreCheq() {

        viewModelScope.launch {
            _event.send(LandingEvent.DoExploreCheq)
        }

    }
}