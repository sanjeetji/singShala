package com.sensibol.lucidmusic.singstr.gui.app.profile.teacher.self

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentSelfTeacherCoversBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TeacherOtherCoverFragment : BaseFragment() {
    override val layoutResId: Int get() = R.layout.fragment_self_teacher_covers
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding get() = FragmentSelfTeacherCoversBinding::inflate
    override val binding: FragmentSelfTeacherCoversBinding get() = super.binding as FragmentSelfTeacherCoversBinding


    lateinit var teacherOtherCoversAdapter: TeacherOtherCoversAdapter


    override fun onInitView() {

        teacherOtherCoversAdapter = TeacherOtherCoversAdapter()
        Timber.e("========= TeacherSelfCoverFragment")

        binding.apply {


            /*rvTeacherCovers.apply {
                layoutManager = GridLayoutManager(context, 2)
                adapter = teacherSelfCoversAdapter
            }*/
        }
    }

}