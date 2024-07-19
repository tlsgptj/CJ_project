package com.example.cj_project_app

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.Executor

class fingerprint_signin : AppCompatActivity() {

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var executor: Executor
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fingerprint_signin)

        executor = ContextCompat.getMainExecutor(this)
        auth = FirebaseAuth.getInstance()

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(this@fingerprint_signin, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val user = auth.currentUser
                    if (user != null) {
                        fetchFingerprintDataFromFirebase(user)
                    } else {
                        Toast.makeText(this@fingerprint_signin, "No authenticated user found.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(this@fingerprint_signin, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("지문을 인증하세요")
            .setSubtitle("앱에 접근하려면 지문을 사용하세요")
            .setNegativeButtonText("취소")
            .build()

        val biometricButton = findViewById<Button>(R.id.finger_bt)
        biometricButton.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    private fun fetchFingerprintDataFromFirebase(user: FirebaseUser) {
        val database = FirebaseDatabase.getInstance()
        val fingerprintRef = database.getReference("fingerprints/${user.uid}")

        fingerprintRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val storedFingerprint = snapshot.getValue(String::class.java)
                if (storedFingerprint != null) {
                    compareFingerprints(storedFingerprint)
                } else {
                    Toast.makeText(this@fingerprint_signin, "No fingerprint data found.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@fingerprint_signin, "Failed to read fingerprint data.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun compareFingerprints(storedFingerprint: String) {
        Toast.makeText(this, "Fingerprints matched: $storedFingerprint", Toast.LENGTH_SHORT).show()
    }
}
