package com.a213310009ayubbudisantoso.transmart.api.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.a213310009ayubbudisantoso.transmart.R
import com.a213310009ayubbudisantoso.transmart.api.model.TarikBarangModel

class TarikBarangAdapter(private var itemList: List<TarikBarangModel>) :
    RecyclerView.Adapter<TarikBarangAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_barang, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun setData(newList: List<TarikBarangModel>) {
        itemList = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.name)
        private val tanggalTextView: TextView = itemView.findViewById(R.id.tanggal)

        fun bind(item: TarikBarangModel) {
            nameTextView.text = item.itemName
            tanggalTextView.text = item.expiredDate
        }
    }
}
