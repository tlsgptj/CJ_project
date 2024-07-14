package com.example.cj_project_app

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.concurrent.Executor

class login : AppCompatActivity() {
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var prompt: BiometricPrompt.PromptInfo
    private lateinit var executor: Executor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }
            })
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("지문을 인증하세요")
            .setSubtitle("앱에 접근하려면 지문을 사용하세요")
            .setNegativeButtonText("취소")
            .build()
        val biometricButton = findViewById<Button>(R.id.finger_button)
        biometricButton.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
    }
}