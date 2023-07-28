package com.project981.dundun.view.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project981.dundun.databinding.BottomListItemBinding
import com.project981.dundun.model.dto.BottomDetailDTO
import java.text.SimpleDateFormat
import java.util.Locale

class BottomRecyclerAdapter(val clickListener : (String, String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemList = listOf<BottomDetailDTO>()

    inner class MapViewHolder(val binding: BottomListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BottomDetailDTO) {
            val dateFormat = "yyyy.MM.dd a h:mm"
            val date = item.date
            val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
            val mapDate: String = simpleDateFormat.format(date)

            binding.button2.setOnClickListener {
                clickListener(item.articleID,item.artistID)
            }
            binding.txtMapitemName.text = item.artistName
            binding.txtMapitemDate.text = mapDate
            if (item.locationDescription != null) {
                binding.txtMapitemLocation.text = item.locationDescription
                binding.txtMapitemLocation.visibility = View.VISIBLE
                binding.imageView.visibility = View.VISIBLE

            }else{
                binding.txtMapitemLocation.visibility = View.GONE
                binding.imageView.visibility = View.GONE
            }
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