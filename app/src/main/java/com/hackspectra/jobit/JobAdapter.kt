package com.hackspectra.jobit

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hackspectra.jobit.R

class JobAdapter(private val jobs: List<JobData>) :
    RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    class JobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvJobTitle)
        val employer: TextView = itemView.findViewById(R.id.employerName)
        val location: TextView = itemView.findViewById(R.id.jobCity)
        val applyButton: Button = itemView.findViewById(R.id.applyHere) // ⬅️ Apply button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_job, parent, false)
        return JobViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobs[position]

        Log.d("JobAdapter", "Title: ${job.job_title}, Publisher: ${job.employer_name}, Location: ${job.job_city}, Apply Link: ${job.job_apply_link}")

        holder.title.text = job.job_title ?: "No title"
        holder.employer.text = job.employer_name ?: "No employer name"
        holder.location.text = job.job_city ?: "No job location"

        holder.applyButton.setOnClickListener {
            val link = job.job_apply_link
            if (!link.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                holder.itemView.context.startActivity(intent)
            } else {
                Log.e("JobAdapter", "No job_apply_link found!")
            }
        }
    }

    override fun getItemCount(): Int = jobs.size
}