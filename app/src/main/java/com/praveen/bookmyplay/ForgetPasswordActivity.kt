package com.praveen.bookmyplay

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.concurrent.thread

class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var editTextEmailMobile: TextInputEditText
    private lateinit var buttonSendOtp: Button
    private lateinit var editTextOtp: TextInputEditText
    private lateinit var buttonSubmitOtp: Button
    private lateinit var editTextNewPassword: TextInputEditText
    private lateinit var editTextConfirmPassword: TextInputEditText
    private lateinit var buttonResetPassword: Button

    private var generatedOtp: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)
        supportActionBar?.hide()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editTextEmailMobile = findViewById(R.id.editTextEmailMobile)
        buttonSendOtp = findViewById(R.id.buttonSendOtp)
        editTextOtp = findViewById(R.id.editTextOtp)
        buttonSubmitOtp = findViewById(R.id.buttonSubmitOtp)
        editTextNewPassword = findViewById(R.id.editTextNewPassword)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        buttonResetPassword = findViewById(R.id.buttonResetPassword)

        buttonSendOtp.setOnClickListener {
            val email = editTextEmailMobile.text.toString().trim()

            if (email.isEmpty()) {
                editTextEmailMobile.error = "Please enter email"
                return@setOnClickListener
            }

            generatedOtp = (100000..999999).random().toString() // Generate 6-digit OTP

            sendOtpViaEmail(email, generatedOtp) // Send OTP via Email

            editTextEmailMobile.isEnabled = false
            buttonSendOtp.isEnabled = false

            findViewById<View>(R.id.inputLayoutOtp).visibility = View.VISIBLE

            editTextOtp.visibility = View.VISIBLE
            buttonSubmitOtp.visibility = View.VISIBLE
        }

        editTextOtp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buttonSubmitOtp.isEnabled = s?.length == 6
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        buttonSubmitOtp.setOnClickListener {
            val enteredOtp = editTextOtp.text.toString().trim()
            if (enteredOtp == generatedOtp) {
                Toast.makeText(this, "OTP Verified", Toast.LENGTH_SHORT).show()

                editTextOtp.visibility = View.GONE
                buttonSubmitOtp.visibility = View.GONE

                editTextNewPassword.visibility = View.VISIBLE
                editTextConfirmPassword.visibility = View.VISIBLE
                buttonResetPassword.visibility = View.VISIBLE
            } else {
                Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
            }
        }

        buttonResetPassword.setOnClickListener {
            val newPassword = editTextNewPassword.text.toString()
            val confirmPassword = editTextConfirmPassword.text.toString()

            if (newPassword.isEmpty()) {
                editTextNewPassword.error = "Enter new password"
                return@setOnClickListener
            }
            if (confirmPassword.isEmpty()) {
                editTextConfirmPassword.error = "Confirm your password"
                return@setOnClickListener
            }
            if (newPassword != confirmPassword) {
                editTextConfirmPassword.error = "Passwords do not match"
                return@setOnClickListener
            }

            Toast.makeText(this, "Password reset successful", Toast.LENGTH_SHORT).show()
            // TODO: Update password on server here
        }
    }

    // Email sending function
    private fun sendOtpViaEmail(email: String, otp: String) {
        thread {
            try {
                val props = Properties().apply {
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.host", "smtp.gmail.com")
                    put("mail.smtp.port", "587")
                }

                // Use your Gmail account and app password here
                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(
                            "praveensri2018@gmail.com",  // Replace with your email
                            "Iniyal@14"      // Replace with your Gmail app password
                        )
                    }
                })

                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress("praveensri2018@gmail.com"))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))
                    subject = "Your OTP Verification Code"
                    setText("Your OTP is $otp. Please do not share this code with anyone.")
                }

                Transport.send(message)

                runOnUiThread {
                    Toast.makeText(this, "OTP sent to your email", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Failed to send OTP email: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
