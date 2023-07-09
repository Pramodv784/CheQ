package com.cheq.retail.ui.main.fragment

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cheq.retail.R
import com.cheq.retail.api.errormodel.BlockData
import com.cheq.retail.constants.AFConstants.AMERICAN_CARD
import com.cheq.retail.constants.AFConstants.CUSTOM
import com.cheq.retail.constants.AFConstants.DINERS_CARD
import com.cheq.retail.constants.AFConstants.FULL
import com.cheq.retail.constants.AFConstants.MASTER_CARD
import com.cheq.retail.constants.AFConstants.MIN
import com.cheq.retail.constants.AFConstants.RUPAY_CARD
import com.cheq.retail.constants.AFConstants.VISA
import com.cheq.retail.databinding.*
import com.cheq.retail.extensions.bankMasterUrl
import com.cheq.retail.extensions.faqsBaseUrl
import com.cheq.retail.extensions.loanMasterUrl
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.accountSettings.webView.CommonWebViewActivity
import com.cheq.retail.ui.activateCard.CardDetailsActivityNew
import com.cheq.retail.ui.billPayments.PaymentMethodsActivity
import com.cheq.retail.ui.billPayments.PaymentMethodsActivity.SupportedUPIApps.Companion.DEFAULT_PERCENTAGE
import com.cheq.retail.ui.blocker_screen.BlockerActivity
import com.cheq.retail.ui.loans.DetailsForLoanActivity
import com.cheq.retail.ui.loans.model.Loan2
import com.cheq.retail.ui.loans.viewmodel.LoanProviderViewModel
import com.cheq.retail.ui.login.modelv2.productv1.ProductV2
import com.cheq.retail.ui.main.MainActivity
import com.cheq.retail.ui.main.ProductBottomSheetDialog
import com.cheq.retail.ui.main.adapter.CombineBIllAdapter
import com.cheq.retail.ui.main.adapter.LoansAdapter
import com.cheq.retail.ui.main.adapter.ProductsAdapterNew
import com.cheq.retail.ui.main.helper.MonthlyEarnRule
import com.cheq.retail.ui.main.model.HideProductRequest
import com.cheq.retail.ui.main.model.SelectOfferResponseItem
import com.cheq.retail.ui.main.viewModel.ProductViewModel
import com.cheq.retail.utils.*
import com.cheq.retail.utils.ImageUtils.loadSvg
import com.cheq.retail.utils.Utils.Companion.getRewardRate
import com.cheq.retail.utils.Utils.Companion.roundupAmount
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*


open class ProductFragment : Fragment(), ProductAdapterListener,
    CombineBIllAdapter.ProductClickListener {
    private var activity: MainActivity? = null
    lateinit var mBinding: FragmentProductsBinding
    private var viewModel: ProductViewModel? = null
    lateinit var viewModelLoan: LoanProviderViewModel
    private var ccCount = 0
    private var loansCount = 0
    private var total = 0.0
    private var totalDue = 0.0
    var isProductsActive = ObservableBoolean(false)
    private var isToggle = ObservableBoolean(false)
    var showTotal = ObservableBoolean(false)
    private var rewardsPoint = 0
    private var itemRemoved = -1
    private val toggleProductList: ArrayList<ProductV2> = ArrayList()
    private val mainProductList: ArrayList<ProductV2> = ArrayList()
    private var topBankList: List<Loan2> = ArrayList()
    private lateinit var combineBIllAdapter: CombineBIllAdapter

    private var billStatus = FULL
    private var isPayTogether = false
    private var isPartialPayTogether = false
    private var toggleEnable: Boolean = false
    private var prefs: SharePrefs? = null
    private var imageUrl = ""
    private var mFirebaseDatabase: DatabaseReference? = null
    private var mFirebaseInstance: FirebaseDatabase? = null
    private var fbRewardsRateList: List<SelectOfferResponseItem>? = null

    /**
     * how much user can earn more this month
     */
    private var rewardsCanAssign = 0

    /**
     * what user has already earned this month
     */
    private var rewardsAssigned = 0
    var selectedOfferList: HashMap<String, String>? = null

    /**
     * global CheQ Chips limit for the month. This is the overall static limit that will remain same across all users
     */
    private var rewardsAssignUpto = 0
    private val getResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == ACTIVITY_INTENT_CODE) {
            mainProductList.clear()
            viewModel?.readDeviceSms()
        }
    }

    companion object {
        const val ACTIVITY_INTENT_CODE = 28
        const val TAG = "ProductFragment"
        const val ONE_PERCENT = "1.0%"
        const val PAY_URL = "pay-tab.html"
        const val URL = "URL"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_products, container, false)
        mBinding.activity = this
        mFirebaseInstance =
            FirebaseDatabase.getInstance(com.cheq.retail.BuildConfig.firebaseDataBaseURL)
        mFirebaseDatabase = mFirebaseInstance?.reference
        setupUI()
        setupViewModel()
        setupObserver()
        return mBinding.root
    }


    private fun setupObserver() {

        mainProductList.clear()
        toggleProductList.clear()
        viewModel?.PayTogatherObserver?.observe(viewLifecycleOwner) {
            mBinding.switchPayTogether.isChecked = it
        }
        mBinding.rvCombineBill.visibility = View.GONE
        viewModel?.errorBlockObserver?.observe(viewLifecycleOwner) { blockdata ->
            showErrorBlocker(blockdata)
        }

        viewModel?.dashboardObserver?.observe(viewLifecycleOwner) {
            mBinding.loading.visibility = View.GONE
            mBinding.rvProduct.layoutManager = LinearLayoutManager(activity)
            if (it?.products != null && it.products.size != 0) {
                mBinding.llAddCard.visibility = View.VISIBLE
                mBinding.tvAddLoan.visibility = View.GONE

                ccCount = it.products.size
                mBinding.tvCCCount.text = "$ccCount"
                mBinding.tvCard.text = if (it.products.size > 1) "Cards" else "Card"
                mBinding.tvCardTitle.text = if (it.products.size > 1) "CARDS" else "CARD"
            } else {

                mBinding.rvProduct.visibility = View.GONE
                mBinding.llAddCard.visibility = View.GONE
            }
            if (it?.loans != null && it.loans.size != 0) {
                mBinding.tvLoanText.visibility = View.VISIBLE
                loansCount = it.loans.size
                mBinding.tvLoanCount.text = "$loansCount"
                mBinding.tvLoan.text = if (it.loans.size > 1) "Loans" else "Loan"
                mBinding.tvLoanText.text = if (it.loans.size > 1) "LOANS" else "LOAN"
            } else {
                mBinding.llAddLoan.visibility = View.GONE
                mBinding.rvLoans.visibility = View.GONE
            }
            if (ccCount != 0 && loansCount != 0) {
                mBinding.llChipCard.visibility = View.VISIBLE
                mBinding.llChipLoan.visibility = View.VISIBLE
                mBinding.svChips.visibility = View.VISIBLE
                enableCardChip()

            } else {

                mBinding.llChipCard.visibility = View.GONE
                mBinding.llChipLoan.visibility = View.GONE
            }

            total = 0.0

            if (it?.products != null && it.products.size != 0 || it?.loans != null && it.loans.size != 0) {
                mainProductList.addAll(it.products)
                setCustomRewardRate()
                mBinding.rvProduct.adapter =
                    ProductsAdapterNew(requireContext(), this, mainProductList, Utils.itemList)

                for (i in it.products.indices) {
                    if (it.products[i].productStatus == "4" || it.products[i].productStatus == "3") {
                        total += it.products[i].bill?.amount_paid?.let { it1 ->
                            it.products[i].bill?.total_due?.minus(
                                it1
                            )
                        } ?: 0.0
                        toggleProductList.add(
                            ProductV2(
                                it.products[i].product_status,
                                it.products[i].bankmaster_id,
                                "",
                                "",
                                it.products[i].bill_repeat_count,
                                "",
                                it.products[i].cheq_user_id,
                                it.products[i].created_from,
                                it.products[i].customer_name,
                                it.products[i].id,
                                "",
                                it.products[i].iin,
                                it.products[i].bankMasterRecord,
                                "",
                                "",
                                it.products[i].is_enabled_for_autopay,
                                it.products[i].is_tokenized,
                                it.products[i].is_total_due_enabled,
                                it.products[i].last4,
                                it.products[i].card_network,
                                it.products[i].product_number,
                                it.products[i].bill,
                                it.products[i].product_type,
                                "",
                                it.products[i].productStatus,
                                (it.products[i].bill?.amount_paid?.let { it1 ->
                                    it.products[i].bill?.total_due?.minus(
                                        it1
                                    )
                                }) ?: 0.0,
                                roundupAmount(
                                    it.products[i].bill?.total_due ?: DEFAULT_PERCENTAGE,
                                    DEFAULT_PERCENTAGE,
                                    null,
                                    null,
                                    Utils.itemList
                                ),
                                (it.products[i].bill?.amount_paid?.let { it1 ->
                                    it.products[i].bill?.total_due?.minus(
                                        it1
                                    )
                                }) ?: 0.0,
                                it.products[i].bill?.amount_paid,
                                true,
                                if (it.products[i].bill?.total_due == 0.0) CUSTOM else FULL,
                                null,
                                "",
                                "",
                                1,
                                "",
                                getRewardRate(
                                    it.products[i].bankMasterRecord?.id, Utils.itemList
                                )
                            )
                        )
                    }

                }
                for (i in it.loans.indices) {
                    if (it.loans[i].productStatus == "4" || it.loans[i].productStatus == "3") {
                        total += it.loans[i].bill?.amount_paid?.let { it1 ->
                            it.loans[i].bill?.total_due?.minus(
                                it1
                            )
                        } ?: 0.0
                        toggleProductList.add(
                            ProductV2(
                                it.loans[i].product_status,
                                "",
                                "",
                                "",
                                it.loans[i].bill_repeat_count,
                                "",
                                it.loans[i].cheq_user_id,
                                it.loans[i].created_from,
                                it.loans[i].customer_name,
                                it.loans[i].id,
                                "",
                                "",
                                it.loans[i].bankMasterRecord,
                                it.loans[i].bankMasterRecord?.id ?: "",
                                "",
                                it.loans[i].is_enabled_for_autopay,
                                it.loans[i].is_tokenized,
                                it.loans[i].is_total_due_enabled,
                                it.loans[i].last4,
                                "",
                                it.loans[i].product_number,
                                it.loans[i].bill,
                                it.loans[i].product_type,
                                "",
                                it.loans[i].productStatus,
                                (it.loans[i].bill?.amount_paid?.let { it1 ->
                                    it.loans[i].bill?.total_due?.minus(
                                        it1
                                    )
                                }) ?: 0.0,
                                roundupAmount(
                                    it.loans[i].bill?.total_due ?: DEFAULT_PERCENTAGE,
                                    DEFAULT_PERCENTAGE,
                                    null,
                                    null,
                                    Utils.itemList
                                ),
                                (it.loans[i].bill?.amount_paid?.let { it1 ->
                                    it.loans[i].bill?.total_due?.minus(
                                        it1
                                    )
                                }) ?: 0.0,
                                it.loans[i].bill?.amount_paid,
                                true,
                                if (it.loans[i].bill?.total_due == 0.0) CUSTOM else FULL,
                                null,
                                "",
                                "",
                                2,
                                it.loans[i].bankMasterRecord?.id ?: "",
                                getRewardRate(
                                    it.loans[i].bankMasterRecord?.id, Utils.itemList
                                )
                            )
                        )


                    }

                }
                if (!toggleEnable) {
                    try {
                        toggleEnable = true

                            val pay: Boolean? = arguments?.getBoolean("pay")
                            if (pay != null) {
                                mBinding.switchPayTogether.isChecked = pay
                            }



                        MparticleUtils.logCurrentScreen(
                            "/pay-tab",
                            "The pay tab completely loads, and customer views the tab",
                            "pay-together",
                            "on",
                            "",
                            "",
                            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.PAY_TAB),
                            requireActivity()
                        )
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
                mBinding.llDataLayout.visibility = View.VISIBLE
                mBinding.llNotFound.visibility = View.GONE
                mBinding.tvNoProduct.visibility = View.GONE
                mBinding.llNoData.visibility = View.GONE
                mBinding.tvDueAmountTop.text = "₹" + Utils.getFormattedDecimal(it.totalDue)
                totalDue = it.totalDue
                mBinding.tvDueAmountTopTwo.text = "₹" + Utils.getFormattedDecimal(it.totalDue)
                if (it.totalDue > 0) {
                    mBinding.tvDueAmountTop.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.colorTextTitle
                        )
                    )
                    mBinding.tvDueAmountTopTwo.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.colorTextTitle
                        )
                    )
                    mBinding.llEarnInfo.visibility = View.VISIBLE
                } else {
                    mBinding.tvDueAmountTop.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.colorText
                        )
                    )
                    mBinding.tvDueAmountTopTwo.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.colorText
                        )
                    )
                    mBinding.llEarnInfo.visibility = View.GONE
                }
                if (mBinding.tvDueAmountTop.text.isNotEmpty()) {
                    showTotal.set(true)
                }
                for (element in it.products) {
                    if (element.product_status != null && element.product_status == "2") {
                        isProductsActive.set(true)
                        break
                    }
                }
                if (it.rewardPoints != 0) {
                    mBinding.tvRewardPoints.text =
                        Utils.getFormattedDecimal(it.rewardPoints.toDouble())
                } else {
                    mBinding.tvRewardPoints.text = "0"
                }

                if (toggleProductList.size > 1) {
                    mBinding.llPayTogether.visibility = View.VISIBLE
                    mBinding.llHeader.visibility = View.GONE
                } else {
                    mBinding.llPayTogether.visibility = View.GONE

                    MparticleUtils.logCurrentScreen(
                        "/pay-tab",
                        "The pay tab completely loads, and customer views the tab",
                        "pay-together",
                        "not-available",
                        "",
                        "",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.PAY_TAB),
                        requireActivity()
                    )
                    mBinding.llHeader.visibility = View.VISIBLE
                }
                if (it.loans.size != 0) {
                    mBinding.rvLoans.adapter = LoansAdapter(requireContext(), this, it.loans)
                }

            } else if (it?.loans != null && it.loans.size != 0) {
                mBinding.llAddCard.visibility = View.GONE
                mBinding.llDataLayout.visibility = View.VISIBLE
                mBinding.llNotFound.visibility = View.GONE
                mBinding.tvNoProduct.visibility = View.GONE
                mBinding.llNoData.visibility = View.GONE
                mBinding.llAddCard.visibility = View.VISIBLE
                mBinding.tvDueAmountTop.text = "₹${Utils.getFormattedDecimal(it.totalDue)}"
                if (mBinding.tvDueAmountTop.text.isNotEmpty()) {
                    showTotal.set(true)
                }
                if (it.rewardPoints != 0) {
                    mBinding.tvRewardPoints.text =
                        Utils.getFormattedDecimal(it.rewardPoints.toDouble())
                } else {
                    mBinding.tvRewardPoints.text = getString(R.string.str_zero)
                }

                mBinding.rvLoans.adapter = LoansAdapter(requireContext(), this, it.loans)

            } else {
                mBinding.llDataLayout.visibility = View.GONE
                mBinding.llNotFound.visibility = View.VISIBLE
                mBinding.llRewards.visibility = View.VISIBLE
                mBinding.tvNoProduct.visibility = View.VISIBLE
                mBinding.llNoData.visibility = View.VISIBLE
                mBinding.llAddCard.visibility = View.GONE
                mBinding.llAddLoan.visibility = View.GONE
                context?.let { it1 ->
                    MparticleUtils.logCurrentScreen(
                        "/onboarding/no-products-found",
                        "The customer is informed that no products were found on performing all backend functions",
                        "",
                        "",
                        "",
                        "",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.NO_PRODUCT_FOUND),
                        it1
                    )
                }
            }

            try {
                rewardsAssignUpto = it.rewardLimitManager.rewardsAssignUpto ?: 0
                rewardsAssigned = it.rewardLimitManager.rewardsAssigned ?: 0
                rewardsCanAssign = it.rewardLimitManager.rewardsCanAssign ?: 0
            } catch (e: Exception) {
                Log.e(TAG, "setupObserver: ${e.localizedMessage}")
            }
        }


        viewModel?.statusObserver?.observe(viewLifecycleOwner) {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
        }

        viewModel?.progressObserverGetDashBoard?.observe(viewLifecycleOwner) {
            if (it) {
                mBinding.loading.visibility = View.VISIBLE
            } else {
                mBinding.loading.visibility = View.GONE
            }

        }

        viewModel?.topBillersObserver?.observe(viewLifecycleOwner) {
            if (it != null) {
                topBankList = it
            }
        }

        viewModel?.hideProductObserver?.observe(this, Observer {
            when (it) {
                is NetworkResult.Loading -> {
                }
                is NetworkResult.Success -> {
                    if (it.data?.status == true && itemRemoved != -1) {
                        mainProductList.removeAt(itemRemoved)
                        mBinding.rvProduct.adapter?.notifyItemRemoved(itemRemoved)
                        showRemovedMsg(getString(R.string.your_product_removed))
                        if (mainProductList.isEmpty()) {
                            findNavController().navigate(R.id.nav_products_fragment)
                        }
                    } else
                        showRemovedMsg(getString(R.string.your_product_not_removed))
                }
                is NetworkResult.Error -> {
                    showRemovedMsg(getString(R.string.your_product_not_removed))
                }
            }
        })

    }

    private fun showErrorBlocker(blockedData: BlockData?) {
        val intent = Intent(getActivity(), BlockerActivity::class.java).apply {
            putExtra(BlockerActivity.EXTRA_BLOCKER, blockedData)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }
    private fun setCustomRewardRate() {
        for (item in mainProductList) {
            item.rewardRate = getRewardRate(item.bankMasterRecord?.id, Utils.itemList)
        }

    }

    override fun onAttach(_context: Context) {
        super.onAttach(_context)
        activity = _context as MainActivity
    }

    private fun setupUI() {
        prefs = SharePrefs.getInstance(requireActivity())
        activity?.let { Utils.setColorStatusBar(it) }
        mBinding.rvProduct.isNestedScrollingEnabled = false
        mBinding.rvLoans.setHasFixedSize(true)
        mBinding.llRewards.setOnClickListener {
            activity?.setPayScreen(3, false)
            activity?.setBottomTab(3)
        }
        mBinding.ivPT.setOnClickListener {
            openPTInfoDialog()
        }

        fbRewardsRateList = (getActivity() as MainActivity).getRewardsList()


        mBinding.rvProduct.layoutManager = LinearLayoutManager(activity)

        mBinding.btnAddProduct.setOnClickListener {
            context?.let { it1 ->
                MparticleUtils.logEvent(
                    "Onboarding_Add_New_Clicked",
                    "User opts to add a new product in a scenario where no products are identified\n",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_Add_New_Clicked),
                    it1
                )
            }


            try {
                activity?.let { it1 ->
                    ProductBottomSheetDialog.newInstance(it1, rewardsCanAssign, rewardsAssigned, rewardsAssignUpto).show(
                        childFragmentManager, ""
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        mBinding.btnAddProduct2.setOnClickListener {
            context?.let { it1 ->
                MparticleUtils.logEvent(
                    "Onboarding_Add_New_Clicked",
                    "User opts to add a new product in a scenario where no products are identified\n",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_Add_New_Clicked),
                    it1
                )
            }


            try {
                activity?.let { it1 ->
                    ProductBottomSheetDialog.newInstance(
                        it1, rewardsCanAssign, rewardsAssigned, rewardsAssignUpto
                    ).show(
                        childFragmentManager, ""
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        mBinding.tvAddProductBottom.setOnClickListener {
            context?.let { it1 ->
                MparticleUtils.logEvent(
                    "Onboarding_Add_New_Clicked",
                    "User opts to add a new product in a scenario where no products are identified\n",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_Add_New_Clicked),
                    it1
                )
            }


            try {
                activity?.let { it1 ->
                    ProductBottomSheetDialog.newInstance(it1, rewardsCanAssign, rewardsAssigned, rewardsAssignUpto).show(
                        childFragmentManager, ""
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        mBinding.switchPayTogether.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mBinding.rvCombineBill.visibility = View.VISIBLE
                mBinding.rvProduct.visibility = View.GONE
                mBinding.llAddCard.visibility = View.GONE
                mBinding.tvAddLoan.visibility = View.GONE
                mBinding.llAddButtonWidget.visibility = View.GONE
                MparticleUtils.logCurrentScreen(
                    "/pay-tab",
                    "The pay tab completely loads, and customer views the tab",
                    "pay-together",
                    "on",
                    "",
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.PAY_TAB),
                    requireActivity()
                )
                mBinding.llAddCard.visibility = View.GONE
                mBinding.llChild.visibility = View.GONE
                mBinding.svChips.visibility = View.GONE
                mBinding.llPaytogetherAnimation.visibility = View.VISIBLE
                mBinding.payTogetherAnimation.playAnimation()
                mBinding.tvHowItWork.visibility = View.VISIBLE
                mBinding.ivHelp.visibility = View.GONE
                mBinding.llRewards.visibility = View.GONE
                mBinding.tvCount.text = getString(R.string.pay_together)
                isToggle.set(true)
                isPayTogether = true
                combineBIllAdapter =
                    CombineBIllAdapter(requireContext(), this, toggleProductList, this)
                for (i in toggleProductList.indices) {
                    if (toggleProductList[i].bill != null && (toggleProductList[i].customAmount
                            ?: 0.0) > 0
                    ) {
                        toggleProductList[i].isChecked = true
                        toggleProductList[i].billStatus = FULL
                        combineBIllAdapter.notifyItemChanged(i)
                    } else {
                        toggleProductList[i].isChecked = false
                        combineBIllAdapter.notifyItemChanged(i)
                    }
                }

                if (combineBIllAdapter.getSelectedProduct().size != 0) {
                    activity?.setBtnVisibility(total, true, "")
                    if (combineBIllAdapter.getSelectedProduct().size > 1) {
                        activity?.mBinding?.btnPayTogether?.text =
                            getString(R.string.str_pay_together_now)

                    }

                } else {

                    activity?.setBtnVisibilityFull(true)
                }
                mBinding.rvCombineBill.adapter = combineBIllAdapter
                mBinding.tvDueAmountTopTwo.text = "₹${Utils.getFormattedDecimal(total)}"
                mBinding.rvLoans.visibility = View.GONE
                mBinding.tvLoanText.visibility = View.GONE
            } else {
                mBinding.rvCombineBill.visibility = View.GONE
                mBinding.llAddButtonWidget.visibility = View.VISIBLE
                MparticleUtils.logCurrentScreen(
                    "/pay-tab",
                    "The pay tab completely loads, and customer views the tab",
                    "pay-together",
                    "off",
                    "",
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.PAY_TAB),
                    requireActivity()
                )
                isPayTogether = false
                mBinding.llAddCard.visibility = View.VISIBLE
                mBinding.rvLoans.visibility = View.VISIBLE
                mBinding.tvLoanText.visibility = View.VISIBLE
                mBinding.llPaytogetherAnimation.visibility = View.GONE
                mBinding.tvHowItWork.visibility = View.GONE
                mBinding.ivHelp.visibility = View.VISIBLE
                mBinding.svChips.visibility = View.VISIBLE
                mBinding.llRewards.visibility = View.VISIBLE
                mBinding.payTogetherAnimation.cancelAnimation()
                mBinding.tvCount.text = getString(R.string.manage)
                isToggle.set(false)
                activity?.setBtnVisibilityFull(true)
                toggleProductList.clear()
                mainProductList.clear()
                viewModel?.readDeviceSms()
                setCustomRewardRate()
                mBinding.rvProduct.adapter =
                    ProductsAdapterNew(requireContext(), this, mainProductList, Utils.itemList)
                mBinding.tvDueAmountTopTwo.text = "₹${Utils.getFormattedDecimal(totalDue)}"
            }
        }
        mBinding.animationView.setMaxFrame(380)
        mBinding.animationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                mBinding.btnAddProduct.visibility = View.VISIBLE
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationStart(animation: Animator) {
            }
        })



        mBinding.payTogetherAnimation.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                mBinding.llPaytogetherAnimation.visibility = View.GONE
                mBinding.llChild.visibility = View.VISIBLE

            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationStart(animation: Animator) {
            }
        })
        mBinding.tvHowItWork.setOnClickListener {

            startActivity(Intent(activity, CommonWebViewActivity::class.java).apply {
                putExtra(URL, "${prefs?.faqsBaseUrl}${getString(R.string.pay_together_faq)}")
            })
        }
        mBinding.tvAddProduct.setOnClickListener {
            context?.let { it1 ->
                MparticleUtils.logEvent(
                    "Onboarding_Add_New_Clicked",
                    "User opts to add a new product in a scenario where no products are identified\n",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_Add_New_Clicked),
                    it1
                )
            }


            // showAddBottomSheet(it1, "")
            try {
                context?.let { it1 ->
                    ProductBottomSheetDialog.newInstance(it1, rewardsCanAssign, rewardsAssigned, rewardsAssignUpto).show(childFragmentManager, "")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }

        mBinding.tvAddLoan.setOnClickListener {
            context?.let { it1 ->
                MparticleUtils.logEvent(
                    "Onboarding_Add_New_Clicked",
                    "User opts to add a new product in a scenario where no products are identified\n",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.OB_Add_New_Clicked),
                    it1
                )
            }


            try {
                context?.let { it1 ->
                    ProductBottomSheetDialog.newInstance(it1, rewardsCanAssign, rewardsAssigned, rewardsAssignUpto).show(
                        childFragmentManager, ""
                    )
                }
            } catch (e: Exception) {
            }

        }

        mBinding.llChipCard.setOnClickListener {
            enableCardChip()
            mBinding.mainScroll.post {
                mBinding.mainScroll.smoothScrollTo(
                    0, mBinding.llAddCard.top
                )
            }
        }

        mBinding.llChipLoan.setOnClickListener {
            enableLoanChip()
            mBinding.mainScroll.post {
                mBinding.mainScroll.smoothScrollTo(
                    0, mBinding.llAddLoan.bottom
                )
            }
        }

        activity?.mBinding?.btnPayTogether?.setOnClickListener {
            goToPaymentMethodScreen()
        }
        mBinding.ivHelp.setOnClickListener {
            requireActivity().startActivity(Intent(
                requireContext(), CommonWebViewActivity::class.java
            ).apply {
                putExtra(URL, "${prefs?.faqsBaseUrl}${PAY_URL}")
            })
        }


    }

    private fun openPTInfoDialog() {
        val dialog = Utils.showCustomDialogBottum(activity, R.layout.bottom_sheet_pay_together_info)
        val bindingSheet = DataBindingUtil.inflate<BottomSheetPayTogetherInfoBinding>(
            layoutInflater, R.layout.bottom_sheet_pay_together_info, null, false
        )
        dialog.setContentView(bindingSheet.root)
        dialog.setCancelable(false)
        bindingSheet.ivCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun goToPaymentMethodScreen() {
        val productList = combineBIllAdapter.getSelectedProduct()
        requireActivity().startActivity(
            Intent(
                requireContext(), PaymentMethodsActivity::class.java
            ).putExtra(IntentKeys.PRODUCT_LIST, productList)
                .putExtra(IntentKeys.IS_PAY_TOGETHER, true)
                .putExtra(IntentKeys.TOTAL_AMOUNT, total.toString().trim())
                .putExtra(IntentKeys.REWARDS_ASSIGN_UPTO, rewardsAssignUpto)
                .putExtra(IntentKeys.REWARDS_CAN_ASSIGN, rewardsCanAssign)
                .putExtra(IntentKeys.REWARDS_ASSIGNED, rewardsAssigned)
        )
        isPayTogether = true
        isPartialPayTogether = false
    }

    override fun onResume() {
        super.onResume()
        activity?.setBottomTab(3)

        if (isPayTogether || isPartialPayTogether) {
            activity?.setBtnVisibilityFull(false)
        } else {
            activity?.setBtnVisibilityFull(true)
        }

    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]
        viewModelLoan = ViewModelProvider(this)[LoanProviderViewModel::class.java]
        viewModel?.readDeviceSms()
    }

    override fun onEmandate() {
    }

    override fun onActiveClick(
        productId: String?,
        last4: String?,
        bankLogo: String?,
        bankName: String?,
        startColor: String,
        middleColor: String,
        endColor: String,
        item: ProductV2
    ) {
        val intent = Intent(activity, CardDetailsActivityNew::class.java).putExtra(
            IntentKeys.PRODUCT_ID, productId
        ).putExtra(IntentKeys.LAST_FOUR, last4).putExtra(IntentKeys.BANK_LOGO, bankLogo)
            .putExtra(IntentKeys.BANK_NAME, bankName)
            .putExtra(IntentKeys.NEW_ID, item.bankMasterRecord?.id)
            .putExtra(IntentKeys.PRODUCT_ITEM, item).putExtra(IntentKeys.IS_FROM_HOME, "doToken")
            .putExtra(IntentKeys.SHOW_CUSTOM_PAYMENT, true)
            .putExtra(IntentKeys.REWARDS_ASSIGN_UPTO, rewardsAssignUpto)
            .putExtra(IntentKeys.REWARDS_CAN_ASSIGN, rewardsCanAssign)
            .putExtra(IntentKeys.REWARDS_ASSIGNED, rewardsAssigned)
        getResult.launch(intent)
    }

    override fun onActiveAndPayClick(
        productId: String?,
        last4: String?,
        bankLogo: String?,
        bankName: String?,
        startColor: String,
        middleColor: String,
        endColor: String,
        item: ProductV2
    ) {
        val selectedProduct: ArrayList<ProductV2> = ArrayList()
        selectedProduct.add(item)
        val intent = Intent(activity, CardDetailsActivityNew::class.java).putExtra(
            IntentKeys.PRODUCT_ID, productId
        ).putExtra(IntentKeys.LAST_FOUR, last4).putExtra(IntentKeys.BANK_LOGO, bankLogo)
            .putExtra(IntentKeys.BANK_NAME, bankName)
            .putExtra(IntentKeys.NEW_ID, item.bankMasterRecord?.id)
            .putExtra(IntentKeys.PRODUCT_ITEM, item).putExtra(IntentKeys.IS_FROM_HOME, "doToken")
            .putExtra(IntentKeys.PRODUCT_LIST, selectedProduct)
            .putExtra(IntentKeys.TOTAL_AMOUNT, total.toString().trim())
            .putExtra(IntentKeys.ACTIVE_AND_PAY, true)
            .putExtra(IntentKeys.REWARDS_ASSIGN_UPTO, rewardsAssignUpto)
            .putExtra(IntentKeys.REWARDS_CAN_ASSIGN, rewardsCanAssign)
            .putExtra(IntentKeys.REWARDS_ASSIGNED, rewardsAssigned)
        getResult.launch(intent)
    }


    @SuppressLint("SetTextI18n")
    override fun onPayNowCLick(
        productV2: ProductV2
    ) {
        val dialog = Utils.showCustomDialogBottum(activity, R.layout.bottom_sheet_total_due)
        dialog.setCancelable(false)
        val ivCancel = dialog.findViewById<FrameLayout>(R.id.ivCancel)

        dialog.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                dialog.dismiss()
                true // Capture onKey
            } else false
        }
        ivCancel.setOnClickListener {
            dialog.dismiss()
            Utils.hideKeyboard(activity)
            MparticleUtils.logEvent(
                "CC_Payment_SelectAmount_BackClicked",
                "User cancels the payment flow by closing the view",
                "Unique",
                "Back",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_SelectAmount_BackClicked),
                requireActivity()
            )

        }

        val ivCardType = dialog.findViewById<AppCompatImageView>(R.id.ivCardType)
        val ivBankImage = dialog.findViewById<AppCompatImageView>(R.id.iv_bank_image)

        imageUrl = "${prefs?.bankMasterUrl}${productV2.bankMasterRecord?.id}-logo-with-name.svg"

        ivBankImage.loadSvg(imageUrl)
        val ivMore = dialog.findViewById<AppCompatImageView>(R.id.ivMore)
        ivMore.setOnClickListener {
            openKnowMoreDialog(productV2.rewardRate)
            context?.let { it1 ->
                MparticleUtils.logEvent(
                    "CC_Payment_Rewards_KnowMore_Clicked",
                    "User clicks the information icon to learn more about the rewards on bill payment\n",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Rewards_KnowMore_Clicked),
                    it1
                )
            }
        }
        val tvRewardPercentage = dialog.findViewById<AppCompatTextView>(R.id.tvRewardPercentage)
        val tvRewardEarned = dialog.findViewById<AppCompatTextView>(R.id.tvRewardEarned)
        val tvAtTheRate = dialog.findViewById<AppCompatTextView>(R.id.tvAtTheRate)
        val tvInfo = dialog.findViewById<AppCompatTextView>(R.id.tvInfo)
        val ivMessageType = dialog.findViewById<AppCompatImageView>(R.id.ivMessageType)
        val tvCardNumber = dialog.findViewById<AppCompatTextView>(R.id.tvCardNumber)
        tvCardNumber.text = "··· " + productV2.last4
        val tvBankName = dialog.findViewById<AppCompatTextView>(R.id.tvBankName)
        val tvRewards = dialog.findViewById<AppCompatTextView>(R.id.tvRewards)
        tvBankName.text = productV2.bankMasterRecord?.bankName ?: ""
        val etAmount = dialog.findViewById<EditText>(R.id.etAmount)
        etAmount.setText(Utils.getFormattedDecimal(productV2.bill?.amount_paid?.let {
            productV2.bill.total_due.minus(
                it
            )
        } ?: 0.0))
        val tvCaption = dialog.findViewById<AppCompatTextView>(R.id.tvCaption)
        val chipTotalDue = dialog.findViewById<Chip>(R.id.chipTotalDue)
        val chipMinimumDue = dialog.findViewById<Chip>(R.id.chipMinimumDue)
        val customChip = dialog.findViewById<Chip>(R.id.chipCustom)

        val llMessageMinimumDue = dialog.findViewById<LinearLayoutCompat>(R.id.llMessageMinimumDue)
        val llRewards = dialog.findViewById<LinearLayoutCompat>(R.id.llRewards)
        val llMessageTotalDue = dialog.findViewById<LinearLayoutCompat>(R.id.llMessageTotalDue)
        val llAmtView = dialog.findViewById<LinearLayoutCompat>(R.id.llAmtView)
        val llMessageError = dialog.findViewById<LinearLayoutCompat>(R.id.llMessageError)
        val tvError = dialog.findViewById<TextView>(R.id.tvError)
        val btnOkay = dialog.findViewById<AppCompatButton>(R.id.btnOkay)
        val flStroke = dialog.findViewById<FrameLayout>(R.id.flStroke)
        tvInfo.text = getString(R.string.str_great_move)
        ivMessageType.setImageResource(R.drawable.ic_happy_face)
        val llCardSolidBackGround =
            dialog.findViewById<LinearLayoutCompat>(R.id.llCardSolidBackGround)
        GradientUtils.setBoarderStroke(
            productV2.bankMasterRecord?.ui_config?.cardColor ?: "#FFFFFF",
            productV2.bankMasterRecord?.ui_config?.opacity_border ?: "#FFFFFF",
            true,
            flStroke
        )
        GradientUtils.setBackGround(
            productV2.bankMasterRecord?.ui_config?.cardColor ?: "#FFFFFF",
            "",
            productV2.bankMasterRecord?.ui_config?.opacity_topLeft ?: "#FFFFFF",
            productV2.bankMasterRecord?.ui_config?.opacity_bottomRight ?: "#FFFFFF",
            llCardSolidBackGround
        )
        if (rewardsAssignUpto != 0) {
            tvRewardEarned.text =
                "$rewardsAssigned/$rewardsAssignUpto chips have been earned this month!"
        }

        if ((productV2.bill?.total_due ?: 0.0) >= 100) {
            rewardsPoint = roundupAmount(productV2.bill?.amount_paid?.let {
                productV2.bill.total_due.minus(
                    it
                )
            } ?: DEFAULT_PERCENTAGE,
                DEFAULT_PERCENTAGE,
                tvRewardPercentage,
                productV2.bankMasterRecord?.id,
                Utils.itemList)
            llRewards.visibility = View.VISIBLE
            MonthlyEarnRule.setRewardLimit(
                rewardsCanAssign,
                rewardsAssignUpto,
                rewardsPoint,
                tvRewards,
                tvAtTheRate,
                tvRewardPercentage,
                tvRewardEarned,
                requireContext()
            )


        } else if ((productV2.bill?.total_due ?: 0.0) < 100) {
            llRewards.visibility = View.VISIBLE
            tvRewards.text = getString(R.string.str_pay_total_bill)
        } else {
            llRewards.visibility = View.GONE
        }
        billStatus = FULL
        productV2.billStatus = FULL
        chipTotalDue.setOnClickListener {
            tvInfo.text = getString(R.string.str_great_move)
            ivMessageType.setImageResource(R.drawable.ic_happy_face)
            billStatus = FULL
            productV2.billStatus = FULL
            etAmount.setText(
                "" + Utils.getFormattedDecimal(
                    productV2.bill?.total_due?.minus(productV2.bill.amount_paid) ?: 0.0
                )
            )
            etAmount.isEnabled = false
            llMessageError.visibility = View.GONE
            llMessageTotalDue.visibility = View.VISIBLE
            llMessageMinimumDue.visibility = View.GONE
            tvCaption.text = resources.getText(R.string.you_have_to_pay)
            context?.let { it1 ->
                activity?.let { it2 ->
                    MparticleUtils.logCurrentScreen(
                        "/cc-payment/select-amount",
                        "The customer chooses the amount for individual credit card payment",
                        "type",
                        "total-only",
                        "",
                        "",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Select_Amount),
                        it2.applicationContext
                    )
                }
                MparticleUtils.logEvent(
                    "CC_Payment_TotalDue_Clicked",
                    "User clicks the total due bubble to view total due amount\n",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_TotalDue_Clicked),
                    it1
                )
            }

            if ((productV2.bill?.total_due ?: 0.0) >= 100) {
                rewardsPoint = roundupAmount(
                    productV2.bill?.total_due?.minus(
                        productV2.bill.amount_paid
                    ) ?: DEFAULT_PERCENTAGE,
                    DEFAULT_PERCENTAGE,
                    tvRewardPercentage,
                    productV2.bankMasterRecord?.id,
                    Utils.itemList
                )
                llRewards.visibility = View.VISIBLE
                MonthlyEarnRule.setRewardLimit(
                    rewardsCanAssign,
                    rewardsAssignUpto,
                    rewardsPoint,
                    tvRewards,
                    tvAtTheRate,
                    tvRewardPercentage,
                    tvRewardEarned,
                    requireContext()
                )
            } else if ((productV2.bill?.total_due ?: 0.0) < 100) {
                llRewards.visibility = View.VISIBLE
                tvRewards.text = getString(R.string.str_pay_total_bill)
            } else {
                llRewards.visibility = View.GONE
            }
        }

        if (productV2.card_network == MASTER_CARD) {
            ivCardType.setImageResource(R.drawable.ic_mastercard)
        }
        if (productV2.card_network == VISA) {
            ivCardType.setImageResource(R.drawable.visa)
        }
        if (productV2.card_network == DINERS_CARD) {
            ivCardType.setImageResource(R.drawable.ic_dinner_icon)
        }
        if (productV2.card_network == AMERICAN_CARD) {
            ivCardType.setImageResource(R.drawable.ic_american_icon)
        }
        if (productV2.card_network == RUPAY_CARD) {
            ivCardType.setImageResource(R.drawable.ic_rupay_card_icon)
        }

        etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (p0.toString().isNotEmpty()) {
                    val amount = p0.toString().replace(",", "").toDouble()
                    if (amount > 0 && amount < 100) {
                        tvRewards.text = getString(R.string.str_enter_100)
                        tvRewardPercentage.visibility = View.GONE
                        tvAtTheRate.visibility = View.GONE
                        llRewards.visibility = View.VISIBLE
                    } else {
                        rewardsPoint = roundupAmount(
                            amount,
                            DEFAULT_PERCENTAGE,
                            tvRewardPercentage,
                            productV2.bankMasterRecord?.id,
                            Utils.itemList
                        )
                        tvRewardPercentage.visibility = View.VISIBLE
                        tvAtTheRate.visibility = View.VISIBLE
                        llRewards.visibility = View.VISIBLE
                        MonthlyEarnRule.setRewardLimit(
                            rewardsCanAssign,
                            rewardsAssignUpto,
                            rewardsPoint,
                            tvRewards,
                            tvAtTheRate,
                            tvRewardPercentage,
                            tvRewardEarned,
                            requireContext()
                        )

                    }

                    if (amount > (productV2.bill?.total_due ?: 0.0)) {
                        tvError.text = getString(R.string.str_enter_amount_is_more)
                        llMessageError.visibility = View.VISIBLE
                        btnOkay.isEnabled = true
                        llMessageTotalDue.visibility = View.GONE
                    } else if (amount < 1) {
                        llMessageError.visibility = View.VISIBLE
                        tvError.text = getString(R.string.str_minimum_amount_one)
                        btnOkay.isEnabled = false
                        llMessageTotalDue.visibility = View.GONE
                    } else if (amount > 1000000) {
                        llMessageError.visibility = View.VISIBLE
                        tvError.text = getString(R.string.str_amount_less_1000000)
                        btnOkay.isEnabled = false
                        llMessageTotalDue.visibility = View.GONE
                    } else {
                        llMessageError.visibility = View.GONE
                        btnOkay.isEnabled = true
                        llMessageTotalDue.visibility = View.VISIBLE
                    }

                    val amountForRewards = p0.toString().replace(",", "").toInt()

                    rewardsPoint = roundupAmount(
                        amountForRewards.toDouble(),
                        DEFAULT_PERCENTAGE,
                        tvRewardPercentage,
                        productV2.bankMasterRecord?.id,
                        Utils.itemList
                    )

                } else {
                    llMessageError.visibility = View.GONE
                    btnOkay.isEnabled = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        etAmount.setOnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                llAmtView.setBackgroundResource(R.drawable.et_btm_bg)
            } else {
                llAmtView.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
                context?.let {
                    MparticleUtils.logEvent(
                        "CC_Payment_Custom_Entered",
                        "User enters a custom amount for bill payment\n",
                        "Unique",
                        "Input Field",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Custom_Entered),
                        it
                    )
                }
            }
        }

        if (productV2.bill?.min_due != null && productV2.bill.min_due.minus(productV2.bill.amount_paid) > 0) {
            chipMinimumDue.setOnClickListener {
                tvInfo.text = getString(R.string.str_you_will_incur)
                ivMessageType.setImageResource(R.drawable.ic_warning)
                if (productV2.bill.min_due >= 100) {
                    rewardsPoint = roundupAmount(
                        productV2.bill.min_due,
                        DEFAULT_PERCENTAGE,
                        tvRewardPercentage,
                        productV2.bankMasterRecord?.id,
                        Utils.itemList
                    )
                    llRewards.visibility = View.VISIBLE
                    MonthlyEarnRule.setRewardLimit(
                        rewardsCanAssign,
                        rewardsAssignUpto,
                        rewardsPoint,
                        tvRewards,
                        tvAtTheRate,
                        tvRewardPercentage,
                        tvRewardEarned,
                        requireContext()
                    )

                } else if (productV2.bill.min_due < 100) {
                    llRewards.visibility = View.VISIBLE
                    tvRewards.text = getString(R.string.str_pay_total_bill)
                } else {
                    llRewards.visibility = View.GONE
                }
                billStatus = MIN
                productV2.billStatus = MIN
                etAmount.setText(Utils.getFormattedDecimal(
                    productV2.bill.min_due.minus(productV2.bill.amount_paid)))
                etAmount.isEnabled = false
                llMessageError.visibility = View.GONE
                llMessageTotalDue.visibility = View.VISIBLE
                llMessageMinimumDue.visibility = View.VISIBLE
                tvCaption.text = resources.getText(R.string.you_have_to_pay)
                context?.let { it1 ->
                    MparticleUtils.logCurrentScreen(
                        "/cc-payment/select-amount",
                        "The customer chooses the amount for individual credit card payment",
                        "type",
                        "total-minimum-only",
                        "",
                        "",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Select_Amount),
                        it1
                    )
                    MparticleUtils.logEvent(
                        "CC_Payment_MinimumDue_Clicked",
                        "User clicks the minimum due bubble to view minimum due amount\n",
                        "Unique",
                        "Continue",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_MinimumDue_Clicked),
                        it1
                    )
                }
                rewardsPoint = roundupAmount(
                    productV2.bill.min_due,
                    DEFAULT_PERCENTAGE,
                    tvRewardPercentage,
                    productV2.bankMasterRecord?.id,
                    Utils.itemList
                )
            }
        } else {
            chipMinimumDue.visibility = View.GONE
        }
        customChip.setOnClickListener {
            tvInfo.text = getString(R.string.str_you_will_incur)
            ivMessageType.setImageResource(R.drawable.ic_warning)
            llRewards.visibility = View.GONE
            tvRewards.text = ""
            billStatus = CUSTOM
            etAmount.isEnabled = true

            etAmount.requestFocus()
            etAmount.setText("")
            btnOkay.isEnabled = false
            llMessageTotalDue.visibility = View.VISIBLE
            llMessageMinimumDue.visibility = View.GONE
            tvCaption.text = getString(R.string.str_enter_amount)
            activity?.let { it1 -> Utils.showKeyboard(it1) }
            activity?.let { it1 ->

                MparticleUtils.logCurrentScreen(
                    "/cc-payment/select-amount",
                    "The customer chooses the amount for individual credit card payment",
                    "type",
                    "total-custom-only",
                    "",
                    "",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Select_Amount),
                    it1
                )
                MparticleUtils.logEvent(
                    "CC_Payment_Custom_Clicked",
                    "User clicks the custom bumble to enter a custom amount for bill payment\n",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Custom_Clicked),
                    it1
                )
            }

        }

        btnOkay.setOnClickListener {
            val amount = etAmount.text.toString().replace(",", "")
            if (amount.replace(",", "").toDouble() < 10) {
                llMessageError.visibility = View.VISIBLE
                tvError.text = getString(R.string.str_minimum_amount)
                btnOkay.isEnabled = false
                llMessageTotalDue.visibility = View.GONE

            } else if (amount.replace(",", "").toDouble() > 1000000) {
                llMessageError.visibility = View.VISIBLE
                tvError.text = getString(R.string.str_amount_less_1000000)
                llMessageError.visibility = View.VISIBLE

            } else {
                productV2.customAmount = amount.toDouble()
                productV2.billStatus = billStatus
                productV2.rewardsPoint = rewardsPoint
                val selectedProduct: ArrayList<ProductV2> = ArrayList()
                selectedProduct.add(productV2)
                requireActivity().startActivity(
                    Intent(requireContext(), PaymentMethodsActivity::class.java).putExtra(
                        IntentKeys.PRODUCT_LIST, selectedProduct
                    ).putExtra(IntentKeys.IS_PAY_TOGETHER, false)
                        .putExtra(IntentKeys.TOTAL_AMOUNT, amount.trim())
                        .putExtra(IntentKeys.REWARDS_POINT, rewardsPoint)
                        .putExtra(IntentKeys.BILL_STATUS, billStatus)
                        .putExtra(IntentKeys.REWARDS_ASSIGN_UPTO, rewardsAssignUpto)
                        .putExtra(IntentKeys.REWARDS_CAN_ASSIGN, rewardsCanAssign)
                        .putExtra(IntentKeys.REWARDS_ASSIGNED, rewardsAssigned)
                )
                isPartialPayTogether = false
                Utils.hideKeyboard(activity)

                activity?.applicationContext?.let { it1 ->
                    MparticleUtils.logEvent(
                        "CC_Payment_SelectAmount_Clicked",
                        "User confirms the selection of amount and progresses to next screen",
                        "Not Unique",
                        "Continue",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_SelectAmount_Clicked),
                        it1
                    )
                }


            }

        }
        dialog.show()
    }

    override fun onLoanActivateClick(
        productId: String, loan: ProductV2
    ) {
        viewModelLoan.bbpsPrefilledData(productId).observe(this) {
            if (it.status) {
                val intent = Intent(activity, DetailsForLoanActivity::class.java).putExtra(
                    "LOAN_PROVIDER",
                    it.product,
                ).putExtra("from", "home").putExtra("instId", loan.institution_master_id)

                startActivity(intent)
            }
        }

    }

    override fun onLoanPayClick(
        product_number: String?,
        billerName: String?,
        product_type: String?,
        bankLogo: String?,
        minDue: Double?,
        totalDue: Int?,
        billId: String?,
        productId: String?,
        paidAmount: Double?,
        lastFour: String?,
        startColor: String,
        middleColor: String,
        endColor: String,
        item: ProductV2
    ) {

        val dialog = Utils.showCustomDialogBottum(activity, R.layout.bottom_sheet_total_due_loan)
        dialog.setCancelable(false)
        val ivCancel = dialog.findViewById<FrameLayout>(R.id.ivCancel)
        imageUrl = "${prefs?.loanMasterUrl}${item.bankMasterRecord?.id}-logo-with-name.svg"
        ivCancel.setOnClickListener {
            dialog.dismiss()
            Utils.hideKeyboard(activity)
        }

        dialog.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                dialog.dismiss()
                true // Capture onKey
            } else false
        }
        val ivCardType = dialog.findViewById<AppCompatImageView>(R.id.ivCardType)
        ivCardType.visibility = View.GONE
        val ivBankImage = dialog.findViewById<AppCompatImageView>(R.id.iv_bank_image)
        ivBankImage.loadSvg(imageUrl)
        val ivMore = dialog.findViewById<AppCompatImageView>(R.id.ivMore)
        ivMore.setOnClickListener {
            openKnowMoreDialog(item.rewardRate)
        }
        val tvCardNumber = dialog.findViewById<AppCompatTextView>(R.id.tvCardNumber)
        tvCardNumber.text = "··· ${item.last4}"
        val flSolidColor = dialog.findViewById<ImageView>(R.id.flSolidColor)
        if (item.bankMasterRecord?.ui_config != null) {
            flSolidColor.setBackgroundResource(R.drawable.loan_bottom_shap)
            val bg: Drawable? = flSolidColor?.background
            bg?.setTint(Color.parseColor(item.bankMasterRecord.ui_config.cardColor))
        }

        val tvBankName = dialog.findViewById<AppCompatTextView>(R.id.tvBankName)
        tvBankName.text = item.bankMasterRecord?.billerName
        val etAmount = dialog.findViewById<EditText>(R.id.etAmount)
        val tvCaption = dialog.findViewById<AppCompatTextView>(R.id.tvCaption)
        val tvRewards = dialog.findViewById<AppCompatTextView>(R.id.tvRewards)
        val chipTotalDue = dialog.findViewById<Chip>(R.id.chipTotalDue)
        val chipMinimumDue = dialog.findViewById<Chip>(R.id.chipMinimumDue)
        val customChip = dialog.findViewById<Chip>(R.id.chipCustom)
        val tvRewardPercentage = dialog.findViewById<AppCompatTextView>(R.id.tvRewardPercentage)
        val tvAtTheRate = dialog.findViewById<AppCompatTextView>(R.id.tvAtTheRate)
        val tvRewardEarned = dialog.findViewById<AppCompatTextView>(R.id.tvRewardEarned)

        val llMessageMinimumDue = dialog.findViewById<LinearLayoutCompat>(R.id.llMessageMinimumDue)
        val llMessageTotalDue = dialog.findViewById<LinearLayoutCompat>(R.id.llMessageTotalDue)
        val llAmtView = dialog.findViewById<LinearLayoutCompat>(R.id.llAmtView)
        val llMessageError = dialog.findViewById<LinearLayoutCompat>(R.id.llMessageError)
        val llRewards = dialog.findViewById<LinearLayoutCompat>(R.id.llRewards)
        val tvError = dialog.findViewById<TextView>(R.id.tvError)
        val ivLoanError = dialog.findViewById<AppCompatImageView>(R.id.ivLoanError)
        val btnOkay = dialog.findViewById<AppCompatButton>(R.id.btnOkay)
        tvError.setTextColor(resources.getColor(R.color.red))
        ivLoanError.setImageResource(R.drawable.ic_alert_new)
        if (item.bill?.total_due != null) {
            etAmount.setText(totalDue?.toDouble()?.let { Utils.getFormattedDecimal(it) })
        }
        if (rewardsAssignUpto != 0) {
            tvRewardEarned.text =
                "$rewardsAssigned/$rewardsAssignUpto chips have been earned this month!"
        }

        if ((item.bill?.total_due ?: 0.0) >= 100) {

            etAmount.isEnabled = false
            rewardsPoint = roundupAmount(
                (item.bill?.total_due?.minus(item.bill.amount_paid)) ?: DEFAULT_PERCENTAGE,
                DEFAULT_PERCENTAGE,
                tvRewardPercentage,
                item.bankMasterRecord?.id,
                Utils.itemList
            )
            item.rewardsPoint = rewardsPoint
            llRewards.visibility = View.VISIBLE
            billStatus = FULL
            item.billStatus = FULL
            MonthlyEarnRule.setRewardLimit(
                rewardsCanAssign,
                rewardsAssignUpto,
                rewardsPoint,
                tvRewards,
                tvAtTheRate,
                tvRewardPercentage,
                tvRewardEarned,
                requireContext()
            )
        } else if ((item.bill?.total_due ?: 0.0) < 100) {
            llRewards.visibility = View.VISIBLE
            tvRewards.text = getString(R.string.str_pay_total_bill)
            tvAtTheRate.visibility = View.GONE
        } else {
            llRewards.visibility = View.GONE
        }
        if (item.bill?.total_due != null && item.bill?.total_due > 0) {
            etAmount.setText(totalDue?.toDouble()?.let { it1 -> Utils.getFormattedDecimal(it1) })
            btnOkay.isEnabled = true
        }

        chipTotalDue.setOnClickListener {
            billStatus = FULL
            item.billStatus = FULL
            etAmount.setText(totalDue?.toDouble()?.let { it1 -> Utils.getFormattedDecimal(it1) })
            etAmount.isEnabled = false
            llMessageError.visibility = View.GONE
            llMessageTotalDue.visibility = View.VISIBLE
            llMessageMinimumDue.visibility = View.GONE
            tvCaption.text = resources.getText(R.string.you_have_to_pay)
            if ((item.bill?.total_due ?: 0.0) >= 100) {
                rewardsPoint = roundupAmount(
                    (item.bill?.total_due?.minus(item.bill.amount_paid)) ?: DEFAULT_PERCENTAGE,
                    DEFAULT_PERCENTAGE,
                    tvRewardPercentage,
                    item.bankMasterRecord?.id,
                    Utils.itemList
                )
                item.rewardsPoint = rewardsPoint
                llRewards.visibility = View.VISIBLE
                billStatus = FULL
                item.billStatus = FULL
                MonthlyEarnRule.setRewardLimit(
                    rewardsCanAssign,
                    rewardsAssignUpto,
                    rewardsPoint,
                    tvRewards,
                    tvAtTheRate,
                    tvRewardPercentage,
                    tvRewardEarned,
                    requireContext()
                )

            } else if ((item.bill?.total_due ?: 0.0) < 100) {
                llRewards.visibility = View.VISIBLE
                tvRewards.text = getString(R.string.str_pay_total_bill)
            } else {
                llRewards.visibility = View.GONE
            }
        }
        val paymentAmountExactness = item.amountExactness

        var paymentTypeEvent = ""

        if (paymentAmountExactness == getString(R.string.adhoc)) {
            paymentTypeEvent = "all-three"
            /// All three methode Total,Minimum ,Custom
            customChip.visibility = View.VISIBLE
            llMessageError.visibility = View.GONE
            if (item.bill?.total_due != null && item.bill.total_due > 0) {
                chipTotalDue.visibility = View.VISIBLE
            } else {
                etAmount?.setText("")
                etAmount.isEnabled = true
                etAmount.requestFocus()
                chipTotalDue.visibility = View.GONE
                customChip.isChecked = true
            }
            if (minDue != null && minDue > 0 && !minDue.equals(0)) {
                chipMinimumDue.visibility = View.VISIBLE
            } else {
                chipMinimumDue.visibility = View.GONE
            }
            llMessageTotalDue.visibility = View.GONE
            llMessageMinimumDue.visibility = View.GONE
            tvCaption.text = getString(R.string.str_enter_amount)
            activity?.let { it1 -> Utils.showKeyboard(it1) }

        } else if (paymentAmountExactness == "EXACT_AND_BELOW") {
            // Custom and Total
            paymentTypeEvent = "total-custom-only"
            chipMinimumDue.visibility = View.GONE
            chipTotalDue.visibility = View.VISIBLE
            customChip.visibility = View.VISIBLE


        } else if (paymentAmountExactness == "EXACT_AND_ABOVE") {
            // Custom and Total
            paymentTypeEvent = "total-custom-only"
            chipMinimumDue.visibility = View.GONE
            chipTotalDue.visibility = View.VISIBLE
            customChip.visibility = View.VISIBLE

        } else if (paymentAmountExactness == "EXACT") {
            //   Total
            paymentTypeEvent = "total-only"
            chipMinimumDue.visibility = View.GONE
            chipTotalDue.visibility = View.VISIBLE
            customChip.visibility = View.GONE
            etAmount.isEnabled = false
        } else {
            // Total and custom
            paymentTypeEvent = "total-custom-only"
            etAmount?.setText(item.bill?.total_due.toString())
            etAmount?.isEnabled = false
            customChip.visibility = View.VISIBLE
            chipMinimumDue.visibility = View.GONE
            chipTotalDue.visibility = View.VISIBLE
            llMessageError.visibility = View.GONE
            llMessageError.visibility = View.GONE
        }
        customChip.setOnClickListener {
            llRewards.visibility = View.GONE
            tvRewards.text = ""
            billStatus = CUSTOM
            etAmount.isEnabled = true
            etAmount.requestFocus()
            etAmount.setText("")
            btnOkay.isEnabled = false
            llMessageTotalDue.visibility = View.GONE
            llMessageMinimumDue.visibility = View.GONE
            tvCaption.text = getString(R.string.str_enter_amount)
            activity?.let { it1 -> Utils.showKeyboard(it1) }

        }
        etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (p0?.toString()?.isNotEmpty() == true) {
                    val amount = p0.toString().replace(",", "").toDouble()
                    if (billStatus == CUSTOM) {
                        if (amount > 0 && amount < 100) {
                            tvRewards.text = getString(R.string.str_enter_100)
                            tvRewardPercentage.visibility = View.GONE
                            tvAtTheRate.visibility = View.GONE
                            btnOkay.isEnabled = true
                            llRewards.visibility = View.VISIBLE
                        } else {
                            rewardsPoint = roundupAmount(
                                amount,
                                DEFAULT_PERCENTAGE,
                                tvRewardPercentage,
                                item.bankMasterRecord?.id,
                                Utils.itemList
                            )
                            tvRewardPercentage.visibility = View.VISIBLE
                            tvAtTheRate.visibility = View.VISIBLE
                            llRewards.visibility = View.VISIBLE
                            MonthlyEarnRule.setRewardLimit(
                                rewardsCanAssign,
                                rewardsAssignUpto,
                                rewardsPoint,
                                tvRewards,
                                tvAtTheRate,
                                tvRewardPercentage,
                                tvRewardEarned,
                                requireContext()
                            )
                            btnOkay.isEnabled = true
                        }
                    } else {
                        if (amount > 0 && amount < 100) {
                            tvRewards.text = getString(R.string.str_enter_100)
                            tvRewardPercentage.visibility = View.GONE
                            tvAtTheRate.visibility = View.GONE
                            btnOkay.isEnabled = true
                            llRewards.visibility = View.VISIBLE
                        } else {
                            rewardsPoint = roundupAmount(
                                amount,
                                DEFAULT_PERCENTAGE,
                                tvRewardPercentage,
                                item.bankMasterRecord?.id,
                                Utils.itemList
                            )
                            tvRewardPercentage.visibility = View.VISIBLE
                            tvAtTheRate.visibility = View.VISIBLE
                            llRewards.visibility = View.VISIBLE
                            MonthlyEarnRule.setRewardLimit(
                                rewardsCanAssign,
                                rewardsAssignUpto,
                                rewardsPoint,
                                tvRewards,
                                tvAtTheRate,
                                tvRewardPercentage,
                                tvRewardEarned,
                                requireContext()
                            )
                            btnOkay.isEnabled = true
                        }
                        if (paymentAmountExactness == getString(R.string.adhoc)) {
                            btnOkay.isEnabled = true
                        } else if (paymentAmountExactness == getString(R.string.exact_and_below)) {
                            if (totalDue != null) {
                                if (amount > totalDue) {
                                    tvError.text = getString(R.string.str_enter_amount_is_more)
                                    llMessageError.visibility = View.VISIBLE
                                    btnOkay.isEnabled = false
                                }
                            }
                        } else if (paymentAmountExactness == getString(R.string.exact_and_above)) {
                            if (totalDue != null) {
                                if (amount < totalDue) {
                                    tvError.text = getString(R.string.str_amount_less_total_due)
                                    llMessageError.visibility = View.VISIBLE
                                    btnOkay.isEnabled = true
                                }
                            }
                        } else {
                            btnOkay.isEnabled = true
                        }
                    }
                } else {
                    llMessageError.visibility = View.GONE
                    btnOkay.isEnabled = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        etAmount.setOnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                llAmtView.setBackgroundResource(R.drawable.et_btm_bg)
            } else {
                llAmtView.setBackgroundResource(R.drawable.et_btm_bg_un_focused)

            }
        }
        btnOkay.setOnClickListener {
            if (etAmount.text.toString().isNotEmpty()) {
                if (etAmount.text.toString().replace(",", "").toDouble() < 10) {
                    tvError.text = getString(R.string.str_minimum_amount)
                    llMessageError.visibility = View.VISIBLE
                } else if (etAmount.text.toString().replace(",", "").toDouble() > 1000000) {
                    tvError.text = getString(R.string.str_amount_less_1000000)
                    llMessageError.visibility = View.VISIBLE
                } else {
                    if (product_number != null) {
                        val amount = etAmount.text.toString().replace(",", "")
                        item.customAmount = amount.toDouble()
                        item.rewardsPoint = rewardsPoint
                        item.billStatus = billStatus
                        val selectedProduct: ArrayList<ProductV2> = ArrayList()
                        selectedProduct.add(item)
                        requireActivity().startActivity(
                            Intent(requireContext(), PaymentMethodsActivity::class.java).putExtra(
                                IntentKeys.PRODUCT_LIST, selectedProduct
                            ).putExtra(IntentKeys.IS_PAY_TOGETHER, false)
                                .putExtra(IntentKeys.TOTAL_AMOUNT, amount.trim())
                                .putExtra(IntentKeys.REWARDS_POINT, rewardsPoint)
                                .putExtra(IntentKeys.BILL_STATUS, billStatus)
                                .putExtra(IntentKeys.REWARDS_ASSIGN_UPTO, rewardsAssignUpto)
                                .putExtra(IntentKeys.REWARDS_CAN_ASSIGN, rewardsCanAssign)
                                .putExtra(IntentKeys.REWARDS_ASSIGNED, rewardsAssigned)
                        )
                        isPartialPayTogether = false

                    }
                    Utils.hideKeyboard(activity)
                    dialog.dismiss()

                }
            }
        }

        if (item.bill?.total_due != null && item.bill?.total_due > 0) {
            MparticleUtils.logCurrentScreen(
                "/loan-payment/select-amount",
                "The customer chooses the amount for individual loan payment",
                "type",
                "$paymentTypeEvent",
                "",
                "",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.LOAN_Payment_Select_Amount),
                dialog.context
            )

            if (totalDue != null && totalDue > 0 && totalDue != 0) {
                chipTotalDue.visibility = View.VISIBLE

            } else {
                llRewards.visibility = View.GONE
                tvRewards.text = ""
                chipTotalDue.visibility = View.GONE
                billStatus = CUSTOM
                customChip.isChecked = true
                etAmount.isEnabled = true
                etAmount.requestFocus()
                etAmount.setText("")
                btnOkay.isEnabled = false
                llMessageTotalDue.visibility = View.GONE
                llMessageMinimumDue.visibility = View.GONE
                tvCaption.text = getString(R.string.str_enter_amount)
                activity?.let { it1 -> Utils.showKeyboard(it1) }
            }


        }
        dialog.show()
    }

    private fun enableCardChip() {
        mBinding.llChipCard.setBackgroundResource(R.drawable.categories_item_bg_selected)
        mBinding.llChipLoan.setBackgroundResource(R.drawable.categories_item_bg)
        mBinding.tvCard.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.colorPrimary
            )
        )
        mBinding.tvCard.setTypeface(null, Typeface.BOLD)
        mBinding.tvLoan.setTypeface(null, Typeface.NORMAL)
        mBinding.tvCCCount.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        mBinding.tvLoan.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.colorGreyText
            )
        )
        mBinding.tvLoanCount.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.colorGreyText
            )
        )
        mBinding.ivCircleCC.setImageResource(R.drawable.circle_bg_primary)
        mBinding.ivCircleLoan.setImageResource(R.drawable.circle_bg_grey)
        mBinding.rvProduct.visibility = View.VISIBLE
        mBinding.llAddCard.visibility = View.VISIBLE


    }

    private fun enableLoanChip() {
        mBinding.llChipLoan.setBackgroundResource(R.drawable.categories_item_bg_selected)
        mBinding.llChipCard.setBackgroundResource(R.drawable.categories_item_bg)
        mBinding.tvLoan.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.colorPrimary
            )
        )
        mBinding.tvLoan.setTypeface(null, Typeface.BOLD)
        mBinding.tvCard.setTypeface(null, Typeface.NORMAL)
        mBinding.tvCCCount.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.colorGreyText
            )
        )
        mBinding.tvCard.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.colorGreyText
            )
        )
        mBinding.tvLoanCount.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.white
            )
        )
        mBinding.ivCircleLoan.setImageResource(R.drawable.circle_bg_primary)
        mBinding.ivCircleCC.setImageResource(R.drawable.circle_bg_grey)

    }

    @SuppressLint("SetTextI18n")
    override fun onPayMoreCLick(
        productV2: ProductV2
    ) {


        activity?.applicationContext?.let {
            MparticleUtils.logCurrentScreen(
                "/cc-payment/select-amount",
                "The customer chooses the amount for individual credit card payment",
                "type",
                "custom-only",
                "",
                "",
                SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Activation_Pending),
                it
            )
        }
        val dialog = Utils.showCustomDialogBottum(activity, R.layout.bottom_sheet_pay_more)
        var rewardsPoint = 0
        val bindingSheet = DataBindingUtil.inflate<BottomSheetPayMoreBinding>(
            layoutInflater, R.layout.bottom_sheet_pay_more, null, false
        )

        imageUrl = "${prefs?.bankMasterUrl}${productV2.bankMasterRecord?.id}-logo-with-name.svg"
        dialog.setContentView(bindingSheet.root)
        dialog.setCancelable(false)
        activity?.let { Utils.showKeyboard(it) }
        bindingSheet.ivCancel.setOnClickListener {
            dialog.dismiss()
            Utils.hideKeyboard(activity)
        }
        if (rewardsPoint == 0) {
            bindingSheet.llRewards.visibility = View.GONE
        } else {
            bindingSheet.llRewards.visibility = View.VISIBLE
        }
        bindingSheet.etAmount.setOnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                bindingSheet.llAmtView.setBackgroundResource(R.drawable.et_btm_bg)
            } else {
                bindingSheet.llAmtView.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
            }
        }
        if (rewardsAssignUpto != 0) {
            bindingSheet.tvRewardEarned.text =
                "$rewardsAssigned/$rewardsAssignUpto chips have been earned this month!"
        }
        bindingSheet.llCreditCard.ivBankImage.loadSvg(imageUrl)
        bindingSheet.ivMore.setOnClickListener {
            openKnowMoreDialog(productV2.rewardRate)
        }
        GradientUtils.setBoarderStroke(
            productV2.bankMasterRecord?.ui_config?.cardColor ?: "#FFFFFF",
            productV2.bankMasterRecord?.ui_config?.opacity_border ?: "#FFFFFF",
            true,
            bindingSheet.llCreditCard.flStroke
        )
        GradientUtils.setBackGround(
            productV2.bankMasterRecord?.ui_config?.cardColor ?: "#FFFFFF",
            "",
            productV2.bankMasterRecord?.ui_config?.opacity_topLeft ?: "#FFFFFF",
            productV2.bankMasterRecord?.ui_config?.opacity_bottomRight ?: "#FFFFFF",
            bindingSheet.llCreditCard.llCardSolidBackGround
        )
        bindingSheet.llCreditCard.tvCardNumber.text = "···  ${productV2.last4}"


        bindingSheet.llCreditCard.tvBankName.text = productV2.bankMasterRecord?.bankName ?: ""
        bindingSheet.etAmount.setText("")
        bindingSheet.etAmount.requestFocus()

        when (productV2.card_network) {
            MASTER_CARD -> {
                bindingSheet.llCreditCard.ivCardType.setImageResource(R.drawable.ic_mastercard)
            }
            VISA -> {
                bindingSheet.llCreditCard.ivCardType.setImageResource(R.drawable.visa)
            }
            AMERICAN_CARD -> {
                bindingSheet.llCreditCard.ivCardType.setImageResource(R.drawable.ic_american_icon)
            }
            DINERS_CARD -> {
                bindingSheet.llCreditCard.ivCardType.setImageResource(R.drawable.ic_dinner_icon)
            }
            RUPAY_CARD -> {
                bindingSheet.llCreditCard.ivCardType.setImageResource(R.drawable.ic_rupay_card_icon)
            }
        }
        bindingSheet.etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()) {
                    val amountForRewards = p0.toString().toDouble()
                    if (amountForRewards > 0 && amountForRewards < 100) {
                        bindingSheet.tvRewards.text = getString(R.string.str_enter_100)
                        bindingSheet.tvRewardPercentage.visibility = View.GONE
                        bindingSheet.tvAtTheRate.visibility = View.GONE
                        bindingSheet.llRewards.visibility = View.VISIBLE
                    } else {
                        rewardsPoint = roundupAmount(
                            amountForRewards,
                            DEFAULT_PERCENTAGE,
                            bindingSheet.tvRewardPercentage,
                            productV2.bankMasterRecord?.id,
                            Utils.itemList
                        )
                        bindingSheet.tvRewardPercentage.visibility = View.VISIBLE
                        bindingSheet.tvAtTheRate.visibility = View.VISIBLE
                        bindingSheet.llRewards.visibility = View.VISIBLE
                        MonthlyEarnRule.setRewardLimit(
                            rewardsCanAssign,
                            rewardsAssignUpto,
                            rewardsPoint,
                            bindingSheet.tvRewards,
                            bindingSheet.tvAtTheRate,
                            bindingSheet.tvRewardPercentage,
                            bindingSheet.tvRewardEarned,
                            requireContext()
                        )

                    }

                    bindingSheet.btnOkay.isEnabled = true
                    val amount = p0.toString().replace(",", "").toDouble()
                    if (amount <= 1000000) {
                        bindingSheet.llMessageError.visibility = View.GONE
                    }
                } else {
                    bindingSheet.llMessageError.visibility = View.GONE
                    bindingSheet.btnOkay.isEnabled = false
                    bindingSheet.llRewards.visibility = View.GONE
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        bindingSheet.btnOkay.setOnClickListener {
            billStatus = CUSTOM
            if (!bindingSheet.etAmount.text.toString().isNullOrEmpty()) {
                if (bindingSheet.etAmount.text.toString().replace(",", "").toDouble() < 10) {
                    bindingSheet.llMessageError.visibility = View.VISIBLE
                    bindingSheet.tvError.text = getString(R.string.str_minimum_amount)

                } else if (bindingSheet.etAmount.text.toString().replace(",", "")
                        .toDouble() > 1000000
                ) {
                    bindingSheet.llMessageError.visibility = View.VISIBLE
                    bindingSheet.tvError.text = getString(R.string.str_amount_less_1000000)
                } else {
                    bindingSheet.llMessageError.visibility = View.GONE
                    val amount = bindingSheet.etAmount.text.toString().replace(",", "")
                    productV2.customAmount = amount.toDouble()
                    productV2.rewardsPoint = rewardsPoint
                    productV2.billStatus = billStatus
                    val selectedProduct: ArrayList<ProductV2> = ArrayList()
                    selectedProduct.add(productV2)

                    requireActivity().startActivity(
                        Intent(requireContext(), PaymentMethodsActivity::class.java).putExtra(
                            IntentKeys.PRODUCT_LIST, selectedProduct
                        ).putExtra(IntentKeys.IS_PAY_TOGETHER, false)
                            .putExtra(IntentKeys.TOTAL_AMOUNT, amount.trim())
                            .putExtra(IntentKeys.REWARDS_POINT, rewardsPoint)
                            .putExtra(IntentKeys.BILL_STATUS, billStatus)
                            .putExtra(IntentKeys.REWARDS_ASSIGN_UPTO, rewardsAssignUpto)
                            .putExtra(IntentKeys.REWARDS_CAN_ASSIGN, rewardsCanAssign)
                            .putExtra(IntentKeys.REWARDS_ASSIGNED, rewardsAssigned)
                    )
                    isPartialPayTogether = false
                    Utils.hideKeyboard(activity)


                }
            } else {
                bindingSheet.tvError.text = getString(R.string.str_minimum_amount)
            }
        }
        dialog.show()
    }

    override fun onEditAmountClick(productV2: ProductV2, position: Int) {
        if (productV2.bill != null) {
            if (productV2.product_type == "CC") {
                openTotalDueBottomSHeet(productV2, position)
            } else {
                openTotalDueBottomSheetLoan(productV2, position)
            }

        } else {
            openCustomPaymentBottomSheet(productV2, position)
        }
    }

    private fun openTotalDueBottomSheetLoan(productV2: ProductV2, position: Int) {
        val dialog = Utils.showCustomDialogBottum(activity, R.layout.bottom_sheet_total_due_loan)
        dialog.setCancelable(false)
        val ivCancel = dialog.findViewById<FrameLayout>(R.id.ivCancel)
        ivCancel.setOnClickListener {
            dialog.dismiss()
            Utils.hideKeyboard(activity)
            if ((toggleProductList[position].customAmount?.toInt() ?: 0) <= 0) {
                toggleProductList[position].isChecked = false
                combineBIllAdapter.notifyItemChanged(position)
            }
        }

        val ivBankImage = dialog.findViewById<AppCompatImageView>(R.id.iv_bank_image)
        val paymentAmountExactness = productV2.bankMasterRecord?.paymentAmountExactness
        imageUrl = "${prefs?.loanMasterUrl}${productV2.bankMasterRecord?.id}-logo-with-name.svg"
        ivBankImage.loadSvg(imageUrl)
        val ivMore = dialog.findViewById<AppCompatImageView>(R.id.ivMore)
        ivMore.setOnClickListener {
            openKnowMoreDialog(productV2.rewardRate)
            context?.let { it1 ->
                MparticleUtils.logEvent(
                    "CC_Payment_Rewards_KnowMore_Clicked",
                    "User clicks the information icon to learn more about the rewards on bill payment\n",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Rewards_KnowMore_Clicked),
                    it1
                )
            }
        }
        val tvCardNumber = dialog.findViewById<AppCompatTextView>(R.id.tvCardNumber)
        tvCardNumber.text = "··· " + productV2.last4
        val flSolidColor = dialog.findViewById<ImageView>(R.id.flSolidColor)
        if (productV2.bankMasterRecord?.ui_config != null) {

            flSolidColor.setBackgroundResource(R.drawable.loan_bottom_shap)
            val bg: Drawable? = flSolidColor?.background
            bg?.setTint(Color.parseColor(productV2.bankMasterRecord.ui_config.cardColor))

        }

        val tvBankName = dialog.findViewById<AppCompatTextView>(R.id.tvBankName)
        tvBankName.text = productV2.bankMasterRecord?.billerName

        val etAmount = dialog.findViewById<EditText>(R.id.etAmount)


        val tvCaption = dialog.findViewById<AppCompatTextView>(R.id.tvCaption)
        val tvRewards = dialog.findViewById<AppCompatTextView>(R.id.tvRewards)
        val tvRewardPercentage = dialog.findViewById<AppCompatTextView>(R.id.tvRewardPercentage)
        val tvAtTheRate = dialog.findViewById<AppCompatTextView>(R.id.tvAtTheRate)
        val tvRewardEarned = dialog.findViewById<AppCompatTextView>(R.id.tvRewardEarned)
        val chipTotalDue = dialog.findViewById<Chip>(R.id.chipTotalDue)
        val chipMinimumDue = dialog.findViewById<Chip>(R.id.chipMinimumDue)
        val customChip = dialog.findViewById<Chip>(R.id.chipCustom)

        val llMessageMinimumDue = dialog.findViewById<LinearLayoutCompat>(R.id.llMessageMinimumDue)
        val llMessageTotalDue = dialog.findViewById<LinearLayoutCompat>(R.id.llMessageTotalDue)
        val llAmtView = dialog.findViewById<LinearLayoutCompat>(R.id.llAmtView)
        val llMessageError = dialog.findViewById<LinearLayoutCompat>(R.id.llMessageError)
        val tvError = dialog.findViewById<TextView>(R.id.tvError)
        val llRewards = dialog.findViewById<LinearLayoutCompat>(R.id.llRewards)
        val btnOkay = dialog.findViewById<AppCompatButton>(R.id.btnOkay)
        if ((productV2.customAmount ?: 0.0) >= 100) {
            rewardsPoint = roundupAmount(
                productV2.customAmount ?: DEFAULT_PERCENTAGE,
                DEFAULT_PERCENTAGE,
                tvRewardPercentage,
                productV2.bankMasterRecord?.id,
                Utils.itemList
            )
            llRewards.visibility = View.VISIBLE
            MonthlyEarnRule.setRewardLimit(
                rewardsCanAssign,
                rewardsAssignUpto,
                rewardsPoint,
                tvRewards,
                tvAtTheRate,
                tvRewardPercentage,
                tvRewardEarned,
                requireContext()
            )

        } else if ((productV2.bill?.total_due ?: 0.0) < 100) {
            llRewards.visibility = View.VISIBLE
            tvRewards.text = getString(R.string.str_pay_total_bill)
        } else {
            llRewards.visibility = View.GONE
        }
        if (rewardsAssignUpto != 0) {
            tvRewardEarned.text =
                "$rewardsAssigned/$rewardsAssignUpto chips have been earned this month!"
        }

        if (totalDue > 0.0) {
            etAmount.setText(totalDue.let { Utils.getFormattedDecimal(it) })
        }

        chipTotalDue.setOnClickListener {
            billStatus = FULL
            productV2.billStatus = FULL
            etAmount.setText(Utils.getFormattedDecimal(productV2.total_due ?: 0.0))
            etAmount.isEnabled = false
            llMessageError.visibility = View.GONE
            llMessageTotalDue.visibility = View.VISIBLE
            llMessageMinimumDue.visibility = View.GONE
            tvCaption.text = resources.getText(R.string.you_have_to_pay)
            context?.let { it1 ->
                MparticleUtils.logEvent(
                    "CC_Payment_TotalDue_Clicked",
                    "User clicks the total due bubble to view total due amount\n",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_TotalDue_Clicked),
                    it1
                )
            }
            if ((productV2.bill?.total_due ?: 0.0) >= 100) {
                rewardsPoint = roundupAmount(
                    productV2.bill?.total_due ?: DEFAULT_PERCENTAGE,
                    DEFAULT_PERCENTAGE,
                    tvRewardPercentage,
                    productV2.bankMasterRecord?.id,
                    Utils.itemList
                )
                llRewards.visibility = View.VISIBLE
                MonthlyEarnRule.setRewardLimit(
                    rewardsCanAssign,
                    rewardsAssignUpto,
                    rewardsPoint,
                    tvRewards,
                    tvAtTheRate,
                    tvRewardPercentage,
                    tvRewardEarned,
                    requireContext()
                )

            } else if ((productV2.bill?.total_due ?: 0.0) < 100) {
                llRewards.visibility = View.VISIBLE
                tvRewards.text = getString(R.string.str_pay_total_bill)
            } else {
                llRewards.visibility = View.GONE
            }
        }
        if (paymentAmountExactness == "") {
            customChip.visibility = View.VISIBLE
            llMessageError.visibility = View.GONE
            if (productV2.bill?.total_due != null && productV2.bill.total_due > 0 && !productV2.bill.total_due.equals(
                    0
                )
            ) {
                chipTotalDue.visibility = View.VISIBLE
            } else {
                chipTotalDue.visibility = View.GONE
            }
            if (productV2.bill?.min_due != null && productV2.bill.min_due > 0 && !productV2.bill.min_due.equals(
                    0
                )
            ) {
                chipMinimumDue.visibility = View.VISIBLE
            } else {
                chipMinimumDue.visibility = View.GONE
            }
            etAmount.isEnabled = true
            etAmount.requestFocus()
        } else if (paymentAmountExactness == "EXACT_AND_BELOW") {
            chipMinimumDue.visibility = View.GONE
            chipTotalDue.visibility = View.VISIBLE
            customChip.visibility = View.VISIBLE


        } else if (paymentAmountExactness == "EXACT_AND_ABOVE") {
            chipMinimumDue.visibility = View.GONE
            chipTotalDue.visibility = View.VISIBLE
            customChip.visibility = View.VISIBLE

        } else if (paymentAmountExactness == "EXACT") {
            chipMinimumDue.visibility = View.GONE
            chipTotalDue.visibility = View.GONE
            customChip.visibility = View.VISIBLE
            etAmount?.isEnabled = true
        } else {
            etAmount?.isEnabled = false
            customChip.visibility = View.VISIBLE
            chipMinimumDue.visibility = View.GONE
            chipTotalDue.visibility = View.VISIBLE
            llMessageError.visibility = View.GONE
            llMessageError.visibility = View.GONE
        }
        if ((productV2.total_due ?: 0.0) > 0) {
            chipTotalDue.visibility = View.VISIBLE
            etAmount?.isEnabled = false

        }
        etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (p0.toString().isNotEmpty()) {
                    val amount = p0.toString().replace(",", "").toDouble()
                    if (amount > 0 && amount < 100) {
                        tvRewards.text = getString(R.string.str_enter_100)
                        tvRewardPercentage.visibility = View.GONE
                        tvAtTheRate.visibility = View.GONE
                        llRewards.visibility = View.VISIBLE
                    } else {
                        rewardsPoint = roundupAmount(
                            amount,
                            DEFAULT_PERCENTAGE,
                            tvRewardPercentage,
                            productV2.bankMasterRecord?.id,
                            Utils.itemList
                        )
                        tvRewardPercentage.visibility = View.VISIBLE
                        tvAtTheRate.visibility = View.VISIBLE
                        llRewards.visibility = View.VISIBLE
                        MonthlyEarnRule.setRewardLimit(
                            rewardsCanAssign,
                            rewardsAssignUpto,
                            rewardsPoint,
                            tvRewards,
                            tvAtTheRate,
                            tvRewardPercentage,
                            tvRewardEarned,
                            requireContext()
                        )

                    }
                    if (amount > totalDue) {
                        tvError.text = getString(R.string.str_enter_amount_is_more)
                        llMessageError.visibility = View.VISIBLE
                        btnOkay.isEnabled = true
                    } else if (amount < 1) {
                        llMessageError.visibility = View.VISIBLE
                        tvError.text = getString(R.string.str_minimum_amount_one)
                        btnOkay.isEnabled = false
                    } else if (amount > 500000) {
                        llMessageError.visibility = View.VISIBLE
                        tvError.text = getString(R.string.str_amount_less_500000)
                        btnOkay.isEnabled = false
                    } else {
                        llMessageError.visibility = View.GONE
                        btnOkay.isEnabled = true
                    }

                    val amountForRewards = p0.toString().replace(",", "").toInt()
                    rewardsPoint = roundupAmount(
                        amountForRewards.toDouble(),
                        DEFAULT_PERCENTAGE,
                        tvRewardPercentage,
                        productV2.bankMasterRecord?.id,
                        Utils.itemList
                    )
                } else {
                    llMessageError.visibility = View.GONE
                    btnOkay.isEnabled = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        etAmount.setOnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                llAmtView.setBackgroundResource(R.drawable.et_btm_bg)
            } else {
                llAmtView.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
                context?.let {
                    MparticleUtils.logEvent(
                        "CC_Payment_Custom_Entered",
                        "User enters a custom amount for bill payment\n",
                        "Unique",
                        "Input Field",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Custom_Entered),
                        it
                    )
                }
            }
        }

        if (productV2.bill?.min_due != null) {
            chipMinimumDue.setOnClickListener {
                billStatus = MIN
                if (productV2.bill.min_due >= 100) {
                    rewardsPoint = roundupAmount(
                        productV2.bill.min_due,
                        DEFAULT_PERCENTAGE,
                        tvRewardPercentage,
                        productV2.bankMasterRecord?.id,
                        Utils.itemList
                    )
                    llRewards.visibility = View.VISIBLE
                    MonthlyEarnRule.setRewardLimit(
                        rewardsCanAssign,
                        rewardsAssignUpto,
                        rewardsPoint,
                        tvRewards,
                        tvAtTheRate,
                        tvRewardPercentage,
                        tvRewardEarned,
                        requireContext()
                    )

                } else if (productV2.bill.min_due < 100) {
                    llRewards.visibility = View.VISIBLE
                    tvRewards.text = getString(R.string.str_pay_total_bill)
                } else {
                    llRewards.visibility = View.GONE
                }
                etAmount.setText(Utils.getFormattedDecimal(productV2.bill.min_due.toDouble()))
                etAmount.isEnabled = false
                llMessageError.visibility = View.GONE
                llMessageTotalDue.visibility = View.GONE
                llMessageMinimumDue.visibility = View.VISIBLE
                tvCaption.text = resources.getText(R.string.you_have_to_pay)
                context?.let { it1 ->
                    MparticleUtils.logEvent(
                        "CC_Payment_MinimumDue_Clicked",
                        "User clicks the minimum due bubble to view minimum due amount\n",
                        "Unique",
                        "Continue",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_MinimumDue_Clicked),
                        it1
                    )
                }
            }
        } else {
            chipMinimumDue.visibility = View.GONE
        }
        customChip.setOnClickListener {
            billStatus = CUSTOM
            etAmount.isEnabled = true
            etAmount.requestFocus()
            etAmount.setText("")
            btnOkay.isEnabled = false
            llMessageTotalDue.visibility = View.GONE
            llMessageMinimumDue.visibility = View.GONE
            tvCaption.text = getString(R.string.str_enter_amount)
            activity?.let { it1 -> Utils.showKeyboard(it1) }
            activity?.let { it1 ->
                MparticleUtils.logEvent(
                    "CC_Payment_Custom_Clicked",
                    "User clicks the custom bumble to enter a custom amount for bill payment\n",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Custom_Clicked),
                    it1
                )
            }
        }

        btnOkay.setOnClickListener {
            if (etAmount.text.toString().replace(",", "").toDouble() < 10) {
                llMessageError.visibility = View.VISIBLE
                tvError.text = getString(R.string.str_minimum_amount)

            } else {
                total = 0.0
                val amount = etAmount.text.toString().replace(",", "").toString()
                toggleProductList[position].customAmount = amount.toDouble()
                toggleProductList[position].rewardsPoint = rewardsPoint
                toggleProductList[position].billStatus = billStatus
                combineBIllAdapter.notifyItemChanged(position)
                for (i in toggleProductList) {
                    if (i.isChecked) {
                        if (i.customAmount != null) total += i.customAmount ?: 0.0 else 0
                    }
                }
                setTotalTextView(total)
                Utils.hideKeyboard(activity)
                dialog.dismiss()

            }
        }
        dialog.show()
    }

    private fun openCustomPaymentBottomSheet(productV2: ProductV2, position: Int) {
        val dialog =
            Utils.showCustomDialogBottum(activity, R.layout.bottom_sheet_edit_amount_custom)
        val bindingSheet = DataBindingUtil.inflate<BottomSheetEditAmountCustomBinding>(
            layoutInflater, R.layout.bottom_sheet_edit_amount_custom, null, false
        )
        imageUrl = "${prefs?.bankMasterUrl}${productV2.bankMasterRecord?.id}-logo-with-name.svg"

        dialog.setContentView(bindingSheet.root)
        dialog.setCancelable(false)
        activity?.let { Utils.showKeyboard(it) }

        bindingSheet.ivCancel.setOnClickListener {
            dialog.dismiss()
            Utils.hideKeyboard(activity)
            if (toggleProductList[position].customAmount == 0.0) {
                toggleProductList[position].isChecked = false
                combineBIllAdapter.notifyItemChanged(position)
            } else {
                toggleProductList[position].isChecked = true
                combineBIllAdapter.notifyItemChanged(position)
            }
        }
        bindingSheet.llRewards.visibility = View.GONE
        bindingSheet.etAmount.setOnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                bindingSheet.llAmtView.setBackgroundResource(R.drawable.et_btm_bg)
            } else {
                bindingSheet.llAmtView.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
            }
        }
        if (rewardsAssignUpto != 0) {
            bindingSheet.tvRewardEarned.text =
                "$rewardsAssigned/$rewardsAssignUpto chips have been earned this month!"
        }
        GradientUtils.setBoarderStroke(
            productV2.bankMasterRecord?.ui_config?.cardColor ?: "#FFFFFF",
            productV2.bankMasterRecord?.ui_config?.opacity_border ?: "#FFFFFF",
            true,
            bindingSheet.llCreditCard.flStroke
        )
        GradientUtils.setBackGround(
            productV2.bankMasterRecord?.ui_config?.cardColor ?: "#FFFFFF",
            "",
            productV2.bankMasterRecord?.ui_config?.opacity_topLeft ?: "#FFFFFF",
            productV2.bankMasterRecord?.ui_config?.opacity_bottomRight ?: "#FFFFFF",
            bindingSheet.llCreditCard.llCardSolidBackGround
        )
        bindingSheet.llCreditCard.ivBankImage.loadSvg(imageUrl)
        bindingSheet.ivMore.setOnClickListener {
            openKnowMoreDialog(productV2.rewardRate)
        }
        bindingSheet.llCreditCard.tvBankName.text = productV2.bankMasterRecord?.bankName ?: ""
        bindingSheet.llCreditCard.tvCardNumber.text = "··· " + (productV2.last4)
        bindingSheet.etAmount.setText("")
        bindingSheet.etAmount.requestFocus()

        when (productV2.card_network) {
            MASTER_CARD -> {
                bindingSheet.llCreditCard.ivCardType.setImageResource(R.drawable.ic_mastercard)
            }
            VISA -> {
                bindingSheet.llCreditCard.ivCardType.setImageResource(R.drawable.visa)
            }
            AMERICAN_CARD -> {
                bindingSheet.llCreditCard.ivCardType.setImageResource(R.drawable.ic_american_icon)
            }
            DINERS_CARD -> {
                bindingSheet.llCreditCard.ivCardType.setImageResource(R.drawable.ic_dinner_icon)
            }
            RUPAY_CARD -> {
                bindingSheet.llCreditCard.ivCardType.setImageResource(R.drawable.ic_rupay_card_icon)
            }
        }

        bindingSheet.etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()) {
                    bindingSheet.llRewards.visibility = View.VISIBLE
                    val amountForRewards = p0.toString().toDouble()
                    if (amountForRewards > 0 && amountForRewards < 100) {
                        bindingSheet.tvRewards.text = getString(R.string.str_enter_100)
                        bindingSheet.tvRewardPercentage.visibility = View.GONE
                        bindingSheet.tvAtTheRate.visibility = View.GONE
                        bindingSheet.llRewards.visibility = View.VISIBLE

                    } else {
                        rewardsPoint = roundupAmount(
                            amountForRewards,
                            DEFAULT_PERCENTAGE,
                            bindingSheet.tvRewardPercentage,
                            productV2.bankMasterRecord?.id,
                            Utils.itemList
                        )
                        bindingSheet.tvRewardPercentage.visibility = View.VISIBLE
                        bindingSheet.tvAtTheRate.visibility = View.VISIBLE
                        bindingSheet.llRewards.visibility = View.VISIBLE
                        MonthlyEarnRule.setRewardLimit(
                            rewardsCanAssign,
                            rewardsAssignUpto,
                            rewardsPoint,
                            bindingSheet.tvRewards,
                            bindingSheet.tvAtTheRate,
                            bindingSheet.tvRewardPercentage,
                            bindingSheet.tvRewardEarned,
                            requireContext()
                        )

                    }

                    bindingSheet.btnOkay.isEnabled = true
                    val amount = p0.toString().replace(",", "").toDouble()
                    if (amount > 1000000) {
                        bindingSheet.llMessageError.visibility = View.VISIBLE
                        bindingSheet.tvError.text = getString(R.string.str_amount_less_1000000)
                        bindingSheet.btnOkay.isEnabled = false
                    } else {
                        bindingSheet.llMessageError.visibility = View.GONE
                        bindingSheet.btnOkay.isEnabled = true
                    }
                } else {
                    bindingSheet.llRewards.visibility = View.GONE
                    bindingSheet.llMessageError.visibility = View.GONE
                    bindingSheet.btnOkay.isEnabled = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        bindingSheet.btnOkay.setOnClickListener {
            if (bindingSheet.etAmount.text.toString().replace(",", "").toDouble() < 10) {
                bindingSheet.llMessageError.visibility = View.VISIBLE
                bindingSheet.tvError.text = getString(R.string.str_minimum_amount)

            } else {
                total = 0.0
                var inputAmout = bindingSheet.etAmount.text.trim().toString()
                toggleProductList[position].customAmount = inputAmout.toDouble()
                toggleProductList[position].rewardsPoint = rewardsPoint
                toggleProductList[position].billStatus = CUSTOM
                combineBIllAdapter.notifyItemChanged(position)
                for (i in toggleProductList) {
                    if (i.isChecked) {

                        if (i.customAmount != null) total += i.customAmount ?: 0.0 else 0
                    }
                }
                setTotalTextView(total)
                mBinding.tvDueAmountTopTwo.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.colorTextTitle
                    )
                )
                dialog.dismiss()

            }

        }
        dialog.show()
    }

    private fun openTotalDueBottomSHeet(productV2: ProductV2, position: Int) {
        val dialog = Utils.showCustomDialogBottum(activity, R.layout.bottom_sheet_total_due)
        dialog.setCancelable(false)
        val ivCancel = dialog.findViewById<FrameLayout>(R.id.ivCancel)

        ivCancel.setOnClickListener {
            dialog.dismiss()
            Utils.hideKeyboard(activity)
            if (toggleProductList[position].customAmount == 0.0) {
                toggleProductList[position].isChecked = false
                combineBIllAdapter.notifyItemChanged(position)
            } else {
                toggleProductList[position].isChecked = true
                combineBIllAdapter.notifyItemChanged(position)
            }
        }

        val ivCardType = dialog.findViewById<AppCompatImageView>(R.id.ivCardType)
        val ivBankImage = dialog.findViewById<AppCompatImageView>(R.id.iv_bank_image)
        val tvRewardPercentage = dialog.findViewById<AppCompatTextView>(R.id.tvRewardPercentage)
        val tvAtTheRate = dialog.findViewById<AppCompatTextView>(R.id.tvAtTheRate)
        imageUrl = "${prefs?.bankMasterUrl}${productV2.bankMasterRecord?.id}-logo-with-name.svg"
        ivBankImage.loadSvg(imageUrl)
        val ivMore = dialog.findViewById<AppCompatImageView>(R.id.ivMore)
        ivMore.setOnClickListener {
            openKnowMoreDialog(productV2.rewardRate)
            context?.let { it1 ->
                MparticleUtils.logEvent(
                    "CC_Payment_Rewards_KnowMore_Clicked",
                    "User clicks the information icon to learn more about the rewards on bill payment\n",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Rewards_KnowMore_Clicked),
                    it1
                )
            }
        }
        val tvCardNumber = dialog.findViewById<AppCompatTextView>(R.id.tvCardNumber)
        tvCardNumber.text = productV2.last4
        val tvBankName = dialog.findViewById<AppCompatTextView>(R.id.tvBankName)
        tvBankName.text = productV2.bankMasterRecord?.bankName ?: ""

        val etAmount = dialog.findViewById<EditText>(R.id.etAmount)
        etAmount.setText(Utils.getFormattedDecimal(productV2.bill?.amount_paid?.let {
            productV2.bill.total_due.minus(
                it
            )
        } ?: 0.0))
        val tvRewardEarned = dialog.findViewById<AppCompatTextView>(R.id.tvRewardEarned)
        val tvCaption = dialog.findViewById<AppCompatTextView>(R.id.tvCaption)
        val tvRewards = dialog.findViewById<AppCompatTextView>(R.id.tvRewards)
        val chipTotalDue = dialog.findViewById<Chip>(R.id.chipTotalDue)
        val chipMinimumDue = dialog.findViewById<Chip>(R.id.chipMinimumDue)
        val flStroke = dialog.findViewById<FrameLayout>(R.id.flStroke)
        val customChip = dialog.findViewById<Chip>(R.id.chipCustom)

        val llMessageMinimumDue = dialog.findViewById<LinearLayoutCompat>(R.id.llMessageMinimumDue)
        val llCardSolidBackGround =
            dialog.findViewById<LinearLayoutCompat>(R.id.llCardSolidBackGround)
        val llMessageTotalDue = dialog.findViewById<LinearLayoutCompat>(R.id.llMessageTotalDue)
        val llAmtView = dialog.findViewById<LinearLayoutCompat>(R.id.llAmtView)
        val llMessageError = dialog.findViewById<LinearLayoutCompat>(R.id.llMessageError)
        val tvError = dialog.findViewById<TextView>(R.id.tvError)
        val llRewards = dialog.findViewById<LinearLayoutCompat>(R.id.llRewards)
        val ivMessageType = dialog.findViewById<AppCompatImageView>(R.id.ivMessageType)
        val tvInfo = dialog.findViewById<AppCompatTextView>(R.id.tvInfo)
        val btnOkay = dialog.findViewById<AppCompatButton>(R.id.btnOkay)
        billStatus = FULL
        productV2.billStatus = FULL
        if ((productV2.bill?.total_due ?: 0.0) >= 100) {
            rewardsPoint = roundupAmount(
                productV2.bill?.total_due?.minus(productV2.bill.amount_paid) ?: 0.0,
                DEFAULT_PERCENTAGE,
                tvRewardPercentage,
                productV2.bankMasterRecord?.id,
                Utils.itemList
            )
            llRewards.visibility = View.VISIBLE
            MonthlyEarnRule.setRewardLimit(
                rewardsCanAssign,
                rewardsAssignUpto,
                rewardsPoint,
                tvRewards,
                tvAtTheRate,
                tvRewardPercentage,
                tvRewardEarned,
                requireContext()
            )

        } else if ((productV2.bill?.total_due ?: 0.0) < 100) {
            llRewards.visibility = View.VISIBLE
            tvRewards.text = getString(R.string.str_pay_total_bill)
        } else {
            llRewards.visibility = View.GONE
        }
        if (rewardsAssignUpto != 0) {
            tvRewardEarned.text =
                "$rewardsAssigned/$rewardsAssignUpto chips have been earned this month!"
        }
        GradientUtils.setBoarderStroke(
            productV2.bankMasterRecord?.ui_config?.cardColor ?: "#FFFFFF",
            productV2.bankMasterRecord?.ui_config?.opacity_border ?: "#FFFFFF",
            true,
            flStroke
        )
        GradientUtils.setBackGround(
            productV2.bankMasterRecord?.ui_config?.cardColor ?: "#FFFFFF",
            "",
            productV2.bankMasterRecord?.ui_config?.opacity_topLeft ?: "#FFFFFF",
            productV2.bankMasterRecord?.ui_config?.opacity_bottomRight ?: "#FFFFFF",
            llCardSolidBackGround
        )
        chipTotalDue.setOnClickListener {
            billStatus = FULL
            productV2.billStatus = FULL
            tvInfo.text = getString(R.string.str_great_move)
            ivMessageType.setImageResource(R.drawable.ic_happy_face)
            etAmount.setText(Utils.getFormattedDecimal(productV2.bill?.amount_paid?.let {
                productV2.bill.total_due.minus(
                    it
                )
            } ?: 0.0))

            etAmount.isEnabled = false
            llMessageError.visibility = View.GONE
            llMessageTotalDue.visibility = View.VISIBLE
            llMessageMinimumDue.visibility = View.GONE
            tvCaption.text = resources.getText(R.string.you_have_to_pay)
            context?.let { it1 ->
                MparticleUtils.logEvent(
                    "CC_Payment_TotalDue_Clicked",
                    "User clicks the total due bubble to view total due amount\n",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_TotalDue_Clicked),
                    it1
                )
            }
            if ((productV2.bill?.total_due ?: 0.0) >= 100) {
                rewardsPoint = roundupAmount(
                    productV2.bill?.total_due?.minus(productV2.bill.amount_paid) ?: 0.0,
                    DEFAULT_PERCENTAGE,
                    tvRewardPercentage,
                    productV2.bankMasterRecord?.id,
                    Utils.itemList
                )
                llRewards.visibility = View.VISIBLE
                MonthlyEarnRule.setRewardLimit(
                    rewardsCanAssign,
                    rewardsAssignUpto,
                    rewardsPoint,
                    tvRewards,
                    tvAtTheRate,
                    tvRewardPercentage,
                    tvRewardEarned,
                    requireContext()
                )

            } else if ((productV2.bill?.total_due ?: 0.0) < 100) {
                llRewards.visibility = View.VISIBLE
                tvRewards.text = getString(R.string.str_pay_total_bill)
            } else {
                llRewards.visibility = View.GONE
            }
        }
        if (productV2.card_network == MASTER_CARD) {
            ivCardType.setImageResource(R.drawable.ic_mastercard)
        }
        if (productV2.card_network == VISA) {
            ivCardType.setImageResource(R.drawable.visa)
        }
        if (productV2.card_network == DINERS_CARD) {
            ivCardType.setImageResource(R.drawable.ic_dinner_icon)
        }
        if (productV2.card_network == AMERICAN_CARD) {
            ivCardType.setImageResource(R.drawable.ic_american_icon)
        }
        if (productV2.card_network == RUPAY_CARD) {
            ivCardType.setImageResource(R.drawable.ic_rupay_card_icon)
        }
        etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (p0.toString().isNotEmpty()) {
                    val amount = p0.toString().replace(",", "").toDouble()
                    if (amount > 0 && amount < 100) {
                        tvRewards.text = getString(R.string.str_enter_100)
                        tvRewardPercentage.visibility = View.GONE
                        tvAtTheRate.visibility = View.GONE
                        llRewards.visibility = View.VISIBLE
                    } else {
                        rewardsPoint = roundupAmount(
                            amount,
                            DEFAULT_PERCENTAGE,
                            tvRewardPercentage,
                            productV2.bankMasterRecord?.id,
                            Utils.itemList
                        )
                        tvRewardPercentage.visibility = View.VISIBLE
                        tvAtTheRate.visibility = View.VISIBLE
                        llRewards.visibility = View.VISIBLE
                        MonthlyEarnRule.setRewardLimit(
                            rewardsCanAssign,
                            rewardsAssignUpto,
                            rewardsPoint,
                            tvRewards,
                            tvAtTheRate,
                            tvRewardPercentage,
                            tvRewardEarned,
                            requireContext()
                        )
                    }
                    if (amount > totalDue) {
                        tvError.text = getString(R.string.str_enter_amount_is_more)
                        llMessageError.visibility = View.VISIBLE
                        llMessageTotalDue.visibility = View.GONE
                    } else if (amount < 1) {
                        llMessageError.visibility = View.VISIBLE
                        tvError.text = getString(R.string.str_minimum_amount_one)
                        btnOkay.isEnabled = false
                        llMessageError.visibility = View.GONE
                        llMessageTotalDue.visibility = View.GONE

                    } else if (amount > 500000) {
                        llMessageError.visibility = View.VISIBLE
                        tvError.text = getString(R.string.str_amount_less_500000)
                        btnOkay.isEnabled = false
                        llMessageTotalDue.visibility = View.GONE
                    } else {
                        llMessageError.visibility = View.GONE
                        btnOkay.isEnabled = true
                        llMessageTotalDue.visibility = View.VISIBLE
                    }

                    val amountForRewards = p0.toString().replace(",", "").toInt()
                    rewardsPoint = roundupAmount(
                        amountForRewards.toDouble(),
                        DEFAULT_PERCENTAGE,
                        tvRewardPercentage,
                        productV2.bankMasterRecord?.id,
                        Utils.itemList
                    )

                } else {
                    llMessageError.visibility = View.GONE
                    btnOkay.isEnabled = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        etAmount.setOnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                llAmtView.setBackgroundResource(R.drawable.et_btm_bg)
            } else {
                llAmtView.setBackgroundResource(R.drawable.et_btm_bg_un_focused)
                context?.let {
                    MparticleUtils.logEvent(
                        "CC_Payment_Custom_Entered",
                        "User enters a custom amount for bill payment\n",
                        "Unique",
                        "Input Field",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Custom_Entered),
                        it
                    )
                }
            }
        }

        if (productV2.bill?.min_due != null && productV2.bill.min_due.minus(productV2.bill.amount_paid) > 0) {
            chipMinimumDue.setOnClickListener {
                tvInfo.text = getString(R.string.str_you_will_incur)
                ivMessageType.setImageResource(R.drawable.ic_warning)
                billStatus = MIN
                if (productV2.bill.min_due >= 100) {
                    rewardsPoint = roundupAmount(
                        productV2.bill.min_due,
                        DEFAULT_PERCENTAGE,
                        tvRewardPercentage,
                        productV2.bankMasterRecord?.id,
                        Utils.itemList
                    )
                    llRewards.visibility = View.VISIBLE
                    MonthlyEarnRule.setRewardLimit(
                        rewardsCanAssign,
                        rewardsAssignUpto,
                        rewardsPoint,
                        tvRewards,
                        tvAtTheRate,
                        tvRewardPercentage,
                        tvRewardEarned,
                        requireContext()
                    )
                } else if (productV2.bill.min_due < 100) {
                    llRewards.visibility = View.VISIBLE
                    tvRewards.text = getString(R.string.str_pay_total_bill)
                } else {
                    llRewards.visibility = View.GONE
                }
                etAmount.setText(Utils.getFormattedDecimal(
                    productV2.bill.min_due.minus(productV2.bill.amount_paid)))
                etAmount.isEnabled = false
                llMessageError.visibility = View.GONE
                llMessageTotalDue.visibility = View.VISIBLE
                llMessageMinimumDue.visibility = View.VISIBLE
                tvCaption.text = resources.getText(R.string.you_have_to_pay)
                context?.let { it1 ->
                    MparticleUtils.logEvent(
                        "CC_Payment_MinimumDue_Clicked",
                        "User clicks the minimum due bubble to view minimum due amount\n",
                        "Unique",
                        "Continue",
                        SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_MinimumDue_Clicked),
                        it1
                    )
                }
            }
        } else {
            chipMinimumDue.visibility = View.GONE
        }
        customChip.setOnClickListener {
            tvInfo.text = getString(R.string.str_you_will_incur)
            ivMessageType.setImageResource(R.drawable.ic_warning)
            billStatus = CUSTOM
            etAmount.isEnabled = true
            etAmount.requestFocus()
            etAmount.setText("")
            btnOkay.isEnabled = false
            llMessageTotalDue.visibility = View.VISIBLE
            llMessageMinimumDue.visibility = View.GONE
            tvCaption.text = getString(R.string.str_enter_amount)
            activity?.let { it1 -> Utils.showKeyboard(it1) }
            activity?.let { it1 ->
                MparticleUtils.logEvent(
                    "CC_Payment_Custom_Clicked",
                    "User clicks the custom bumble to enter a custom amount for bill payment\n",
                    "Unique",
                    "Continue",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Custom_Clicked),
                    it1
                )
            }
        }

        btnOkay.setOnClickListener {
            if (etAmount.text.toString().replace(",", "").toDouble() < 10) {
                llMessageError.visibility = View.VISIBLE
                tvError.text = getString(R.string.str_minimum_amount)

            } else if (etAmount.text.toString().replace(",", "").toDouble() > 1000000) {
                llMessageError.visibility = View.VISIBLE
                tvError.text = getString(R.string.str_amount_less_1000000)
                llMessageError.visibility = View.VISIBLE

            } else {
                total = 0.0
                val amount = etAmount.text.toString().replace(",", "")
                toggleProductList[position].customAmount = amount.toDouble()
                toggleProductList[position].rewardsPoint = rewardsPoint
                toggleProductList[position].billStatus = billStatus
                combineBIllAdapter.notifyItemChanged(position)
                for (i in toggleProductList) {
                    if (i.isChecked) {
                        if (i.customAmount != null) total += i.customAmount ?: 0.0 else 0
                    }
                }
                setTotalTextView(total)
                Utils.hideKeyboard(activity)
                dialog.dismiss()

            }

        }
        dialog.show()
    }

    override fun onSwitchUnChecked(productV2: ProductV2, position: Int) {
    }

    override fun onSwitchedChecked(productV2: ProductV2, position: Int, isChecked: Boolean) {
        total = 0.0
        for (i in toggleProductList) {
            if (i.isChecked) {
                if (i.customAmount != null) total += i.customAmount ?: 0.0
            }
        }
        setTotalTextView(total)
    }

    private fun setTotalTextView(totalAmount: Double) {
        mBinding.tvDueAmountTopTwo.text =
            getString(R.string.str_rs_symbol, Utils.getFormattedDecimal(totalAmount))
        if (combineBIllAdapter.getSelectedProduct().size == 1) {
            activity?.mBinding?.btnPayTogether?.text = getString(R.string.pay_now)
        } else {
            activity?.mBinding?.btnPayTogether?.text = getString(R.string.str_pay_together_now)
        }
        if (totalAmount > 1000000) {
            activity?.setBtnVisibility(
                total, false, getString(R.string.str_pay_maximum)
            )
        } else if (totalAmount == 0.0) {
            activity?.setBtnVisibility(
                total, false, getString(R.string.str_select_one_product)
            )
        } else {
            activity?.setBtnVisibility(
                total, true, getString(R.string.str_pay_maximum)
            )
            isPartialPayTogether = true
        }
    }

    override fun onMenuClicked(productV2: ProductV2) {}

    override fun onRemovedProduct(productV2: ProductV2, position: Int) {
        itemRemoved = position
        val hideProductRequest = HideProductRequest(productV2.id)
        viewModel?.hideProduct(hideProductRequest)
    }

    private fun showRemovedMsg(message: String) {
        try {
            val snackbar = Snackbar.make(mBinding.mainScroll, "", Snackbar.LENGTH_SHORT)
            val customSnackView: View =
                layoutInflater.inflate(R.layout.custom_toast, null)
            val txtMsg = customSnackView.findViewById<AppCompatTextView>(R.id.txtMessage)
            val imageIcon = customSnackView.findViewById<AppCompatImageView>(R.id.ivRewardsEarned)
            txtMsg.text = message
            imageIcon.setImageResource(R.drawable.ic_toast_remove)
            snackbar.view.setBackgroundColor(Color.TRANSPARENT)
            var snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
            snackbarLayout.addView(customSnackView, 0)
            snackbar.anchorView = activity?.mBinding?.llBottomBar
            snackbar.show()
        } catch (exception: Exception) {
            Log.e(TAG, exception.message.toString())
        }
    }

    private fun openKnowMoreDialog(rewardRate: Double?) {
        val bottomSheetDialog = BottomSheetDialog(requireActivity())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_know_more)
        bottomSheetDialog.setCancelable(false)
        val btnSubmit = bottomSheetDialog.findViewById<AppCompatImageView>(R.id.ivCancel)
        val info_Image = bottomSheetDialog.findViewById<ImageView>(R.id.info_Image)
        mFirebaseDatabase?.child("payment_info_icon")?.get()?.addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            val options = RequestOptions().placeholder(R.drawable.button_white_bordered)
                .error(R.drawable.button_white_bordered)



            if (info_Image != null) {
                Glide.with(this).load(it.value).apply(options).into(info_Image)
            }
        }?.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }

        btnSubmit?.setOnClickListener {
            bottomSheetDialog.dismiss()
            activity?.let { it1 -> Utils.showKeyboard(it1) }
        }
        bottomSheetDialog.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                MparticleUtils.logEvent(
                    "CC_Payment_Rewards_KnowMore_BackClicked",
                    "User closes the window for rewards description by clicking the bottom CTA with rewards description",
                    "Unique",
                    "Back",
                    SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CC_Payment_Rewards_KnowMore_BackClicked),
                    requireActivity()
                )
            }
            true
        }

        Utils.hideKeyboard(activity)
        bottomSheetDialog.show()
    }

    override fun onProductAction(isSelected: Boolean?) {
    }

}

interface ProductAdapterListener {
    fun onEmandate()
    fun onActiveClick(
        productId: String?,
        last4: String?,
        bankLogo: String?,
        bankName: String?,
        startColor: String,
        middleColor: String,
        endColor: String,
        item: ProductV2
    )

    fun onActiveAndPayClick(
        productId: String?,
        last4: String?,
        bankLogo: String?,
        bankName: String?,
        startColor: String,
        middleColor: String,
        endColor: String,
        item: ProductV2
    )

    fun onPayNowCLick(
        productV2: ProductV2
    )

    fun onLoanActivateClick(
        productId: String, loan: ProductV2
    )

    fun onLoanPayClick(
        accountNumber: String?,
        bankName: String?,
        cardType: String?,
        bankLogo: String?,
        minDue: Double?,
        totalDue: Int?,
        billId: String?,
        productId: String?,
        paidAmount: Double?,
        lastFour: String?,
        startColor: String,
        middleColor: String,
        endColor: String,
        item: ProductV2
    )

    fun onPayMoreCLick(
        productV2: ProductV2
    )

    fun onEditAmountClick(productV2: ProductV2, position: Int)

    fun onSwitchUnChecked(productV2: ProductV2, position: Int)

    fun onSwitchedChecked(productV2: ProductV2, position: Int, isChecked: Boolean)

    fun onMenuClicked(productV2: ProductV2)

    fun onRemovedProduct(productV2: ProductV2, position: Int)
}


