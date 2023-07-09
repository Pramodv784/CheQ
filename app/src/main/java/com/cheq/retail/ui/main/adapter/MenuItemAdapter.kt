package com.cheq.retail.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.R
import com.cheq.retail.databinding.AccountSettingSingleItemLayoutBinding
import com.cheq.retail.ui.main.model.SettingData


class MenuItemAdapter(
    var mContext: Context,
    var itemlist: List<SettingData>,
   var onclicked:(Int)->Unit,
    var emailLinked:Boolean

) :
    RecyclerView.Adapter<MenuItemAdapter.ViewHolder>() {
          private var emailCount:Int=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(mContext)
        val binding = AccountSettingSingleItemLayoutBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val data = itemlist[position]
        holder.binding.tvTitle.text = data.title
       // holder.binding.tvCaption.text = data.subtitle
        holder.binding.ivView.setImageResource(data.drawable)
        emailCount=data.emailCount
        viewState(data.id, holder.binding)
        setEmail(data.emailCount,holder.binding.emailLinked)
        holder.binding.llConst.setOnClickListener {
            onclicked(data.id)
        }
    }

    private fun setEmail(emailCount: Int,textview:AppCompatTextView) {
        when(emailCount){
            0 ->{
                textview.text="No Email Linked"
                textview.background =mContext.resources.getDrawable(R.drawable.bg_email__not_linked)
            }
            1 -> {
                textview.text="${emailCount} Email Linked"
                textview.background =mContext.resources.getDrawable(R.drawable.bg_email_linked)
            }
            in 2..20 ->{
                textview.text="${emailCount} Emails Linked"
                textview.background =mContext.resources.getDrawable(R.drawable.bg_email_linked)
            }
            else  ->{

            }


        }

    }

    private fun viewState(id: Int, binding: AccountSettingSingleItemLayoutBinding) {
        when (id) {
           7 -> {
               // binding.tvCaption.visibility = View.GONE
                binding.view.visibility = View.INVISIBLE
                binding.ivForward.visibility = View.INVISIBLE
            }
            6 -> {
                binding.view.visibility = View.INVISIBLE
               // binding.tvCaption.visibility = View.VISIBLE
            }


            3 ->{
                binding.emailLinked.visibility=View.VISIBLE
             /*   if(emailLinked && emailCount>0){
                    binding.emailLinked.visibility=View.VISIBLE
                }
                else
                {
                    binding.emailLinked.visibility=View.GONE
                }*/

            }
            else -> {
                binding.emailLinked.visibility=View.GONE
                binding.view.visibility = View.VISIBLE
                binding.ivForward.visibility = View.VISIBLE
               // binding.tvCaption.visibility = View.VISIBLE
            }
        }

    }


    override fun getItemCount(): Int {
        return itemlist.size
    }

    inner class ViewHolder(val binding: AccountSettingSingleItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {


    }


}