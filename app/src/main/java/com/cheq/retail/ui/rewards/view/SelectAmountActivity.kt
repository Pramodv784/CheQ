package com.cheq.retail.ui.rewards.view

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.ContextCompat
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.databinding.ActivitySelectAmountBinding
import com.cheq.retail.sharePreferences.SharePrefsKeys
import com.cheq.retail.sharePreferences.SharePrefsLog
import com.cheq.retail.utils.MparticleUtils
import com.cheq.retail.utils.Utils


class SelectAmountActivity : BaseActivity() {
     var _binding: ActivitySelectAmountBinding?=null
    val binding get() = _binding!!

    var defaultamount = 0
    var defaultCoin = 0
    var amount = 0
    var amount20 = 0
    var amount60 = 0
    var amount40 = 0
    var amount80 = 0
    var amount100 = 0
    var coinWorth = 0
    var convertToCashRate = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySelectAmountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        catchIntent()
        setUpUi()
    }

    private fun catchIntent() {
        amount = intent.getIntExtra("AMOUNT", 0)
        //   amount = 100
        convertToCashRate = intent.getDoubleExtra("convertToCashRate", 0.0)

        amount20 = (amount * 20) / 100
        amount40 = (amount * 40) / 100
        amount60 = (amount * 60) / 100
        println("amount60 $amount60")
        println("amount60 $amount")
        amount80 = (amount * 80) / 100
        amount100 = amount
    }

    private fun setUpUi() {
        Utils.setLightStatusBar(this)
        Utils.setColorStatusBar(ContextCompat.getColor(this,R.color.demo7), this)
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }


        binding.etAmount.setText("$defaultamount")
        binding.tvConversionRate.text = "${(1 / convertToCashRate!!).toInt()} = ₹1.00"
        coinWorth = (amount60 / convertToCashRate).toInt()
        binding.tvCoinWorth.text = "$defaultCoin used"
        binding.etAmount.filters = arrayOf(MinMaxFilter(0, amount))
        binding.tvMax.text = "₹$amount"
        binding.etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputAmount = s.toString()
                println("inputAmount $inputAmount")
                if (s?.isNotEmpty() == true) {
                    coinWorth = (inputAmount.toInt() / convertToCashRate).toInt()
                    println("coinWorth ${coinWorth}")
                    println("inputAmount ${inputAmount.toInt()}")
                    binding.tvCoinWorth.text =
                        "${(inputAmount.toInt() / convertToCashRate).toInt()} used"

                    if (inputAmount.toInt() > amount) {
                        Toast.makeText(
                            this@SelectAmountActivity, "Enter valid amount", Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        binding.btnC2C.isEnabled = false
                    }
                    binding.etAmount.setSelection(binding.etAmount.length())
                    binding.seekBaar.progress = inputAmount.toInt()
                }

            }

            override fun afterTextChanged(s: Editable?) {

                if (s?.length == 0) {
                    binding.etAmount.setText("0")
                    binding.seekBaar.progress = 0
                    binding.etAmount.setSelection(binding.etAmount.length())

                }
            }

        })


        binding.ivNoteOne.visibility = View.VISIBLE
        binding.ivNoteTwo.visibility = View.VISIBLE
        binding.ivNoteThree.visibility = View.VISIBLE
        binding.ivNoteFour.visibility = View.VISIBLE
        binding.ivNoteFive.visibility = View.VISIBLE
        binding.ivNoteOneSC.visibility = View.INVISIBLE
        binding.ivNoteTwoSC.visibility = View.INVISIBLE
        binding.ivNoteThreeSC.visibility = View.INVISIBLE
        binding.ivNoteFourSC.visibility = View.INVISIBLE
        binding.ivNoteFiveSC.visibility = View.INVISIBLE
        binding.btnC2C.isEnabled = false
        binding.btnC2C.setOnClickListener {
            binding.btnC2C.visibility = View.GONE
            binding.flSuitCaseContent.alpha = 0F
            binding.seekBaar.alpha = 0F
            binding.llValue.alpha = 0F

            startActivity(
                Intent(this, SelectAccountActivity::class.java).putExtra(
                    "AMOUNT", binding.etAmount.text.toString()
                ).putExtra("COIN_WORTH", coinWorth).putExtra("EARN_UP_TO", amount)

            )
            binding.ivCloseSuitCase.alpha = 1F
            overridePendingTransition(R.anim.fade_in_activity, R.anim.no_animation)
        }
        println("amount20 $amount20  amount40 $amount40  amount60 $amount60  amount80 $amount80")
        binding.tvCashCount.text = "₹$amount"
        binding.seekBaar.max = amount
        //  binding.seekBaar.progress = amount60
        //binding.btnC2C.isEnabled = amount60 > 0
        // animateSeekBar(amount60, binding.seekBaar)
        binding.seekBaar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.etAmount.setText("$progress")
                binding.tvCashCount.text = "₹" + (amount - progress).toString()
                binding.btnC2C.isEnabled = binding.seekBaar.progress > 0
                if (progress == 0) {
                    binding.ivNoteOne.visibility = View.VISIBLE
                    binding.ivNoteTwo.visibility = View.VISIBLE
                    binding.ivNoteThree.visibility = View.VISIBLE
                    binding.ivNoteFour.visibility = View.VISIBLE
                    binding.ivNoteFive.visibility = View.VISIBLE
                    binding.ivNoteOneSC.visibility = View.INVISIBLE
                    binding.ivNoteTwoSC.visibility = View.INVISIBLE
                    binding.ivNoteThreeSC.visibility = View.INVISIBLE
                    binding.ivNoteFourSC.visibility = View.INVISIBLE
                    binding.ivNoteFiveSC.visibility = View.INVISIBLE
                }
                if (progress > amount20) {
                    binding.ivNoteOne.visibility = View.INVISIBLE
                    binding.ivNoteTwo.visibility = View.VISIBLE
                    binding.ivNoteThree.visibility = View.VISIBLE
                    binding.ivNoteFour.visibility = View.VISIBLE
                    binding.ivNoteFive.visibility = View.VISIBLE
                    binding.ivNoteOneSC.visibility = View.VISIBLE
                    binding.ivNoteTwoSC.visibility = View.INVISIBLE
                    binding.ivNoteThreeSC.visibility = View.INVISIBLE
                    binding.ivNoteFourSC.visibility = View.INVISIBLE
                    binding.ivNoteFiveSC.visibility = View.INVISIBLE
                }
                if (progress > amount40) {
                    binding.ivNoteOne.visibility = View.INVISIBLE
                    binding.ivNoteTwo.visibility = View.INVISIBLE
                    binding.ivNoteThree.visibility = View.VISIBLE
                    binding.ivNoteFour.visibility = View.VISIBLE
                    binding.ivNoteFive.visibility = View.VISIBLE

                    binding.ivNoteOneSC.visibility = View.VISIBLE
                    binding.ivNoteTwoSC.visibility = View.VISIBLE
                    binding.ivNoteThreeSC.visibility = View.INVISIBLE
                    binding.ivNoteFourSC.visibility = View.INVISIBLE
                    binding.ivNoteFiveSC.visibility = View.INVISIBLE
                }
                if (progress > amount60) {
                    binding.ivNoteOne.visibility = View.INVISIBLE
                    binding.ivNoteTwo.visibility = View.INVISIBLE
                    binding.ivNoteThree.visibility = View.INVISIBLE
                    binding.ivNoteFour.visibility = View.VISIBLE
                    binding.ivNoteFive.visibility = View.VISIBLE
                    binding.ivNoteOneSC.visibility = View.VISIBLE
                    binding.ivNoteTwoSC.visibility = View.VISIBLE
                    binding.ivNoteThreeSC.visibility = View.VISIBLE
                    binding.ivNoteFourSC.visibility = View.INVISIBLE
                    binding.ivNoteFiveSC.visibility = View.INVISIBLE
                }
                if (progress > amount80) {
                    binding.ivNoteOne.visibility = View.INVISIBLE
                    binding.ivNoteTwo.visibility = View.INVISIBLE
                    binding.ivNoteThree.visibility = View.INVISIBLE
                    binding.ivNoteFour.visibility = View.INVISIBLE
                    binding.ivNoteFive.visibility = View.VISIBLE


                    binding.ivNoteOneSC.visibility = View.VISIBLE
                    binding.ivNoteTwoSC.visibility = View.VISIBLE
                    binding.ivNoteThreeSC.visibility = View.VISIBLE
                    binding.ivNoteFourSC.visibility = View.VISIBLE
                    binding.ivNoteFiveSC.visibility = View.INVISIBLE
                }
                if (progress >= amount100) {
                    binding.ivNoteOne.visibility = View.INVISIBLE
                    binding.ivNoteTwo.visibility = View.INVISIBLE
                    binding.ivNoteThree.visibility = View.INVISIBLE
                    binding.ivNoteFour.visibility = View.INVISIBLE
                    binding.ivNoteFive.visibility = View.INVISIBLE

                    binding.ivNoteOneSC.visibility = View.VISIBLE
                    binding.ivNoteTwoSC.visibility = View.VISIBLE
                    binding.ivNoteThreeSC.visibility = View.VISIBLE
                    binding.ivNoteFourSC.visibility = View.VISIBLE
                    binding.ivNoteFiveSC.visibility = View.VISIBLE
                }
                if (progress < amount20) {
                    binding.ivNoteOne.visibility = View.VISIBLE
                    binding.ivNoteTwo.visibility = View.VISIBLE
                    binding.ivNoteThree.visibility = View.VISIBLE
                    binding.ivNoteFour.visibility = View.VISIBLE
                    binding.ivNoteFive.visibility = View.VISIBLE


                    binding.ivNoteOneSC.visibility = View.INVISIBLE
                    binding.ivNoteTwoSC.visibility = View.INVISIBLE
                    binding.ivNoteThreeSC.visibility = View.INVISIBLE
                    binding.ivNoteFourSC.visibility = View.INVISIBLE
                    binding.ivNoteFiveSC.visibility = View.INVISIBLE
                }

            }

            override fun onStartTrackingTouch(seekbar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekbar: SeekBar?) {

            }


        })

        MparticleUtils.logCurrentScreen(
            "/rewards-tab/convert-to-cash/enter-amount",
            "The customer views the enter amount screen for converting to cash",
            "",
            "",
            "",
            "",
            SharePrefsLog.instance?.getLogBoolean(SharePrefsKeys.REWARD_TAB_CONVERT_TO_CASH),
            this
        )
    }


    //custom class for handling filters for amount input
    inner class MinMaxFilter() : InputFilter {
        private var intMin: Int = 0
        private var intMax: Int = 0

        constructor(minValue: Int, maxValue: Int) : this() {
            this.intMin = minValue
            this.intMax = maxValue
        }

        override fun filter(
            source: CharSequence, start: Int, end: Int, dest: Spanned, dStart: Int, dEnd: Int
        ): CharSequence? {
            try {
                val input = Integer.parseInt(dest.toString() + source.toString())
                if (isInRange(intMin, intMax, input)) {
                    return null
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            return ""
        }

        // Check if input c is in between min a and max b and
        // returns corresponding boolean
        private fun isInRange(a: Int, b: Int, c: Int): Boolean {
            return if (b > a) c in a..b else c in b..a
        }
    }

    private fun animateSeekBar(value: Int, seekBar: AppCompatSeekBar) {
        val anim = ValueAnimator.ofInt(0, value)
        anim.duration = 1000
        anim.addUpdateListener { animation ->
            val animProgress = animation.animatedValue as Int
            seekBar.progress = animProgress
        }
        anim.start()
    }

    override fun onResume() {
        super.onResume()
        binding.btnC2C.visibility = View.VISIBLE
        binding.flSuitCaseContent.alpha = 1F
        binding.seekBaar.alpha = 1F
        binding.llValue.alpha = 1F
        binding.ivCloseSuitCase.alpha = 0F
    }



    override fun onDestroy() {
        super.onDestroy()
         binding.seekBaar.progress=0
        _binding=null
    }
}