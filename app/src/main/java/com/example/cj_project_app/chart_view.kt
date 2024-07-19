package com.example.cj_project_app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var dailyChart: CombinedChart
    private lateinit var weeklyChart: CombinedChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart_view)

        dailyChart = findViewById<CombinedChart>(R.id.today)
        weeklyChart = findViewById<CombinedChart>(R.id.weekly)

        fetchHeartRateData()
    }

    private fun fetchHeartRateData() {
        val database = FirebaseDatabase.getInstance()
        val heartRateRef = database.getReference("heartRates")

        heartRateRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dailyEntries = mutableListOf<Entry>()
                val weeklyEntries = mutableListOf<Entry>()

                snapshot.children.forEach { child ->
                    val timestamp = child.child("timestamp").getValue(Long::class.java) ?: 0L
                    val heartRate = child.child("rate").getValue(Float::class.java) ?: 0f

                    dailyEntries.add(Entry(timestamp.toFloat(), heartRate))
                    weeklyEntries.add(Entry(timestamp.toFloat(), heartRate))
                }

                updateChart(dailyChart, dailyEntries, "Daily Heart Rate")
                updateChart(weeklyChart, weeklyEntries, "Weekly Heart Rate")
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to load heart rate data.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateChart(chart: CombinedChart, entries: List<Entry>, label: String) {
        val lineDataSet = LineDataSet(entries, label).apply {
            color = resources.getColor(R.color.teal_700, null)
            setDrawValues(false)
            setDrawCircles(false)
            lineWidth = 2f
        }
        val lineData = LineData(lineDataSet)

        val combinedData = CombinedData().apply {
            setData(lineData)
        }

        chart.data = combinedData
        chart.invalidate()
    }
}
