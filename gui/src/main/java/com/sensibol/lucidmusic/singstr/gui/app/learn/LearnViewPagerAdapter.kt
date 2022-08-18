package com.sensibol.lucidmusic.singstr.gui.app.learn

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sensibol.lucidmusic.singstr.gui.app.learn.academy.AcademyFragment
import com.sensibol.lucidmusic.singstr.gui.app.learn.academy.TuneTimingFragment

class LearnViewPagerAdapter(
    fragment: Fragment,
    private var tabTotal: Int
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return tabTotal
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0)
            AcademyFragment()
        else
            TuneTimingFragment()
    }

}