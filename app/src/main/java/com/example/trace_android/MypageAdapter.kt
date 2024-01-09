package com.example.trace_android

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MypageAdapter (fm: FragmentManager, private val numberOfFragment: Int) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> DateOrderFragment()
            1 -> CalendarFragment()
            else -> DateOrderFragment()
        }
    }
    override fun getCount(): Int {
        return numberOfFragment
    }
}