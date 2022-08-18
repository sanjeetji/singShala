package com.sensibol.lucidmusic.singstr.gui.app.profile.teacher.self

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.AppToast
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.AcademyContent
import com.sensibol.lucidmusic.singstr.domain.model.LessonMini
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.learn.LearnFragmentDirections
import com.sensibol.lucidmusic.singstr.gui.app.learn.academy.LessonGroupsAdapter
import com.sensibol.lucidmusic.singstr.gui.app.profile.other.OtherUserProfileFragment
import com.sensibol.lucidmusic.singstr.gui.app.profile.self.ProfileFragment
import com.sensibol.lucidmusic.singstr.gui.app.profile.teacher.TeacherLessonViewModel
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentTeacherLessonBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TeacherLessonFragment : BaseFragment() {
    override val layoutResId: Int get() = R.layout.fragment_teacher_lesson
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding get() = FragmentTeacherLessonBinding::inflate
    override val binding: FragmentTeacherLessonBinding get() = super.binding as FragmentTeacherLessonBinding

    private lateinit var mTeacherId: String
    private lateinit var mProfileType: String

    private val viewModel: TeacherLessonViewModel by viewModels()

    @Inject
    lateinit var lessonGroupsAdapter: LessonGroupsAdapter

    companion object {
        private const val TEACHER_ID = "teacher_id"
        private const val TEACHER_PROFILE_TYPE = "teacher_profile_type"

        fun newInstance(teacherID: String, profileType: String) = TeacherLessonFragment().apply {
            arguments = Bundle(2).apply {
                putString(TEACHER_ID, teacherID)
                putString(TEACHER_PROFILE_TYPE, profileType)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let {
            mTeacherId = it.getString(TEACHER_ID).toString()
            mProfileType = it.getString(TEACHER_PROFILE_TYPE).toString()
            onInitView()
        }
    }

    override fun onInitView() {

        Timber.d("from nikhil onInitView lessonGroup")
        lessonGroupsAdapter = LessonGroupsAdapter()

        viewModel.apply {

            loadLessonGroup()
            observe(lessonGroup, :: showLessons)

        }

        binding.apply {

            rvLessonGroups.apply {
                adapter = lessonGroupsAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }

            lessonGroupsAdapter.onAllViewLessonClickListener = { conceptId ->
                if (mProfileType == "own")
                    (requireParentFragment() as ProfileFragment).moveToLessonListScreen(conceptId)
                else if (mProfileType == "other")
                    (requireParentFragment() as OtherUserProfileFragment).moveToLessonListScreen(conceptId)
            }

            lessonGroupsAdapter.onLessonClickListener = {
                if (mProfileType == "own")
                    (requireParentFragment() as ProfileFragment).moveToLessonScreen(it.id)
                else if (mProfileType == "other")
                    (requireParentFragment() as OtherUserProfileFragment).moveToLessonScreen(it.id)

            }
        }
    }

    private fun showLessons(lessonGroup: List<AcademyContent.LessonGroup>){

        Timber.d("lesson list nikhil--> ${viewModel.listSize}")
        Timber.d("onInitView lessonGroup size ${lessonGroup.size}")
        val teacherLessonGroup = mutableListOf<AcademyContent.LessonGroup>()
        lessonGroup.forEach { lessonGroup ->
            val teacherLessonMini = mutableListOf<LessonMini>()
            val id = lessonGroup.id
            val title = lessonGroup.title
            val displayOrder = lessonGroup.displayOrder
            lessonGroup.lessons.forEach { lessonMini ->
                if (lessonMini.teacherId.isNotBlank() && lessonMini.teacherId == mTeacherId)
                    teacherLessonMini.add(lessonMini)
            }

            if (teacherLessonMini.isNotEmpty()) {
                val lessonGroup = AcademyContent.LessonGroup(
                    id, title, displayOrder, teacherLessonMini
                )
                teacherLessonGroup.add(lessonGroup)
            }
        }

        binding.progressBar.visibility = GONE
        if (teacherLessonGroup.isEmpty()) {
//                    AppToast.show(requireContext(), "No lesson found")
//                    lessonGroupsAdapter.lessonGroups = it
            binding.apply {
                tvEmptyList.visibility = View.VISIBLE
                if (mProfileType == "own")
                    binding.tvEmptyList.text = "You don't have any lesson created yet, contact admin to create lessons"
                else
                    binding.tvEmptyList.text = "We don't have any lesson from this teacher, try again after some time"
            }
        } else {
            lessonGroupsAdapter.lessonGroups = teacherLessonGroup
        }

    }

}