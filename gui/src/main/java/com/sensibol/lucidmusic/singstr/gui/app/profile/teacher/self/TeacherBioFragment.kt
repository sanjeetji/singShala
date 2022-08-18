package com.sensibol.lucidmusic.singstr.gui.app.profile.teacher.self

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.TeacherDetails
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.profile.TeacherBioViewModel
import com.sensibol.lucidmusic.singstr.gui.app.profile.teacher.TeacherDetailsViewModel
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentTeacherBioBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TeacherBioFragment : BaseFragment() {
    override val layoutResId: Int get() = R.layout.fragment_teacher_bio
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding get() = FragmentTeacherBioBinding::inflate
    override val binding: FragmentTeacherBioBinding get() = super.binding as FragmentTeacherBioBinding

    private val viewModel: TeacherBioViewModel by viewModels()

    private lateinit var mTeacherId: String
    private lateinit var mProfileType: String

    @Inject
    internal lateinit var teacherBioAdapter: TeacherBioAdapter

    companion object {
        private const val TEACHER_ID = "teacher_id"
        private const val TEACHER_PROFILE_TYPE = "teacher_profile_type"

        fun newInstance(teacherID: String, profileType: String) = TeacherBioFragment().apply {
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
        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(teacherDetails, :: showTeacherBioDetails)
            loadTeacherDetails(mTeacherId)
        }
    }

    private fun showTeacherBioDetails(teacherBioDetails: TeacherDetails) {

        Timber.d("showTeacherBioDetails nikhil---?")
        binding.apply {

            Timber.d("")
            teacherBioAdapter.teacherDetail = teacherBioDetails.attributes

            teacherImg.loadUrl(teacherBioDetails.profile_img_url)
            tvDisplayName.text = teacherBioDetails.name
            tvCategory.text = teacherBioDetails.bio

            rvTeacherQualification.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = teacherBioAdapter
            }
        }
    }

}