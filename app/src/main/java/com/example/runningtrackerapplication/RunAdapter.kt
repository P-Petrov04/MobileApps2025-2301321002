package com.example.runningtrackerapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RunAdapter(
    private var runs: List<Run> = emptyList(),
    private val onItemClick: (Run) -> Unit
) : RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    class RunViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.runName)
        val timestamp: TextView = itemView.findViewById(R.id.runTimestamp)
        val distance: TextView = itemView.findViewById(R.id.runDistance)
        val time: TextView = itemView.findViewById(R.id.runTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_run, parent, false)
        return RunViewHolder(view)
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = runs[position]

        holder.name.text = run.name.ifEmpty { "Без име" }

        holder.distance.text = String.format(Locale.getDefault(), "%.2f км", run.distanceInKm)

        holder.time.text = formatTimeForDisplay(run.timeInMillis)

        val date = Date(run.timestamp)
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        holder.timestamp.text = formatter.format(date)

        holder.itemView.setOnClickListener {
            onItemClick(run)
        }
    }

    private fun formatTimeForDisplay(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d мин", minutes, seconds)
    }

    override fun getItemCount() = runs.size

    fun updateRuns(newRuns: List<Run>) {
        runs = newRuns
        notifyDataSetChanged()
    }

    private fun formatTime(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val hours = (millis / (1000 * 60 * 60))

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
}