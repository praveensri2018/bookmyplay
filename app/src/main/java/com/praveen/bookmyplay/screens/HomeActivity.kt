package com.praveen.bookmyplay.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.praveen.bookmyplay.R
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.praveen.bookmyplay.LoginActivity
import com.praveen.bookmyplay.fragments.BookingFragment
import com.praveen.bookmyplay.fragments.ClubFragment
import com.praveen.bookmyplay.fragments.HomeFragment
import com.praveen.bookmyplay.fragments.GameFragment
import com.praveen.bookmyplay.fragments.ChatFragment
import androidx.fragment.app.Fragment


class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // Apply insets to keep UI under status/navigation bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Show default fragment on load
        loadFragment(BookingFragment())

        // Bottom navigation item selection
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_booking -> {
                    loadFragment(BookingFragment())
                    true
                }
                R.id.nav_club -> {
                    loadFragment(ClubFragment())
                    true
                }
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_game -> {
                    loadFragment(GameFragment())
                    true
                }
                R.id.nav_chat -> {
                    loadFragment(ChatFragment())
                    true
                }
                else -> false
            }
        }

        // Handle settings icon click
        val imgSettings = findViewById<ImageView>(R.id.imgSettings)
        imgSettings.setOnClickListener { view ->

            // Inflate popup layout
            val popupView = LayoutInflater.from(this).inflate(R.layout.popup_settings, null)

            // Create popup window
            val popupWindow = PopupWindow(
                popupView,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                true
            )

            // Set username (optional dynamic load)
            val tvUserName = popupView.findViewById<TextView>(R.id.tvUserName)
            tvUserName.text = "Hello, Praveen"

            // Handle logout button
            val btnLogout = popupView.findViewById<Button>(R.id.btnLogout)
            btnLogout.setOnClickListener {
                popupWindow.dismiss()

                // Clear preferences
                val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
                prefs.edit().clear().apply()

                // Navigate to LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // Prevent back press returning here
            }

            // Show the popup
            popupWindow.showAsDropDown(view, 0, 10)
        }
    }

    // Utility function to load fragments
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}