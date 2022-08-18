package com.sensibol.lucidmusic.singstr.gui.app.profile.teacher.self

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentTeacherOtherBioBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class TeacherOtherBioFragment : BaseFragment() {
    override val layoutResId: Int get() = R.layout.fragment_teacher_other_bio
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding get() = FragmentTeacherOtherBioBinding::inflate
    override val binding: FragmentTeacherOtherBioBinding get() = super.binding as FragmentTeacherOtherBioBinding


    override fun onInitView() {
        Timber.e("========= TeacherSelfCoverFragment")
    }

}