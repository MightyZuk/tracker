package com.example.tracker

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tracker.databinding.EmployeeClientsBinding

class ClientListAdapter(private val context: Context,private val dataList: ArrayList<EmployeeData>)
    : RecyclerView.Adapter<ClientListAdapter.ItemViewHolder>() {

    private lateinit var binding: EmployeeClientsBinding

    inner class ItemViewHolder(binding: EmployeeClientsBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        binding = EmployeeClientsBinding.inflate(LayoutInflater.from(context),parent,false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = dataList[position]

        binding.clientName.text = current.name
        binding.purpose.text = current.id.toString()
        binding.distanceTravelled.text = current.password.toString()
        binding.destinationLocation.text = current.number.toString()

    }

    override fun getItemCount(): Int {
        return dataList.size
    }


}