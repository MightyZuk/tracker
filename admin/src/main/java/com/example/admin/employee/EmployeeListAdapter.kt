package com.example.admin.employee

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.admin.databinding.EmployeeDetailsListLayoutBinding

class EmployeeListAdapter(private val context: Context,private val oldList: ArrayList<EmployeeModel>):
    RecyclerView.Adapter<EmployeeListAdapter.ItemViewHolder>(){

    private lateinit var binding: EmployeeDetailsListLayoutBinding

    inner class ItemViewHolder(binding: EmployeeDetailsListLayoutBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        binding = EmployeeDetailsListLayoutBinding.inflate(LayoutInflater.from(context),parent,false)
        return ItemViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = oldList[position]
        binding.employeeName.text = current.name
        binding.employeeId.text = "id: ${current.id}"
        Glide.with(context).asBitmap().load(current.image)
            .fitCenter()
            .centerInside()
            .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
            .into(binding.employeeImage)

        binding.employeeCard.setOnClickListener {
            val intent = Intent(context, EmployeeInfo::class.java)
            intent.putExtra("name",current.name)
            intent.putExtra("id",current.id)
            intent.putExtra("image",current.image)
            it.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return oldList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}