package com.project981.dundun.view.map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project981.dundun.databinding.BottomListItemBinding
import com.project981.dundun.model.dto.BottomDetailDTO
import java.text.SimpleDateFormat
import java.util.Locale

class BottomRecyclerAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemList = listOf<BottomDetailDTO>()

    inner class MapViewHolder(val binding: BottomListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BottomDetailDTO) {
            val dateFormat = "yyyy.MM.dd a HH:mm"
            val date = item.date
            val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
            val mapDate: String = simpleDateFormat.format(date)

            binding.txtMapitemName.text = item.artistName
            binding.txtMapitemDate.text = mapDate
            binding.txtMapitemLocation.text = item.locationDescription
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MapViewHolder(BottomListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MapViewHolder).bind(itemList[position])
    }

    fun setDate(list: List<BottomDetailDTO>) {
        itemList = list
        notifyDataSetChanged()
    }
}