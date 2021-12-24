package com.example.admin.client

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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

        val start = current.initial_location
        val sla = start.substring(0,start.indexOf(",")).toDouble()
        val slo = start.substring(start.indexOf(",").plus(1),start.length).toDouble()
        val end = current.final_location
        val ela = end.substring(0,end.indexOf(",")).toDouble()
        val elo = end.substring(end.indexOf(",").plus(1),end.length).toDouble()

        val dis = SphericalUtil.computeDistanceBetween(LatLng(sla,slo),LatLng(ela,elo))
        val d = String.format("%.0f",dis/1000).toFloat()

        binding.clientName.text = current.client_name
        binding.purpose.text = "purpose: ${current.purpose}"
        binding.destinationLocation.text = current.final_location
        binding.distanceTravelled.text = "Distance Travelled: ${d}km"
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
            intent.putExtra("initial",current.initial_location)
            intent.putExtra("final",current.final_location)
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

//    private fun calculateDistance(sla: Double,slo: Double,ela: Double,elo: Double): Double{
//        val loDiff = slo - elo
//        var distance = sin(degreeToRadian(sla)) * sin(degreeToRadian(ela)) + cos(degreeToRadian(sla)) * cos(degreeToRadian(ela)) * cos(degreeToRadian(loDiff))
//        distance = acos(distance)
//        distance = radianToDegree(distance)
//        distance *= 60 * 1.1515
//        distance *= 1.609344
//        return distance
//    }
//
//    private fun degreeToRadian(latitude: Double): Double{
//        return (latitude*Math.PI/180.0)
//    }
//
//    private fun radianToDegree(distance: Double): Double{
//        return (distance * 180/Math.PI)
//    }
}