package com.sensibol.lucidmusic.singstr.gui.app.profile.other

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sensibol.lucidmusic.singstr.gui.app.profile.teacher.self.TeacherBioFragment
import com.sensibol.lucidmusic.singstr.gui.app.profile.teacher.self.TeacherLessonFragment
import timber.log.Timber

internal class OtherTeacherDetailsPagerAdapter(
    profileFragment: OtherUserProfileFragment,
    private val teacherId: String,
    private val userId: String
) : FragmentStateAdapter(profileFragment) {

    override fun getItemCount(): Int {
        return 3
    }

    //profile type defined to get move to different screen in TeacherLessonFragment and TeacherBioFragment
    override fun createFragment(position: Int): Fragment {
        Timber.d("createFragment position: $position")
        return when (position) {
            0 -> TeacherLessonFragment.newInstance(teacherId, "other")
            1 -> OtherTeacherCoverFragment.newInstance(userId)
            else -> TeacherBioFragment.newInstance(teacherId, "other")
        }
    }
}