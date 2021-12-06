package com.example.admin

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.databinding.EmployeeDetailsListLayoutBinding

class EmployeeListAdapter(
    private val context: Context): RecyclerView.Adapter<EmployeeListAdapter.ItemViewHolder>(){

    private lateinit var binding: EmployeeDetailsListLayoutBinding

    inner class ItemViewHolder(binding: EmployeeDetailsListLayoutBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        binding = EmployeeDetailsListLayoutBinding.inflate(LayoutInflater.from(context),parent,false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        binding.employeeCard.setOnClickListener {
            it.context.startActivity(Intent(context,EmployeeInfo::class.java))
        }

    }

    override fun getItemCount(): Int {
        return 10
    }
}