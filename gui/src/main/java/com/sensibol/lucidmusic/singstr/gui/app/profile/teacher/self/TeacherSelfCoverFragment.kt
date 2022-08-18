package com.sensibol.lucidmusic.singstr.gui.app.profile.teacher.self

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.profile.self.CoverView
import com.sensibol.lucidmusic.singstr.gui.app.profile.teacher.TeacherDetailsViewModel
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentTeacherSelfCoversBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TeacherSelfCoverFragment : BaseFragment() {
    override val layoutResId: Int get() = R.layout.fragment_teacher_self_covers
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding get() = FragmentTeacherSelfCoversBinding::inflate
    override val binding: FragmentTeacherSelfCoversBinding get() = super.binding as FragmentTeacherSelfCoversBinding


    private val teacherDetailsViewModel: TeacherDetailsViewModel by viewModels()

    @Inject
    lateinit var teacherSelfCoversAdapter: TeacherSelfCoversAdapter

    override fun onInitView() {

        teacherDetailsViewModel.apply {

            observe(coverViews, ::showCoverViews)
            getCovers()
        }

        teacherSelfCoversAdapter = TeacherSelfCoversAdapter()
        Timber.e("========= TeacherSelfCoverFragment")

        binding.apply {

            rvTeacherCovers.apply {
                layoutManager = GridLayoutManager(context, 2)
                adapter = teacherSelfCoversAdapter
            }
        }
    }

    private fun showCoverViews(coverViews: List<CoverView>) {
        teacherSelfCoversAdapter.coverViews = coverViews
    }

}