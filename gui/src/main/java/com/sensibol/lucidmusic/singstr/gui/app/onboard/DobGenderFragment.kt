package com.sensibol.lucidmusic.singstr.gui.app.onboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentDobGenderBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import com.sensibol.lucidmusic.singstr.gui.login.UserGenderFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DobGenderFragment : BaseFragment() {
    override val layoutResId: Int = R.layout.fragment_dob_gender
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentDobGenderBinding::inflate
    override val binding: FragmentDobGenderBinding get() = super.binding as FragmentDobGenderBinding

    private val viewModel: DobGenderViewModel by viewModels()

    private val arg: DobGenderFragmentArgs by navArgs()

    private lateinit var bottomSheetMonthBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var gender: String
    private lateinit var day:String
    private lateinit var month:String
    private lateinit var monthValue:String
    private lateinit var year:String

    override fun onInitView() {

        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(updateUser){
                findNavController().navigate(DobGenderFragmentDirections.toSingerLevelFragment(arg.firstName))
            }
        }

        binding.apply {

//            tvNextBtn.isEnabled = false
            progressBar.visibility = View.GONE

            bottomSheetMonthBehavior = BottomSheetBehavior.from(incMonthBottomSheet.clRoot)
            bottomSheetMonthBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            tvMonth.setOnClickListener {
                showMonthNameDialog(tvMonth)
            }

            tvNextBtn.setOnClickListener {
                day = tvDay.text.toString().trim()
                year = tvYear.text.toString().trim()
                month = monthValue
                when(rgGender.checkedRadioButtonId){
                    R.id.rb_male -> gender = "Male"
                    R.id.rb_female -> gender = "Female"
                    R.id.rb_other -> gender = "NotSpecified"
                    else -> Toast.makeText(requireContext(), "Select Gender to continue", Toast.LENGTH_SHORT).show()
                }
                if (day.isEmpty()){
                    Toast.makeText(requireContext(),"Day can't be blank", Toast.LENGTH_SHORT).show()
                }else if (month.isEmpty()){
                    Toast.makeText(requireContext(),"Month can't be blank", Toast.LENGTH_SHORT).show()
                }else if (year.isEmpty()){
                    Toast.makeText(requireContext(),"Year can't be blank", Toast.LENGTH_SHORT).show()
                }else{
                    day = if(day.toInt()<=9){
                        "0$day"
                    }else{
                        day
                    }
                    val dobValue = "$year-$month-$day"+"T00:00:00.000+00:00"
                    if(this@DobGenderFragment ::gender.isInitialized){
                        progressBar.visibility = View.VISIBLE
                        viewModel.updateUserDetail(
                            arg.firstName, gender, dobValue
                        )
                    }

                }
            }
        }
    }

    private fun showMonthNameDialog(month: TextView) {
        bottomSheetMonthBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.apply {
            incMonthBottomSheet.apply {
                incMonthBottomSheet.clRoot.visibility = View.VISIBLE
                tvJan.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = View.INVISIBLE
                    month.text = "January"
                    monthValue = "01"
                }
                tvFeb.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = View.INVISIBLE
                    month.text = "February"
                    monthValue = "02"
                }
                tvMarch.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = View.INVISIBLE
                    month.text = "March"
                    monthValue = "03"
                }
                tvApril.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = View.INVISIBLE
                    month.text = "April"
                    monthValue = "04"
                }
                tvMay.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = View.INVISIBLE
                    month.text = "May"
                    monthValue = "05"
                }
                tvJune.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = View.INVISIBLE
                    month.text = "June"
                    monthValue = "06"
                }
                tvJuly.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = View.INVISIBLE
                    month.text = "July"
                    monthValue = "07"
                }
                tvAugust.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = View.INVISIBLE
                    month.text = "August"
                    monthValue = "08"
                }
                tvSep.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = View.INVISIBLE
                    month.text = "September"
                    monthValue = "09"
                }
                tvOct.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = View.INVISIBLE
                    month.text = "October"
                    monthValue = "10"
                }
                tvNov.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = View.INVISIBLE
                    month.text = "November"
                    monthValue = "11"
                }
                tvDec.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = View.INVISIBLE
                    month.text = "December"
                    monthValue = "12"
                }
                tvDash.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = View.GONE
                }
            }
        }
    }
}