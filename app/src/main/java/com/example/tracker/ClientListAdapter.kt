package com.example.tracker

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tracker.databinding.EmployeeClientsBinding

class ClientListAdapter(private val context: Context): RecyclerView.Adapter<ClientListAdapter.ItemViewHolder>() {

    private lateinit var binding: EmployeeClientsBinding

    inner class ItemViewHolder(binding: EmployeeClientsBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        binding = EmployeeClientsBinding.inflate(LayoutInflater.from(context),parent,false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 10
    }


}