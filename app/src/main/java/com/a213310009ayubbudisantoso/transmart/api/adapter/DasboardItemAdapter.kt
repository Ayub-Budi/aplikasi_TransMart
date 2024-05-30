package com.a213310009ayubbudisantoso.transmart.api.adapter

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.a213310009ayubbudisantoso.transmart.R
import com.a213310009ayubbudisantoso.transmart.api.model.ClosestItem
import com.google.gson.Gson
import java.text.ParseException
import java.util.*

class DasboardItemAdapter(private var itemList: List<ClosestItem>) :
    RecyclerView.Adapter<DasboardItemAdapter.ViewHolder>() {

    // Interface untuk listener klik item
    interface OnItemClickListener {
        fun onItemClick(item: ClosestItem)
    }

    private var itemClickListener: OnItemClickListener? = null

    // Fungsi untuk mengatur listener klik item
    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dasboard_item_list, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun setData(newList: List<ClosestItem>) {
        itemList = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val hariTextView: TextView = itemView.findViewById(R.id.hari)
        private val nameTextView: TextView = itemView.findViewById(R.id.name)
        private val tanggalTextView: TextView = itemView.findViewById(R.id.tanggal)
        private val noGondalaTextView: TextView = itemView.findViewById(R.id.noGondola)
        private val statusTextView: TextView = itemView.findViewById(R.id.statusItem)

        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(item: ClosestItem) {
            hariTextView.text = item.remainingDays.toString()
            nameTextView.text = item.ieItemName
            noGondalaTextView.text = item.ieGondolaNo
            statusTextView.text = item.ieItemStatus

            // Format tanggal sesuai dengan "yyyy/MM/dd"
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            try {
                val date = inputFormat.parse(item.ieExpiredDate)
                val formattedDate = outputFormat.format(date)
                tanggalTextView.text = formattedDate
            } catch (e: ParseException) {
                e.printStackTrace()
                // Handle jika terjadi kesalahan parsing tanggal
            }

            // Menambahkan listener klik item
            itemView.setOnClickListener {
                itemClickListener?.onItemClick(item)

                val gson = Gson()
                val jsonString = gson.toJson(item)

                // Simpan item ke SharedPreferences dengan nama "item"
                val sharedPreferences = itemView.context.getSharedPreferences("my_shared_preferences", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("item", jsonString) // Ubah item.toString() sesuai dengan representasi data item Anda
                editor.apply()
            }
        }
    }
}

