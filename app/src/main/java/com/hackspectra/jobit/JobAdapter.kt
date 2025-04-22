package com.hackspectra.jobit

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hackspectra.jobit.R

// JobAdapter.kt
class JobAdapter(private val jobs: List<JobData>) :
    RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    class JobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvJobTitle)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_job, parent, false)
        return JobViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobs[position]
        Log.d("JobAdapter", "Title: ${job.job_title}, Publisher: ${job.publisher_name}, Location: ${job.location}, Salary: ${job.salary_period}")
        holder.title.text = job.job_title ?: "No title"


    }

    override fun getItemCount(): Int = jobs.size
}