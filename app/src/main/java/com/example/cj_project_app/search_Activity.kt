
package com.example.cj_project_app
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.gms.fitness.FitnessOptions
import java.util.concurrent.TimeUnit

class search_Activity : AppCompatActivity() {

    private lateinit var chart: LineChart
    private val entries = mutableListOf<Entry>()
    private lateinit var dataSet: LineDataSet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 차트 초기화
        chart = findViewById(R.id.heart_chart)
        initChart()
        // 예시: 심박수 데이터 쓰기 및 차트 업데이트
        // 실제 데이터와 시간 범위를 적절히 설정하여 사용하세요
        writeHeartRateDataAndPlot(70f, System.currentTimeMillis(), System.currentTimeMillis())
    }

    // 차트 초기화 함수
    private fun initChart() {
        chart.setTouchEnabled(true)
        chart.setPinchZoom(true)

        // 차트 데이터 초기화
        dataSet = LineDataSet(entries, "Heart Rate (bpm)")
        dataSet.setDrawValues(false) // 값 표시 안 함
        val lineData = LineData(dataSet)
        chart.data = lineData
        chart.invalidate() // 차트 갱신
    }

    // 심박수 데이터를 기록하고 차트를 실시간으로 업데이트하는 함수
    private fun writeHeartRateDataAndPlot(heartRate: Float, startTimeMillis: Long, endTimeMillis: Long) {
        val fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_WRITE)
            .build()

        val account = GoogleSignIn.getAccountForExtension(this, fitnessOptions)

        val dataSet = Fitness.getRecordingClient(this, account)
            .addData(dataSet)
            .addOnSuccessListener {
                // 데이터 업데이트 성공 시 차트 업데이트
                updateChart(heartRate)
            }
            .addOnFailureListener { exception ->
                // 데이터 업데이트 실패 시 처리
            }
    }

    // 차트를 실시간으로 업데이트하는 함수
    private fun updateChart(heartRate: Float) {
        val currentTime = System.currentTimeMillis()
        entries.add(Entry(currentTime.toFloat(), heartRate))
        dataSet.notifyDataSetChanged() // 데이터셋 변경 알림
        chart.notifyDataSetChanged() // 차트 변경 알림
        chart.invalidate() // 차트 갱신
    }
}
