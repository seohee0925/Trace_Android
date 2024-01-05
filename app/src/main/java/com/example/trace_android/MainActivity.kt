package com.example.trace_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private val fragmentManager: FragmentManager = supportFragmentManager
    private val feedFragment = FeedFragment()
    private val mypageFragment = MypageFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, feedFragment).commit()

        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            val fragmentTransaction = fragmentManager.beginTransaction()

            when (item.itemId) {
                R.id.navigation_feed -> fragmentTransaction.replace(R.id.fragment_container, feedFragment).commit()
                R.id.navigation_my_page -> fragmentTransaction.replace(R.id.fragment_container, mypageFragment).commit()
            }
            true
        }
    }
}