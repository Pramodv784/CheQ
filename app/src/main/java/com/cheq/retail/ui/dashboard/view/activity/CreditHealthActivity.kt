package com.cheq.retail.ui.dashboard.view.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.cheq.retail.R
import com.cheq.retail.application.MainApplication
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivityCreditHealthBinding
import com.cheq.retail.sharePreferences.SharePrefs
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.ui.dashboard.model.ChartEntity
import com.cheq.retail.ui.dashboard.model.creditDashBoard.CreditDashBoardResponse
import com.cheq.retail.ui.dashboard.model.homedashboad.CreditDashBoardProductModel
import com.cheq.retail.ui.dashboard.model.homedashboad.CreditHealthWidget
import com.cheq.retail.ui.dashboard.repository.CreditRepository
import com.cheq.retail.ui.dashboard.view.adapter.CreditProductAdapter
import com.cheq.retail.ui.dashboard.viewModel.CreditDashBoardViewModelFactory
import com.cheq.retail.ui.dashboard.viewModel.CreditViewModel
import com.cheq.retail.ui.main.MainActivity
import com.cheq.retail.ui.main.viewModel.ProductViewModel
import com.cheq.retail.ui.rewards.util.State
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils
import com.google.android.material.snackbar.Snackbar
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CreditHealthActivity : BaseActivity() {
    lateinit var viewModel: CreditViewModel
    lateinit var homeViewModel: ProductViewModel
    private lateinit var repository: CreditRepository

    var binding: ActivityCreditHealthBinding? = null
    val graph1 = floatArrayOf(600f, 700f, 750f, 822f, 840f, 900f)
    val legend = arrayListOf("APR", "MAY", "JUN", "JUL", "AUG", "SEP")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setLightStatusBar(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_credit_health)
        // val firstChartEntity = ArrayList<Float>()
        val legendArr = ArrayList<String>()
        var coin = intent.getStringExtra("coin")
        binding?.tvRewardPoints?.text = coin
        //   firstChartEntity.addAll(graph1)
        legendArr.addAll(legend)


        val firstChartItem = ChartEntity(Color.WHITE, Color.WHITE, graph1)


        val list2 = ArrayList<ChartEntity>().apply {
            add(firstChartItem)

        }
        binding?.ivBack?.setOnClickListener {
            var intent=Intent(this,MainActivity::class.java)
           // intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("from","")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)
            finish()
        }
        binding?.llRewards?.setOnClickListener {
         var intent=Intent(this,MainActivity::class.java)
           // intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("from","credit")
            startActivity(intent)
            finishAffinity()

        }

        binding?.tvReportAnError?.setOnClickListener {
            val firstName = SharePrefs.getInstance(MainApplication.getApplicationContext())?.getString(SharePrefsKeys.FIRST_NAME).toString()
            val lastName = SharePrefs.getInstance(MainApplication.getApplicationContext())?.getString(SharePrefsKeys.LAST_NAME).toString()
            val mobileNumber = SharePrefs.getInstance(MainApplication.getApplicationContext())?.getString(SharePrefsKeys.MOBILE_NUMBER).toString()
            createEmail(firstName, lastName, "consumer.support@in.experian.com", "Raising dispute for my Experian Credit Report", mobileNumber)
        }

        binding?.ivInfo?.setOnClickListener {
            val snackbar =
                binding?.layoutCreditHealthDashBoard?.let { it1 -> Snackbar.make(it1, "", Snackbar.LENGTH_INDEFINITE) }
            val customSnackView: View =
                layoutInflater.inflate(R.layout.custom_toast, null)
            val txtMessage = customSnackView.findViewById<TextView>(R.id.txtMessage)
            val ivClose = customSnackView.findViewById<AppCompatImageView>(R.id.ivCancel)
            val ivRewardsEarned = customSnackView.findViewById<AppCompatImageView>(R.id.ivRewardsEarned)
            ivRewardsEarned.setImageResource(R.drawable.ic_info_circle)
            txtMessage.text = getString(R.string.active_product_disabled_txt)
            snackbar?.view?.setBackgroundColor(Color.TRANSPARENT)
            val snackbarLayout = snackbar?.view as Snackbar.SnackbarLayout
            snackbarLayout.setPadding(0, 0, 0, 0)
            snackbarLayout.addView(customSnackView, 0)
            ivClose.visibility = View.VISIBLE
            ivClose.setOnClickListener {
                snackbar.dismiss()
            }
            snackbar.show()
        }
        binding?.linechart?.setLegend(legendArr)
        binding?.linechart?.setList(list2)
        setupViewModel()
        setupObserver()
//        val text = "<font color=#282828>You’re among top</font> " +
//                "<font color=#00C197>5% in India</font>"
//
//        val text2 = "<font color=#282828>You are in</font> " +
//                "<font color=#00C197>Top 15%</font>" +
//                "<font color=#282828> of people </font>" +
//                "<font color=#00C197>in your area</font>"
//
//        val text3 = "<font color=#282828>You’re among</font> " +
//                "<font color=#F6AD0B >67% in people like you</font>"
//
//        val text4 = "<font color=#282828>Your credit is</font> " +
//                "<font color=#00C197>855</font>"
//
//        binding?.tvComp1?.text = (Html.fromHtml(text))
//        binding?.tvComp2?.text = (Html.fromHtml(text2))
//        binding?.tvComp3?.text = (Html.fromHtml(text3))
//        binding?.tvComp4?.text = (Html.fromHtml(text4))

//        val firstChartEntity = ChartEntity(Color.WHITE, Color.WHITE, firstChartEntity)
//
//
//        val list = ArrayList<ChartEntity>().apply {
//            add(firstChartEntity)
//
//        }
//
//
//        binding?.linechart?.setLegend(legendArr)
//        binding?.linechart?.setList(list)
        MparticleUtils.logCurrentScreen(
            "/credit-dashboard",
            "The customer views the credit dashboard page",
            "",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.CREDIT_DASH),
            this
        )

    }

    private fun setupViewModel() {
        repository = CreditRepository(this)

        viewModel = ViewModelProvider(
            this, CreditDashBoardViewModelFactory(repository)
        )[CreditViewModel::class.java]

        homeViewModel =   ViewModelProvider(this)[ProductViewModel::class.java]


    }

    private fun setupObserver() {
        viewModel.getDashBoard()
//        viewModel.getDashBoardProduct()
//        viewModel.creditDashBoardProduct.observe(this) {
//            when (it) {
//                is State.NetworkError -> {
//                    Toast.makeText(this, it.netWorkMessage, Toast.LENGTH_SHORT).show()
//                    //   binding?.homeLayout?.visibility = View.GONE
//                    //  Utils.hideProgressDialog()
//                }
//                is State.Loading -> {
//                    //   binding?.homeLayout?.visibility = View.GONE
//                    //  Utils.showProgressDialog(this)
//                }
//                is State.Error -> {
//                    //  binding?.homeLayout?.visibility = View.GONE
//                    // Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
//                    //   Utils.hideProgressDialog()
//                }
//                is State.Success -> {
//                    //  binding?.homeLayout?.visibility = View.VISIBLE
//                    //   Utils.hideProgressDialog()
//                    binding?.layoutCreditHealthDashBoard?.visibility = View.VISIBLE
//                    setDataBoardDataProduct(it)
//                }
//
//            }
      //  }
        viewModel.creditDashBoard.observe(this) {
            when (it) {
                is State.NetworkError -> {
                    Toast.makeText(this, it.netWorkMessage, Toast.LENGTH_SHORT).show()
                    //   binding?.homeLayout?.visibility = View.GONE
                    Utils.hideProgressDialog()
                }
                is State.Loading -> {
                    //   binding?.homeLayout?.visibility = View.GONE
                    Utils.showProgressDialog(this)
                }
                is State.Error -> {
                    //  binding?.homeLayout?.visibility = View.GONE
                    // Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    Utils.hideProgressDialog()
                }
                is State.Success -> {
                    //  binding?.homeLayout?.visibility = View.VISIBLE
                    Utils.hideProgressDialog()
                    setDataBoardData(it)
                }

            }
        }
    }

    private fun setDataBoardDataProduct(it: ArrayList<CreditDashBoardProductModel?>) {

        binding?.recyclerviewCreditProduct?.adapter =
            CreditProductAdapter(it )
    }

    private fun setDataBoardData(it: State.Success<CreditDashBoardResponse>) {

        val response = it.data
        if (response.bureauReport != null) {
            setCreditWidget2(response.bureauReport)
                               binding?.layoutCreditHealthDashBoard?.visibility = View.VISIBLE
            if(it.data.bureauReport?.products==null || it.data.bureauReport.products.isEmpty()){
                binding?.tvYourProduct?.visibility=View.GONE
            }
            it.data.bureauReport?.products?.let { it1 -> setDataBoardDataProduct(it1) }
        }


    }

    private fun setCreditWidget2(creditHealthWidget: CreditHealthWidget?) {

        binding?.bureauProvider?.text = creditHealthWidget?.bureauProvider?.uppercase().orEmpty()
        creditHealthWidget?.let { setcreditRefreshDate(it) }
        var creditScoreColor = ""
        var creditScoreInterpretation = ""
        val creaditHealthText = creditHealthWidget?.creditScoreStateValueInterpretation
        val topValue = creditHealthWidget?.creditScoreStateValue
        val topValueBefore = creditHealthWidget?.creditScoreStateTemplate?.substringBefore("{{")
        val topValueAfter = creditHealthWidget?.creditScoreStateTemplate?.substringAfter("}}")
        when (creditHealthWidget?.creditScoreValue) {
            in 0..620 -> {
                creditScoreColor = "#F5466A"
                creditScoreInterpretation = "POOR"

            }
            in 620..699 -> {
                creditScoreColor = "#F6AD0B"
                creditScoreInterpretation = "FAIR"

            }
            in 700..749 -> {
                creditScoreColor = "#99DB6A"
                creditScoreInterpretation = "GOOD"

            }
            in 750..799 -> {
                creditScoreColor = "#00C197"
                creditScoreInterpretation = "VERY GOOD"

            }
            in 800..900 -> {
                creditScoreColor = "#03AC87"
                creditScoreInterpretation = "EXCELLENT"

            }
        }
        binding?.creditScoreInterpretation2?.text = creditScoreInterpretation
        binding?.creditScore2?.text = creditHealthWidget?.creditScoreValue.toString()
        binding?.creditScoreInterpretation2?.setTextColor(Color.parseColor(creditScoreColor))
        if (creditHealthWidget?.creditScoreGraph != null) {
            if (creditHealthWidget.creditScoreGraph.size>1) {
                binding?.yurCreditHealth?.visibility = View.VISIBLE
                binding?.layoutCreditHealth?.llCreditHealth?.visibility =View.GONE
                setCreditGraph(creditHealthWidget)
            } else {
                binding?.yurCreditHealth?.visibility ?: View.GONE
                binding?.layoutCreditHealth?.llCreditHealth?.visibility  = View.VISIBLE

                binding?.layoutCreditHealth?.tvbureauProvider?.text =
                    creditHealthWidget.bureauProvider
                binding?.layoutCreditHealth?.tvCreditScore?.text =
                    creditHealthWidget.creditScoreValue.toString()
                var creditScoreColor = ""
                var creditScoreText = ""
                var progressPercent = 0
                when (creditHealthWidget.creditScoreValue) {
                    in 0..620 -> {
                        creditScoreColor = "#F5466A"
                        creditScoreText = "POOR"
                        progressPercent = 20
                    }
                    in 620..699 -> {
                        creditScoreColor = "#F6AD0B"
                        creditScoreText = "FAIR"
                        progressPercent = 40
                    }
                    in 700..749 -> {
                        creditScoreColor = "#99DB6A"
                        creditScoreText = "GOOD"
                        progressPercent = 60
                    }
                    in 750..799 -> {
                        creditScoreColor = "#00C197"
                        creditScoreText = "VERY GOOD"
                        progressPercent = 80
                    }
                    in 800..900 -> {
                        creditScoreColor = "#03AC87"
                        creditScoreText = "EXCELLENT"
                        progressPercent = 90
                    }
                }
                binding?.layoutCreditHealth?.creditScoreText?.text = creditScoreText
                binding?.layoutCreditHealth?.creditScoreText?.setTextColor(
                    Color.parseColor(
                        creditScoreColor
                    )
                )
                binding?.layoutCreditHealth?.pbHealth?.setProgressTintList(
                    ColorStateList.valueOf(
                        Color.parseColor(
                            creditScoreColor
                        )
                    )
                )

                binding?.layoutCreditHealth?.pbHealth?.setProgress(progressPercent, true)

                if (creditHealthWidget.creditScoreStateValueTop!=null && creditHealthWidget.creditScoreStateValueTop) {
                    binding?.layoutCreditHealth?.rrTopCredit?.visibility = View.VISIBLE
                    binding?.layoutCreditHealth?.rrBottomCredit?.visibility = View.GONE
                    binding?.layoutCreditHealth?.rrTopTextOne?.text = topValueBefore?.replace("top","")
                    binding?.layoutCreditHealth?.tvTopInAreaTextTwo?.text = topValueAfter
                    binding?.layoutCreditHealth?.tvTopValue?.text = topValue
                } else {
                    binding?.layoutCreditHealth?.rrTopCredit?.visibility = View.GONE
                    binding?.layoutCreditHealth?.rrBottomCredit?.visibility = View.VISIBLE
                    binding?.layoutCreditHealth?.ivrrBottomCreditTwoTextOne?.text = topValueBefore
                    binding?.layoutCreditHealth?.ivrrBottomCreditTwoTextTwo?.text = topValueAfter
                    binding?.layoutCreditHealth?.tvBottomTextValue?.text = topValue
                }
            }
        }


//        binding?.tvCompareNational?.text=creditHealthWidget?.creditScoreNationalValueInterpretation
//        binding?.tvCompareArea?.text=creditHealthWidget?.creditScoreStateValueInterpretation
//        binding?.tvCompAge?.text=creditHealthWidget?.creditScoreAgeValueInterpretation
//        val text4 = "<font color=#282828>Your credit is</font> " +
//                "<font color=$creditScoreColor>${creditHealthWidget?.creditScoreValue}</font>"
//
//        if(creditHealthWidget!!.creditScoreNationalValueTop) binding?.ivCompareNational?.setImageResource(R.drawable.iv_thumps_back) else binding?.ivCompareNational?.setImageResource(R.drawable.iv_thumps_down)
//        if(creditHealthWidget!!.creditScoreStateValueTop) binding?.ivCompArea?.setImageResource(R.drawable.iv_thumps_back) else binding?.ivCompArea?.setImageResource(R.drawable.iv_thumps_down)
//        if(creditHealthWidget!!.creditScoreAgeValueTop) binding?.ivCompAge?.setImageResource(R.drawable.iv_thumps_back) else binding?.ivCompAge?.setImageResource(R.drawable.iv_thumps_down)

        //  binding?.tvCompCredit?.text = (Html.fromHtml(text4))

        binding?.paymentHistoryImpression?.text = creditHealthWidget?.paymentHistoryInterpretation
        if ( creditHealthWidget?.paymentHistoryInterpretation!=null) {
            setPaymentHistory( creditHealthWidget?.paymentHistoryInterpretation)
            binding?.paymentHistoryValue?.text =
                creditHealthWidget?.paymentHistoryValue?.toInt().toString() + "%"
        }

        if (creditHealthWidget != null && creditHealthWidget.ccUtilizationAvgInterpretation!=null) {
            setCardUtilasation(creditHealthWidget.ccUtilizationAvgInterpretation)
        }

        if ((creditHealthWidget?.ccUtilizationAvgValue ?: 0.0) >= 0.0) {
            binding?.paymentUtillValue?.text =
                creditHealthWidget?.ccUtilizationAvgValue?.toInt().toString() + "%"
        } else {
            binding?.paymentUtillValue?.text = "0%"
        }
        creditHealthWidget?.creditAgeInterpretation?.let { setCreditAge(it) }
        binding?.paymentageValue?.text = creditHealthWidget?.creditAgeValue.toString() + " years"


    }

    private fun setPaymentHistory(paymentHistoryInterpretation: String) {
        when (paymentHistoryInterpretation) {
            "Risk" -> {
                binding?.paymentHistoryImpression?.setTextColor(Color.parseColor("#F5466A"))
            }
            "Weak" -> {
                binding?.paymentHistoryImpression?.setTextColor(Color.parseColor("#F5466A"))
            }
            "Average" -> {
                binding?.paymentHistoryImpression?.setTextColor(Color.parseColor("#F6AD0B"))
            }
            "Medium" -> {
                binding?.paymentHistoryImpression?.setTextColor(Color.parseColor("#F6AD0B"))
            }
            "Excellent" -> {
                binding?.paymentHistoryImpression?.setTextColor(Color.parseColor("#00C197"))
            }
            "Strong" -> {
                binding?.paymentHistoryImpression?.setTextColor(Color.parseColor("#00C197"))
            }
        }

    }

    private fun setCardUtilasation(cardUtilisation: String) {
        binding?.paymentUtillImpression?.text = cardUtilisation
        when (cardUtilisation) {
            "Risk" -> {
                binding?.paymentUtillImpression?.setTextColor(Color.parseColor("#F5466A"))
            }
            "Weak" -> {
                binding?.paymentUtillImpression?.setTextColor(Color.parseColor("#F5466A"))
            }
            "Average" -> {
                binding?.paymentUtillImpression?.setTextColor(Color.parseColor("#F6AD0B"))
            }
            "Medium" -> {
                binding?.paymentUtillImpression?.setTextColor(Color.parseColor("#F6AD0B"))
            }
            "Excellent" -> {
                binding?.paymentUtillImpression?.setTextColor(Color.parseColor("#00C197"))
            }
            "Strong" -> {
                binding?.paymentUtillImpression?.setTextColor(Color.parseColor("#00C197"))
            }
        }

    }

    private fun setCreditAge(CardAge: String) {
        binding?.paymentageImpression?.text = CardAge
        when (CardAge) {
            "Risk" -> {
                binding?.paymentageImpression?.setTextColor(Color.parseColor("#F5466A"))
            }
            "Weak" -> {
                binding?.paymentageImpression?.setTextColor(Color.parseColor("#F5466A"))
            }
            "Average" -> {
                binding?.paymentageImpression?.setTextColor(Color.parseColor("#F6AD0B"))
            }
            "Medium" -> {
                binding?.paymentageImpression?.setTextColor(Color.parseColor("#F6AD0B"))
            }
            "Excellent" -> {
                binding?.paymentageImpression?.setTextColor(Color.parseColor("#00C197"))
            }
            "Strong" -> {
                binding?.paymentageImpression?.setTextColor(Color.parseColor("#00C197"))
            }
        }

    }

    private fun setCreditGraph(creditHealthWidget: CreditHealthWidget?) {
        creditHealthWidget?.creditScoreGraph
        if (creditHealthWidget?.creditScoreGraph != null) {
            val firstChartEntity = ArrayList<Float>()
            val legendArr = ArrayList<String>()
            for (item in creditHealthWidget.creditScoreGraph) {
                firstChartEntity.add(item.y.toFloat())
                legendArr.add(item.x)


                val firstChartItem =
                    ChartEntity(Color.WHITE, Color.WHITE, firstChartEntity.toFloatArray())


                val list2 = ArrayList<ChartEntity>().apply {
                    add(firstChartItem)


                }


                binding?.linechart?.setLegend(legendArr)
                binding?.linechart?.setList(list2)
            }


        }
    }

    private fun setcreditRefreshDate(creditHealthWidget: CreditHealthWidget) {
        val inputPattern = "yyyy-MM-dd";
        val outputPattern = "MMM dd";
        val inputFormat = SimpleDateFormat(inputPattern);
        val outputFormat = SimpleDateFormat(outputPattern);
        var date: Date? = null
        var creditScoreDate: String? = null
        try {
            date = creditHealthWidget.updatedAt.substringBefore("T").let { inputFormat.parse(it) }
            creditScoreDate = outputFormat.format(date)
            binding?.creditScoreRefreshDate?.text = creditScoreDate
        } catch (e: ParseException) {
            e.printStackTrace()
        }

    }

    fun createEmail(firstName : String, lastName : String, emailTo : String, subject : String, mobileNumber : String){
        val body = "Hello,\n" +
                "\n" +
                "In my Experian credit report fetched by CheQ, I have few issue(s) that I wish to highlight below:\n" +
                "\n" +
                "Full Name: $firstName $lastName\n" +
                "Mobile number: $mobileNumber\n" +
                "\n" +
                "<Delete this line and add your concern here>\n" +
                "\n" +
                "Requesting you to kindly rectify and reply back.\n" +
                "\n" +
                "Best regards, \n" +
                firstName
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailTo))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

}