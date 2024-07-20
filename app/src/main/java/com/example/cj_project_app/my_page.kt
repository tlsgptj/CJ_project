package com.example.cj_project_app


import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class my_page : AppCompatActivity() {

    private lateinit var nameTextView: TextView
    private lateinit var editProfileButton: Button
    private lateinit var changePasswordButton: Button
    private lateinit var pushNotificationsButton: Button
    private lateinit var viewReportsButton: Button
    private lateinit var noticeButton: Button
    private lateinit var inquiryButton: Button
    private lateinit var customerServiceButton: Button
    private lateinit var settingsButton: Button
    private lateinit var homeButton: Button
    private lateinit var chartPicButton: Button
    private lateinit var myPageGoButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        // 뷰 초기화
        nameTextView = findViewById(R.id.name)
        editProfileButton = findViewById(R.id.editProfile)
        changePasswordButton = findViewById(R.id.change_password)
        pushNotificationsButton = findViewById(R.id.push_notifications)
        viewReportsButton = findViewById(R.id.view_reports)
        noticeButton = findViewById(R.id.notice)
        inquiryButton = findViewById(R.id.inquiry)
        customerServiceButton = findViewById(R.id.customer_service)
        settingsButton = findViewById(R.id.settings)
        homeButton = findViewById(R.id.home)
        chartPicButton = findViewById(R.id.chartPic)
        myPageGoButton = findViewById(R.id.my_page_go)

        // 파이어베이스에서 사용자 정보 가져오기
        fetchUserInfo()

        // 버튼 클릭 리스너 설정
        editProfileButton.setOnClickListener {
            val intent = Intent(this, myPageProfile::class.java)
            startActivity(intent)
        }
        changePasswordButton.setOnClickListener {
            Toast.makeText(this, "비밀번호 변경 클릭됨", Toast.LENGTH_SHORT).show()
        }
        pushNotificationsButton.setOnClickListener {
            Toast.makeText(this, "푸시 알람 설정 클릭됨", Toast.LENGTH_SHORT).show()
        }
        viewReportsButton.setOnClickListener {
            val intent = Intent(this, call_119::class.java)
            startActivity(intent)
        }
        noticeButton.setOnClickListener {
            Toast.makeText(this, "공지 사항 클릭됨", Toast.LENGTH_SHORT).show()
        }
        inquiryButton.setOnClickListener {
            Toast.makeText(this, "1 : 1 문의 클릭됨", Toast.LENGTH_SHORT).show()
        }
        customerServiceButton.setOnClickListener {
            Toast.makeText(this, "고객 센터 클릭됨", Toast.LENGTH_SHORT).show()
        }
        settingsButton.setOnClickListener {
            Toast.makeText(this, "환경설정 클릭됨", Toast.LENGTH_SHORT).show()
        }
        homeButton.setOnClickListener {
            Toast.makeText(this, "홈 클릭됨", Toast.LENGTH_SHORT).show()
        }
        chartPicButton.setOnClickListener {
            Toast.makeText(this, "차트 클릭됨", Toast.LENGTH_SHORT).show()
        }
        myPageGoButton.setOnClickListener {
            Toast.makeText(this, "내 페이지 클릭됨", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchUserInfo() {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("users/user1")

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java)
                name?.let {
                    nameTextView.text = it
                } ?: run {
                    Toast.makeText(this@my_page, "사용자 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@my_page, "데이터베이스 오류: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
