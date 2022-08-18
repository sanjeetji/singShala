package com.sensibol.lucidmusic.singstr.gui.app.profile.teacher.self

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import timber.log.Timber

class TeacherSelfPagerAdapter(teacherDetailsFragment: TeacherDetailsFragment,
                              teacherId: String) : FragmentStateAdapter(teacherDetailsFragment) {


    private val mTeacherId = teacherId
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        Timber.d("createFragment position: $position")
        return if (position == 0)
            TeacherLessonFragment()
        else if(position == 1)
            SelfTeacherCoverFragment()
        else
            TeacherBioFragment.newInstance(mTeacherId, "")
    }

}