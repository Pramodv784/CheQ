package com.cheq.retail.ui.downTime

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.cheq.retail.R
import com.cheq.retail.base.BaseActivity
import com.cheq.retail.constants.Constant.Companion.FIREBASE_DATA
import com.cheq.retail.constants.Constant.Companion.FIREBASE_DOWN_TIME_REF
import com.cheq.retail.databinding.ActivityDownTimeActvityBinding
import com.cheq.retail.ui.landing.view.LandingActivity
import com.cheq.retail.utils.ConfigDataResponse
import com.cheq.retail.utils.ImageUtils.loadSvg
import com.cheq.retail.utils.Utils
import com.google.firebase.database.*

class DownTimeActvity : AppCompatActivity() {
    var _data: ConfigDataResponse? = null
    var _binding: ActivityDownTimeActvityBinding? = null
    val TAG = DownTimeActvity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_down_time_actvity)
        initViews()
    }

    private fun initViews() {
        _binding?.btClose?.setOnClickListener { finishAffinity() }
        if (intent.getSerializableExtra(FIREBASE_DATA) != null) {
            _data = intent.getSerializableExtra(FIREBASE_DATA) as ConfigDataResponse?

            _binding?.apply {
                tvTitle.text = "${_data?.title}"
                tvSubtitle.text = "${_data?.description}"
                ivImage.loadSvg(_data?.illustration?.android.toString())
            }
        }
        val mFirebaseDatabase: DatabaseReference?
        val mFirebaseInstance: FirebaseDatabase?
        mFirebaseInstance = FirebaseDatabase.getInstance(com.cheq.retail.BuildConfig.firebaseDataBaseURL)
        mFirebaseDatabase = mFirebaseInstance.getReference(FIREBASE_DOWN_TIME_REF)
        mFirebaseDatabase.keepSynced(true)
        val rateListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _data = snapshot.getValue(ConfigDataResponse::class.java)
                if (_data?.isenabled == false) {
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to read id", error.toException())
            }


        }
        mFirebaseDatabase.addValueEventListener(rateListener)
    }

    override fun onBackPressed() {

    }
}