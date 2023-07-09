package com.cheq.retail.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cheq.retail.R
import com.cheq.retail.ui.main.model.RewardsModel


class RewardsAdapter(

) :
    RecyclerView.Adapter<RewardsAdapter.Viewholder>() {

    fun setList(list: List<RewardsModel>) {

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.saleofferitem, parent, false)
        return Viewholder(itemView)
    }

    override fun onBindViewHolder(holder: Viewholder, @SuppressLint("RecyclerView") position: Int) {


//        holder.text1.setText(movie.getTitle())
        // holder.text1.setText(movie.getBody());
//        holder.mainCardView.setOnClickListener {
//            Toast.makeText(mContext, "" + position, Toast.LENGTH_SHORT).show()
//            cardClick.onItemclick(position)
//            //                v.getContext().startActivity(new Intent(mContext, MainActivity.class));
//        }
    }

//    interface OnCardClick {
//        fun onItemclick(position: Int)
//    }

    override fun getItemCount(): Int {
        return 0
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text1: TextView
         var img1: ImageView
         var img2: ImageView


//        var text2: TextView
//        var text3: TextView
//        var mainCardView: CardView

        init {
            text1 = itemView.findViewById(R.id.saleoffertext)
            img1 = itemView.findViewById(R.id.saleimg1)
            img2= itemView.findViewById(R.id.saleimg2)


//            text2 = itemView.findViewById(R.id.Title1)
//            text3 = itemView.findViewById(R.id.body)
//            mainCardView = itemView.findViewById(R.id.maincard)
        }
    }

    init {
        //movieslist = ModelrewardsList.toList()
//        this.cardClick = cardClick
    }
}

private fun ImageView.setImageResource(valueOf: kotlin.String) {
    TODO("Not yet implemented")
}
