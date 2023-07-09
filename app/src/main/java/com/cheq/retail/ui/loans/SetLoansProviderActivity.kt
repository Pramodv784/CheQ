package com.cheq.retail.ui.loans

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivitySetLoansProviderBinding
import com.cheq.retail.extensions.faqsBaseUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.accountSettings.webView.CommonWebViewActivity
import com.cheq.retail.ui.loans.adapter.LoanProviderAdapter
import com.cheq.retail.ui.loans.model.Loan2
import com.cheq.retail.ui.loans.viewmodel.LoanProviderViewModel
import com.cheq.retail.utils.IntentKeys
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils

class SetLoansProviderActivity : BaseActivity() {
    lateinit var viewModel: LoanProviderViewModel
    lateinit var mBinding: ActivitySetLoansProviderBinding
    var loansList = ArrayList<Loan2>()
    var allProviderList = ArrayList<Loan2>()
    lateinit var LoanListWithoutTopSix: ArrayList<Loan2>
    lateinit var loanProviderAdapter: LoanProviderAdapter
    lateinit var prefs: SharePrefs
    private var rewardsCanAssign = 0
    private var rewardsAssigned = 0
    private var rewardsAssignUpto = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        catchIntent()
        setupUi()
        setupViewModel()
        setupObserver()


    }

    private fun catchIntent() {
        rewardsAssignUpto = intent.getIntExtra(IntentKeys.REWARDS_ASSIGN_UPTO, 0)
        rewardsCanAssign = intent.getIntExtra(IntentKeys.REWARDS_CAN_ASSIGN, 0)
        rewardsAssigned = intent.getIntExtra(IntentKeys.REWARDS_ASSIGNED, 0)
    }


    private fun setupObserver() {
        viewModel.statusObserver.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.progressObserver.observe(this) {
            if (it) {
                Utils.showProgressDialog(this@SetLoansProviderActivity)
            } else {
                Utils.hideProgressDialog()
            }
        }

        viewModel.loanProviderObserver.observe(this, Observer {
            if (it != null) {
                allProviderList.clear()
                if (it.topProviders != null) {
                    allProviderList.addAll(it.topProviders)
                }
                    if (it.providers != null)
                        allProviderList.addAll(it.providers)
                    mBinding.rvBankList.adapter = LoanProviderAdapter(
                        ::onLoanClicked,
                        this,
                        allProviderList
                    )
                }
        })
    }


    private fun onSearchFocused(): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                mBinding.llSearch.setBackgroundResource(R.drawable.et_btm_bg)
            } else {
                mBinding.llSearch.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
            }
        }
    }

    fun search(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) {
                if (s.isEmpty()) {

                    mBinding.rvBankList.adapter = LoanProviderAdapter(
                        ::onLoanClicked,
                        this@SetLoansProviderActivity,
                        allProviderList as ArrayList<Loan2>,

                    )

                } else {
                    val tempList: ArrayList<Loan2> = ArrayList()
                    for (element in allProviderList) {
                        if (element.billerName.lowercase()
                                .contains(s.toString().lowercase())
                        ) {
                            tempList.add(element)
                        }
                    }



                    if (tempList.isNotEmpty()) {
                        mBinding.rvBankList.adapter =
                            LoanProviderAdapter(
                                ::onLoanClicked,
                                this@SetLoansProviderActivity,
                                tempList,
                            )
                    }

                    MparticleUtils.logEvent(
                        "/loan-activation/select-lender",
                        "User searches from amongst the available lenders using the search option",
                        "Unique",
                        "Input Field",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Loan_Activation_Enter_Detail),
                        this@SetLoansProviderActivity
                    )
                }
            }

            override fun afterTextChanged(editable: Editable) {
                //filter(editable.toString())
            }

        }
    }


    override fun onResume() {
        super.onResume()
        //  Utils.showProgressDialog(this)
        loansList.clear()
        viewModel.getLoanList()
    }


    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[LoanProviderViewModel::class.java]

    }

    fun onBackPress(view: View) {
        onBackPressed()
        finish()
    }

    private fun setupUi() {
        prefs = SharePrefs.getInstance(this)!!
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_set_loans_provider)
        mBinding.activity = this
        mBinding.etSearch.onFocusChangeListener = onSearchFocused()

        mBinding.selectLoanProviderHelpButton.setOnClickListener {
            help()
        }

        MparticleUtils.logCurrentScreen(
            "/loan-addition/select-lender",
            "The customer is asked to select a loan provider for addition to their credit portfolio on CheQ",
            "",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.LOAN_SELECT_LENDER),
            this
        )

    }

    fun help() {
        val url = "${prefs.faqsBaseUrl}${getString(R.string.loan_faq)}"
        startActivity(
            Intent(
                this@SetLoansProviderActivity,
                CommonWebViewActivity::class.java
            ).apply {
                putExtra("URL", url)
            })
    }


    private fun onLoanClicked(loan: Loan2) {
        startActivity(
            Intent(this, DetailsForLoanActivity::class.java).putExtra(
                "LOAN_PROVIDER",
                loan
            ).putExtra("from", "loan_provider")
                .putExtra(IntentKeys.REWARDS_ASSIGN_UPTO, rewardsAssignUpto)
                .putExtra(IntentKeys.REWARDS_CAN_ASSIGN, rewardsCanAssign)
                .putExtra(IntentKeys.REWARDS_ASSIGNED, rewardsAssigned)


        )

        MparticleUtils.logCurrentScreen(
            "/loan-activation/select-lender",
            "The customer is asked to select a loan provider for addition to their credit portfolio on CheQ",
            "",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Loan_Activation_Select_Lender),
            this@SetLoansProviderActivity
        )

        MparticleUtils.logEvent(
            "",
            "User selects their lender for loan activation",
            "Not Unique",
            "Continue",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.Loan_Activation_Select_Lender),
            this@SetLoansProviderActivity
        )
    }
}