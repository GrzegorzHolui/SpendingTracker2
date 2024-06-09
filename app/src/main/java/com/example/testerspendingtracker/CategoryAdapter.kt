package com.example.testerspendingtracker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myspendingtracker.R

class CategoryAdapter(private var category: List<Category>): RecyclerView.Adapter<CategoryAdapter.CategoryHolder>(){

    class CategoryHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(category: List<Category>) {
        this.category = category
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_layout, parent, false)
        return CategoryHolder(view)    }

    override fun getItemCount(): Int {
        return category.size
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        val current_category = category[position]
        holder.name.text = current_category.name
    }
}