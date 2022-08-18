package com.sensibol.lucidmusic.singstr.gui.app.onboard

import android.app.DatePickerDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentOnBoardDetailsBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
internal class OnBoardDetailsFragment : BaseFragment() {
    override val layoutResId: Int = R.layout.fragment_on_board_details
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentOnBoardDetailsBinding::inflate
    override val binding: FragmentOnBoardDetailsBinding get() = super.binding as FragmentOnBoardDetailsBinding

    private val args: OnBoardDetailsFragmentArgs by navArgs()
    private lateinit var selectedDate: String

    private val viewmodel: OnBoardUserDetailsViewModel by viewModels()

    override fun onInitView() {
        viewmodel.apply {
            failure(failure, ::handleFailure)
            observe(success, ::handleNavigation)
        }

        binding.apply {
            val items = arrayOf("Female", "Male", "Non-Binary")
            etGender.apply {
                setDropDownBackgroundResource(R.color.bg_page)
                setAdapter(ArrayAdapter(context, R.layout.dropdown_item_textview, items))
                setOnItemClickListener { parent, view, position, id ->
                    enableNextBtn()
                }
            }
            etDob.setOnClickListener { v ->
                openDatePickerForDob(v as EditText)
            }
            tvNext.setOnClickListener {
                viewmodel.updateUserDetail(args.userName, selectedDate, etGender.text.toString())
            }
            tvSkip.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.SkippedWalkThroughEvent(
                        Analytics.Event.Param.SkippedScreenName("User Details")
                    )
                )
//                findNavController().navigate(OnBoardDetailsFragmentDirections.toOnBoardGenreFragment())
                findNavController().navigate(OnBoardDetailsFragmentDirections.toOnBoardSongRecommendationFragment())
            }
        }
    }

    private fun enableNextBtn() {
        binding.tvNext.isEnabled = binding.etGender.text.isNotEmpty() && binding.etDob.text?.isNotEmpty() ?: false
    }

    private fun openDatePickerForDob(etDob: EditText) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        c.add(Calendar.YEAR, -16)

        val dpd =
            DatePickerDialog(requireContext(), R.style.DatePickerTheme, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, monthOfYear, dayOfMonth)
                var sdf = SimpleDateFormat("dd/MM/yyyy")
                etDob.setText(sdf.format(calendar.time))
                sdf = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'")
                selectedDate = sdf.format(calendar.time)
                enableNextBtn()
            }, year, month, day)
        dpd.datePicker.maxDate = c.timeInMillis
        dpd.show()
    }

    private fun handleNavigation(isUserUpdated: Boolean) {
        findNavController().navigate(OnBoardDetailsFragmentDirections.toOnBoardSongRecommendationFragment())
//        findNavController().navigate(OnBoardDetailsFragmentDirections.toOnBoardGenreFragment())
    }
}