package com.sensibol.lucidmusic.singstr.gui.app.onboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentOnBoardLanguageBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardLanguageFragment : BaseFragment() {
    override val layoutResId: Int = R.layout.fragment_on_board_language
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentOnBoardLanguageBinding::inflate
    override val binding: FragmentOnBoardLanguageBinding get() = super.binding as FragmentOnBoardLanguageBinding

    @Inject
    lateinit var onBoardLanguageAdapter: OnBoardLanguageAdapter

    private var listObserver: MutableLiveData<HashSet<String>> = MutableLiveData<HashSet<String>>()

    private val viewModel: OnBoardGenreViewModel by viewModels()

    private var selectedLanguage: HashSet<String> = HashSet()

    override fun onInitView() {
        observe(listObserver, ::activeNextButton)
        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(addUserPreference, ::navigateToNextMove)
        }

        binding.apply {
            rvlanguage.apply {
                isNestedScrollingEnabled = false
                layoutManager = LinearLayoutManager(context)
                adapter = onBoardLanguageAdapter
            }
            onBoardLanguageAdapter.onLanguageCheckChangeListener = { language: String, isCheck: Boolean ->
                when (isCheck) {
                    true -> {
                        selectedLanguage.add(language)
                        listObserver.postValue(selectedLanguage)
                    }
                    false -> {
                        selectedLanguage.remove(language)
                        listObserver.postValue(selectedLanguage)
                    }
                }
                Analytics.setUserProperty(Analytics.UserProperty.UserPrefLanguage(selectedLanguage))
            }
            /*rdgrp.setOnCheckedChangeListener { group, checkedId ->
                tvNext.isEnabled = true
                language = when (checkedId) {
                    rdbtnEnglish.id -> arrayOf("English")
                    rdBtnHindi.id -> arrayOf("Hindi")
                    else -> arrayOf("Hindi", "English")
                }
            }*/
            tvSkip.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.SkippedWalkThroughEvent(
                        Analytics.Event.Param.SkippedScreenName("Language Selection")
                    )
                )
                navigateToNextMove(true)
            }

            tvNext.setOnClickListener {
                val list: List<String> = ArrayList<String>(selectedLanguage)
                viewModel.addUserPreference(null, list)
            }
        }
    }

    private fun navigateToNextMove(b: Boolean) {
        findNavController().popBackStack(R.id.onBoardLanguageFragment, true)
    }

    private fun activeNextButton(hashSet: HashSet<String>) {
        when (hashSet.size) {
            0 -> binding.tvNext.isEnabled = false
            else -> binding.tvNext.isEnabled = true
        }
    }
}