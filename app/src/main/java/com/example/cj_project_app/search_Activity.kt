package com.example.cj_project_app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchActivity : AppCompatActivity() {

    private val CALL_PHONE_PERMISSION_REQUEST_CODE = 1
    private lateinit var adminPhoneNumber: String
    private lateinit var adminEmailAddress: String

    private lateinit var call119Button: Button
    private lateinit var helpButton: Button
    private lateinit var homeButton: Button
    private lateinit var chartPicButton: Button
    private lateinit var personButton: Button
    private lateinit var workHoursTextView: TextView
    private lateinit var minuteTextView: TextView
    private lateinit var heartChart: LineChart
    private lateinit var progressBar: ProgressBar
    private lateinit var stressBar: ProgressBar
    private val entries = mutableListOf<Entry>()
    private lateinit var dataSet: LineDataSet
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        call119Button = findViewById(R.id.call_119)
        helpButton = findViewById(R.id.help)
        homeButton = findViewById(R.id.home)
        chartPicButton = findViewById(R.id.chartPic)
        personButton = findViewById(R.id.person)
        workHoursTextView = findViewById(R.id.work_hours)
        minuteTextView = findViewById(R.id.minute)
        heartChart = findViewById(R.id.heart_chart)
        progressBar = findViewById(R.id.progressBar)
        stressBar = findViewById(R.id.stress_bar)

        dataSet = LineDataSet(entries, "Heart Rate")
        heartChart.data = LineData(dataSet)

        database = FirebaseDatabase.getInstance().getReference("heartRate")

        call119Button.setOnClickListener {
            makePhoneCall("119")
        }

        helpButton.setOnClickListener {
            if (::adminPhoneNumber.isInitialized) {
                makePhoneCall(adminPhoneNumber)
            } else {
                Toast.makeText(this, "관리자 정보를 불러오는 중입니다. 잠시 후 다시 시도하세요.", Toast.LENGTH_SHORT).show()
                loadAdminInfoFromFirebase()
            }
        }

        initChart()
        writeHeartRateDataAndPlot(70f, System.currentTimeMillis(), System.currentTimeMillis())
        updateProgressBar(progressBar)
        updateProgressBar(stressBar)
    }

    private fun loadAdminInfoFromFirebase() {
        val adminRef = FirebaseDatabase.getInstance().getReference("admin")

        adminRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                adminPhoneNumber = snapshot.child("phone").getValue(String::class.java) ?: ""
                adminEmailAddress = snapshot.child("email").getValue(String::class.java) ?: ""
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SearchActivity, "관리자 정보를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun makePhoneCall(phoneNumber: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), CALL_PHONE_PERMISSION_REQUEST_CODE)
        } else {
            startPhoneCall(phoneNumber)
        }
    }

    private fun startPhoneCall(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$phoneNumber")
        try {
            startActivity(callIntent)
        } catch (e: SecurityException) {
            e.printStackTrace()
            Toast.makeText(this, "전화 걸기 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CALL_PHONE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall("119")
            } else {
                Toast.makeText(this, "전화 걸기 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initChart() {
        heartChart.setTouchEnabled(true)
        heartChart.setPinchZoom(true)

        dataSet = LineDataSet(entries, "Heart Rate (bpm)")
        dataSet.setDrawValues(false)
        val lineData = LineData(dataSet)
        heartChart.data = lineData
        heartChart.invalidate()
    }

    private fun writeHeartRateDataAndPlot(heartRate: Float, startTimeMillis: Long, endTimeMillis: Long) {
        val fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_WRITE)
            .build()

        val account = GoogleSignIn.getAccountForExtension(this, fitnessOptions)

        val task = Fitness.getRecordingClient(this, account)
            .subscribe(DataType.TYPE_HEART_RATE_BPM)

        task.addOnSuccessListener {
            updateChart(heartRate)
        }.addOnFailureListener { exception ->
            exception.printStackTrace()
        }
    }

    private fun updateChart(heartRate: Float) {
        val currentTime = System.currentTimeMillis()
        entries.add(Entry(currentTime.toFloat(), heartRate))
        dataSet.notifyDataSetChanged()
        heartChart.notifyDataSetChanged()
        heartChart.invalidate()
    }

    private fun updateProgressBar(progressBar: ProgressBar) {
        progressBar.progress = 0

        Thread {
            var progress = 0
            while (progress <= 100) {
                val currentProgress = progress
                runOnUiThread { progressBar.progress = currentProgress }
                try {
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                progress += 10
            }
        }.start()
    }
}

