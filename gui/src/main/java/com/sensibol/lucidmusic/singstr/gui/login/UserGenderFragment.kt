package com.sensibol.lucidmusic.singstr.gui.login


import android.view.LayoutInflater
import android.view.View.*
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentUserGenderBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class UserGenderFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_user_gender
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentUserGenderBinding::inflate
    override val binding get(): FragmentUserGenderBinding = super.binding as FragmentUserGenderBinding

    private lateinit var bottomSheetMonthBehavior: BottomSheetBehavior<ConstraintLayout>
    private val loginViewModel: LoginViewModel by activityViewModels()

    private lateinit var genderValue:String
    var day:String = ""
    var month:String = ""
    var monthValue:String = ""
    var year:String = ""

    override fun onInitView() {
        binding.apply {

            tvMale.setOnClickListener {
                day = tvDay.text.toString()
                month = tvMonth.text.toString()
                year = tvYear.text.toString()
                genderValue = "Male"
                tvMale.background = ContextCompat.getDrawable(tvMale.context, R.drawable.bg_rounded_card_outline_blue)
                tvFemale.background = ContextCompat.getDrawable(tvFemale.context, R.drawable.bg_rounded_card_outline)
                tvOther.background = ContextCompat.getDrawable(tvOther.context, R.drawable.bg_rounded_card_outline)
                tvNextBtn.background = ContextCompat.getDrawable(tvNextBtn.context, R.drawable.bg_rounded_card_bue_login_with_otp)
            }
            tvFemale.setOnClickListener {
                day = tvDay.text.toString()
                month = tvMonth.text.toString()
                year = tvYear.text.toString()
                genderValue = "Female"
                tvFemale.background = ContextCompat.getDrawable(tvMale.context, R.drawable.bg_rounded_card_outline_blue)
                tvMale.background = ContextCompat.getDrawable(tvFemale.context, R.drawable.bg_rounded_card_outline)
                tvOther.background = ContextCompat.getDrawable(tvOther.context, R.drawable.bg_rounded_card_outline)
                tvNextBtn.background = ContextCompat.getDrawable(tvNextBtn.context, R.drawable.bg_rounded_card_bue_login_with_otp)
            }
            tvOther.setOnClickListener {
                day = tvDay.text.toString()
                month = tvMonth.text.toString()
                year = tvYear.text.toString()
                genderValue = "Other"
                tvOther.background = ContextCompat.getDrawable(tvMale.context, R.drawable.bg_rounded_card_outline_blue)
                tvFemale.background = ContextCompat.getDrawable(tvFemale.context, R.drawable.bg_rounded_card_outline)
                tvMale.background = ContextCompat.getDrawable(tvOther.context, R.drawable.bg_rounded_card_outline)
                tvNextBtn.background = ContextCompat.getDrawable(tvNextBtn.context, R.drawable.bg_rounded_card_bue_login_with_otp)
            }

            ivBack.setOnClickListener {
                requireActivity().onBackPressed()
            }

            bottomSheetMonthBehavior = BottomSheetBehavior.from(incMonthBottomSheet.clRoot)
            bottomSheetMonthBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            tvMonth.setOnClickListener {
                showMonthNameDailog(tvMonth)
            }

            incMonthBottomSheet.clRoot.visibility = INVISIBLE

            tvNextBtn.setOnClickListener {
                if (day.isEmpty()){
                    Toast.makeText(requireContext(),"Day can't be blank",Toast.LENGTH_SHORT).show()
                }else if (month.isEmpty()){
                    Toast.makeText(requireContext(),"Month can't be blank",Toast.LENGTH_SHORT).show()
                }else if (year.isEmpty()){
                    Toast.makeText(requireContext(),"Year can't be blank",Toast.LENGTH_SHORT).show()
                }else{
                    day = if(day.toInt()<=9){
                        "0$day"
                    }else{
                        day
                    }
                    val dobValue = "$year-$monthValue-$day"+"T00:00:00.000+00:00"
                    if(this@UserGenderFragment::genderValue.isInitialized){
                        loginViewModel.apply {
                            dob = dobValue
                            gender = genderValue
                        }
                        findNavController().navigate(UserGenderFragmentDirections.toSingerTypeFragment())

                    }

                }
            }

        }
    }

    private fun showMonthNameDailog(month:TextView) {
        bottomSheetMonthBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.apply {
            incMonthBottomSheet.apply {
                incMonthBottomSheet.clRoot.visibility = VISIBLE
                tvJan.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = INVISIBLE
                    month.text = "January"
                    monthValue = "01"
                }
                tvFeb.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = INVISIBLE
                    month.text = "February"
                    monthValue = "02"
                }
                tvMarch.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = INVISIBLE
                    month.text = "March"
                    monthValue = "03"
                }
                tvApril.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = INVISIBLE
                    month.text = "April"
                    monthValue = "04"
                }
                tvMay.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = INVISIBLE
                    month.text = "May"
                    monthValue = "05"
                }
                tvJune.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = INVISIBLE
                    month.text = "June"
                    monthValue = "06"
                }
                tvJuly.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = INVISIBLE
                    month.text = "July"
                    monthValue = "07"
                }
                tvAugust.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = INVISIBLE
                    month.text = "August"
                    monthValue = "08"
                }
                tvSep.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = INVISIBLE
                    month.text = "September"
                    monthValue = "09"
                }
                tvOct.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = INVISIBLE
                    month.text = "October"
                    monthValue = "10"
                }
                tvNov.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = INVISIBLE
                    month.text = "November"
                    monthValue = "11"
                }
                tvDec.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = INVISIBLE
                    month.text = "December"
                    monthValue = "12"
                }
                tvDash.setOnClickListener {
                    incMonthBottomSheet.clRoot.visibility = GONE
                }
            }
        }
    }


}