package com.example.cj_project_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin) // XML 레이아웃 파일

        auth = FirebaseAuth.getInstance()

        val userIDEditText = findViewById<EditText>(R.id.firstID)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val confirmPasswordEditText = findViewById<EditText>(R.id.confirm_password)
        val nameEditText = findViewById<EditText>(R.id.username)
        val phoneEditText = findViewById<EditText>(R.id.phone)
        val emailEditText = findViewById<EditText>(R.id.email)
        val genderGroup = findViewById<RadioGroup>(R.id.Gender)
        val signUpButton = findViewById<Button>(R.id.sign_up_button)
        val checkButton = findViewById<Button>(R.id.check_button) // 중복 검사 버튼

        signUpButton.setOnClickListener {
            val firstID = userIDEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()
            val name = nameEditText.text.toString()
            val phone = phoneEditText.text.toString()
            val selectedGenderId = genderGroup.checkedRadioButtonId
            val gender = findViewById<RadioButton>(selectedGenderId).text.toString()

            if (password == confirmPassword) {
                createAccount(firstID, email, password, name, phone, gender)
            } else {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        checkButton.setOnClickListener {
            val email = emailEditText.text.toString()
            checkEmailDuplicate(email)
        }
    }

    private fun createAccount(firstID: String, email: String, password: String, name: String, phone: String, gender: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 계정 생성 성공
                    val user = auth.currentUser
                    saveUserData(user?.uid, firstID, name, phone, gender)
                } else {
                    // 계정 생성 실패
                    Toast.makeText(this, "회원가입 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserData(userId: String?, firstID: String, name: String, phone: String, gender: String) {
        if (userId != null) {
            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("users").child(userId)

            val userMap = mapOf(
                "firstID" to firstID,
                "name" to name,
                "phone" to phone,
                "gender" to gender
            )

            userRef.setValue(userMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, search_Activity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "회원가입 데이터 저장 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun checkEmailDuplicate(email: String) {
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (signInMethods.isNullOrEmpty()) {
                        Toast.makeText(this, "사용 가능한 이메일입니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "이메일이 이미 사용 중입니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "중복 검사 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

