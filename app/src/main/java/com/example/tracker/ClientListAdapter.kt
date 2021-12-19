package com.example.tracker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.tracker.databinding.EmployeeClientsBinding

class ClientListAdapter(private val context: Context,private val dataList: ArrayList<ClientModel>)
    : RecyclerView.Adapter<ClientListAdapter.ItemViewHolder>() {

    private lateinit var binding: EmployeeClientsBinding

    inner class ItemViewHolder(binding: EmployeeClientsBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        binding = EmployeeClientsBinding.inflate(LayoutInflater.from(context),parent,false)
        return ItemViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = dataList[position]

        binding.clientName.text = current.client_name
        binding.purpose.text = "purpose: ${current.purpose}"
        binding.distanceTravelled.text = "Travelled distance : 4km"
        binding.destinationLocation.text = "destination: ${current.final_location}"
        Glide.with(context).asBitmap()
            .load(current.image)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
            .into(binding.clientImage)

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}