package com.cheq.retail.ui.billPayments.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.R
import com.cheq.retail.databinding.UpiItemLayoutBinding
import com.cheq.retail.ui.billPayments.model.UPIAppModelClass
import com.cheq.retail.utils.Base64
import com.cheq.retail.utils.MparticleUtils
import com.razorpay.ApplicationDetails


class UPIAppAdapter(private val upiAppModelClassList: MutableList<UPIAppModelClass>, val context: Context
, private val upiListener: onUPIClickListener,private val allUPI:Boolean) : RecyclerView.Adapter<UPIAppAdapter.UPIAppAdaterViewHolder>() {
    private var binding: UpiItemLayoutBinding? = null
    private var layoutInflater: LayoutInflater? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UPIAppAdapter.UPIAppAdaterViewHolder {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(context)
        }
        binding = DataBindingUtil.inflate(
            layoutInflater!!, R.layout.upi_item_layout, parent, false
        )
        return UPIAppAdaterViewHolder(binding!!)
    }

    override fun onBindViewHolder(holder: UPIAppAdapter.UPIAppAdaterViewHolder, position: Int) {
        // Glide.with(context).load(upiAppModelClassList[position].iconBase64).crossFade().fitCenter().into(holder.mBinding.ivAppLogo);

        println("App Icon ++++++ " + upiAppModelClassList[position].appIcon)

        val base64String = upiAppModelClassList[position].appIcon
        val base64Image = base64String!!.split(",").toTypedArray()[1]
        val decodedString: ByteArray = Base64.decode(base64Image, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        holder.mBinding.ivBankLogo.setImageBitmap(decodedByte)
        holder.mBinding.tvUPIName.text = upiAppModelClassList[position].appName

        holder.mBinding.llUpi.setOnClickListener {
            upiAppModelClassList[position].packageName?.let { it1 ->
                upiAppModelClassList[position].appName?.let { it2 ->
                    upiListener.onUPIClick(
                        it2,
                        it1,base64Image
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if(allUPI){
            upiAppModelClassList.size
        } else {
            if(upiAppModelClassList.size>=3) {
                3
            } else{
                upiAppModelClassList.size
            }

        }

    }

    inner class UPIAppAdaterViewHolder(val mBinding: UpiItemLayoutBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

    }

    interface onUPIClickListener {
        fun onUPIClick(appName: String, packageName: String,logo:String)
    }
}