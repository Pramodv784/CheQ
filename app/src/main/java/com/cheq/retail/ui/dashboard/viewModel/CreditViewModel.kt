package com.cheq.retail.ui.dashboard.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cheq.retail.ui.dashboard.model.creditDashBoard.CreditDashBoardResponse
import com.cheq.retail.ui.dashboard.repository.CreditRepository
import com.cheq.retail.ui.rewards.util.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreditViewModel(private val creditRepository: CreditRepository) : ViewModel() {


    val creditDashBoard: LiveData<State<CreditDashBoardResponse>> get() = creditRepository.crediteDashBoardLiveData
   // val creditDashBoardProduct: LiveData<State<CreditDashBoardProductResponse>> get() = creditRepository.crediteDashBoardProductLiveData
    fun getDashBoard() {
        viewModelScope.launch(Dispatchers.IO) {
            creditRepository.getDashBoard()
        }
    }
//    fun getDashBoardProduct() {
//        viewModelScope.launch(Dispatchers.IO) {
//            creditRepository.getDashBoardCreditProduct()
//        }
//    }
}