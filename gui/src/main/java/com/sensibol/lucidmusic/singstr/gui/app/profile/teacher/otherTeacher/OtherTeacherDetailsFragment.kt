package com.sensibol.lucidmusic.singstr.gui.app.profile.teacher.self

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.viewbinding.ViewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.profile.teacher.TeacherProfessionalDetailsAdapter
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentOtherTeacherDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OtherTeacherDetailsFragment : BaseFragment() {
    override val layoutResId: Int get() = R.layout.fragment_other_teacher_details
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding get() = FragmentOtherTeacherDetailsBinding::inflate
    override val binding: FragmentOtherTeacherDetailsBinding get() = super.binding as FragmentOtherTeacherDetailsBinding


    @Inject
    lateinit var teacherProfessionalDetailsAdapter: TeacherProfessionalDetailsAdapter


    override fun onInitView() {
        teacherProfessionalDetailsAdapter = TeacherProfessionalDetailsAdapter();
        binding.apply {

            pbLevel.isIndeterminate = false
            vpTeacherOther.isUserInputEnabled = false
            vpTeacherOther.adapter = TeacherOtherPagerAdapter(this@OtherTeacherDetailsFragment)

            for (i in 0 until tabLayout.tabCount) {
                val tabView: View = (tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
                tabView.requestLayout()
                ViewCompat.setPaddingRelative(tabView, 50, tabView.paddingTop, 50, tabView.paddingBottom)
            }
            TabLayoutMediator(tabLayout, vpTeacherOther) { tab, position ->
                if (position == 0) {
                    tabLayout.setTabTextColors(Color.parseColor("#727272"), Color.parseColor("#ffffff"))
                    tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#ffffff"));
                    tab.text = "Lessons"
                    tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.selector_school_icon);
                } else if (position == 1) {
                    tab.text = "Covers"
                    tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_music_node);
                }else{
                    tab.text = "Bio"
                    tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_profile);
                }
                tab.select()
            }.attach()

        }

    }

}