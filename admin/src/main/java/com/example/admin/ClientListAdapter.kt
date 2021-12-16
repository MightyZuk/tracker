package com.example.admin

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.databinding.EmployeeClientsBinding

class ClientListAdapter(private val context: Context,private val name: String):
    RecyclerView.Adapter<ClientListAdapter.ItemViewHolder>() {

    private lateinit var binding: EmployeeClientsBinding

    inner class ItemViewHolder(binding: EmployeeClientsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        binding = EmployeeClientsBinding.inflate(LayoutInflater.from(context),parent,false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        holder.itemView.setOnClickListener{
            val intent = Intent(context,TravelDetails::class.java)
            intent.putExtra("emp_name",name)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return 10
    }


}