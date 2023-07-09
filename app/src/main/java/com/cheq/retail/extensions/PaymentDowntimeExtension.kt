package com.cheq.retail.extensions

import android.util.Log
import android.view.View
import com.cheq.retail.constants.AFConstants
import com.cheq.retail.ui.billPayments.PaymentMethodsActivity
import com.cheq.retail.ui.billPayments.model.PaymentDowntime
import com.cheq.retail.utils.ImageUtils.loadSvg
import com.google.firebase.database.*


fun PaymentMethodsActivity.paymentDowntime() {
    val mFirebaseDatabase: DatabaseReference?
    mFirebaseDatabase =
        FirebaseDatabase.getInstance(com.cheq.retail.BuildConfig.firebaseDataBaseURL)
            .getReference(AFConstants.PAYMENT_DOWNTIME)
    mFirebaseDatabase.keepSynced(true)
    val downtimeListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val paymentDowntime = snapshot.getValue(PaymentDowntime::class.java)
            paymentDowntime?.let {
                binding.imgDowntime.apply {
                    this.visibility = if (it.flag == false)
                        View.GONE
                    else
                        View.VISIBLE
                    it.url?.let {
                        this.loadSvg(it)
                    }
                }

                binding.linearUpi.apply {
                    if (it.upiFlag == false) {
                        setViewEnable(false, 0.40F, this)

                    } else {
                        setViewEnable(true, 1.0F, this)
                    }
                }
                binding.linearNetBank.apply {
                    if (it.nbFlag == false) {
                        setViewEnable(false, 0.40F, this)
                        binding.overlayNb.visibility = View.VISIBLE
                    } else {
                        setViewEnable(true, 1.0F, this)
                        binding.overlayNb.visibility = View.GONE
                    }
                }
                binding.linearDebit.apply {
                    if (it.dcFlag == false) {
                        binding.overlayDb.visibility = View.VISIBLE
                        setViewEnable(false, 0.40F, this)

                    } else {
                        setViewEnable(true, 1.0F, this)
                        binding.overlayDb.visibility = View.GONE
                    }
                }

            }

        }

        override fun onCancelled(error: DatabaseError) {
            Log.e(TAG, error.message);
        }

    }

    mFirebaseDatabase.addValueEventListener(downtimeListener)


}

fun setViewEnable(isEnable: Boolean, alpha: Float, view: View) {
    view.alpha = alpha
    view.setAllEnabled(isEnable)
}