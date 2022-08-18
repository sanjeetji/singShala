package com.sensibol.lucidmusic.singstr.gui.app.profile.teacher.self

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.learn.academy.LessonGroupsAdapter
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentTeacherSelfLessonBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TeacherSelfLessonFragment : BaseFragment() {
    override val layoutResId: Int get() = R.layout.fragment_teacher_self_lesson
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding get() = FragmentTeacherSelfLessonBinding::inflate
    override val binding: FragmentTeacherSelfLessonBinding get() = super.binding as FragmentTeacherSelfLessonBinding

    @Inject
    lateinit var lessonGroupsAdapter: LessonGroupsAdapter

    override fun onInitView() {

        lessonGroupsAdapter = LessonGroupsAdapter()
        Timber.e("========= TeacherLessonFragment")

        binding.apply {

            rvLessonGroups.apply {
                adapter = lessonGroupsAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }
        }
    }

}