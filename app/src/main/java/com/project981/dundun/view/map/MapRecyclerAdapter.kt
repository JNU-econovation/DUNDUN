package com.project981.dundun.view.map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project981.dundun.R
import com.project981.dundun.databinding.BottomListItemBinding
import com.project981.dundun.databinding.BottomsheetListBinding
import com.project981.dundun.model.dto.MapDetailDTO
import java.text.SimpleDateFormat
import java.util.Date

class MapRecyclerAdapter(val itemList: ArrayList<MapDetailDTO>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class MapViewHolder(val binding: BottomListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MapDetailDTO){
            val dateFormat = "yyyy.MM.dd a HH:mm"
            val date = item.date
            val simpleDateFormat = SimpleDateFormat(dateFormat)
            val mapDate: String = simpleDateFormat.format(date)

            binding.txtMapitemName.text = item.noticeTitle
            binding.txtMapitemDate.text = mapDate
            binding.txtMapitemLocation.text = item.locationDescription
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MapViewHolder(BottomListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MapViewHolder).bind(itemList[position])
    }
}