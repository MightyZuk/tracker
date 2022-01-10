package com.example.admin.client

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.admin.employee.TravelDetails
import com.example.admin.databinding.EmployeeClientsBinding
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class ClientListAdapter(private val context: Context
    ,private val list: ArrayList<ClientModel>
    ,private val name: String):
    RecyclerView.Adapter<ClientListAdapter.ItemViewHolder>() {

    private lateinit var binding: EmployeeClientsBinding

    inner class ItemViewHolder(binding: EmployeeClientsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        binding = EmployeeClientsBinding.inflate(LayoutInflater.from(context),parent,false)
        return ItemViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = list[position]

        binding.clientName.text = current.client_name
        binding.purpose.text = "purpose: ${current.purpose}"
        binding.destinationLocation.text = current.end
        binding.distanceTravelled.text = "Distance Travelled: ${current.distance}km"
        binding.datePickerActions.text = current.dateTime
        Glide.with(context).asBitmap()
            .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
            .load(current.image).into(binding.clientImage)

        holder.itemView.setOnClickListener{
            val intent = Intent(context, TravelDetails::class.java)
            intent.putExtra("emp_name",name)
            intent.putExtra("client_name",current.client_name)
            intent.putExtra("image",current.image)
            intent.putExtra("purpose",current.purpose)
            intent.putExtra("initial",current.start)
            intent.putExtra("final",current.end)
            intent.putExtra("distance",current.distance)
            intent.putExtra("locations",current.locations)
            Log.d("locations Points",current.locations)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}