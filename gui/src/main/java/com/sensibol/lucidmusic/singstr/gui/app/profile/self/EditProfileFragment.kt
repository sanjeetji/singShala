package com.sensibol.lucidmusic.singstr.gui.app.profile.self

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.camera2.CameraCharacteristics
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.PermissionChecker
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.AppToast
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.User
import com.sensibol.lucidmusic.singstr.gui.*
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentEditProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class EditProfileFragment : BaseFragment() {
    override val layoutResId: Int = R.layout.fragment_edit_profile
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentEditProfileBinding::inflate
    override val binding get():FragmentEditProfileBinding = super.binding as FragmentEditProfileBinding

    private val args: EditProfileFragmentArgs by navArgs()

    private val viewModel: EditProfileViewModel by viewModels()

    private val singstrViewModel: SingstrViewModel by activityViewModels()

    private var isUserHandleChanged: Boolean = false

    private lateinit var selectedDate: String

    private lateinit var userId : String

    override fun onInitView() {
        Timber.v("onInitView")
        singstrViewModel.apply {
            failure(failure, ::handleFailure)
            observe(user, ::showUserProfile)
            loadUserProfile()
        }

        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(updateUser, ::handleNavigation)
            observe(isUserHandleAvailable, ::showUserNameExistError)
        }

        binding.apply {
            val items = arrayOf("Female", "Male", "NotSpecified")
            etGender.apply {
                setDropDownBackgroundResource(R.color.bg_page)
                setAdapter(ArrayAdapter(context, R.layout.dropdown_item_textview, items))
            }
            etState.apply {
                val states = getStateList()
                setDropDownBackgroundResource(R.color.bg_page)
                setAdapter(ArrayAdapter(context, R.layout.dropdown_item_textview, states))
            }

            etDob.setOnClickListener { v ->
                openDatePickerForDob(v as EditText)
            }

            tvRemove.setOnClickListener {
                ivProfilePic.setImageResource(R.drawable.ic_profile_placeholder)
                ivPicker.visibility = View.VISIBLE
                tvSaveChanges.isEnabled=true
                tvRemove.visibility = View.GONE
            }

            etHandle.setOnEditorActionListener { v, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_NEXT -> {
                        viewModel.checkUserHandleAvailability(etHandle.text.toString().trim())
                        true
                    }
                    else -> false
                }
            }

            ivProfilePic.setOnClickListener {
                if (!tvRemove.isVisible) {
                    openImageChooser()
                }
            }
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
            tvSaveChanges.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.SaveProfileEvent(Analytics.Event.Param.UserId(userId))
                )
                etCity.addTextChangedListener(onTextChanged = { text: CharSequence?, start: Int, before: Int, count: Int ->
                    if (!validateTextUsingPattern(etCity.text.toString(), "^(?![._])(?!.*[._]$)[a-zA-Z0-9- ]+$")) {
                        AppToast.show(binding.root.context, "Invalid City")
                    }
                })
                etName.addTextChangedListener(onTextChanged = { text :CharSequence?, start :Int, before :Int, count :Int ->
                    if (!validateTextUsingPattern(
                            etName.text.toString(),
                            "^(?![._])(?!.*[._]$)[a-zA-Z0-9- ]+$"
                        )
                    ) {
                        AppToast.show(binding.root.context, "Invalid Name")
                    }
                })
                etHandle.addTextChangedListener(onTextChanged = { text :CharSequence?, start :Int, before :Int, count :Int ->
                    if (!validateTextUsingPattern(etHandle.text.toString().trim(), "[*a-zA-Z0-9.]*$")) {
                        AppToast.show(binding.root.context, "Invalid City")
                    }
                })
                viewModel.updateUserDetail(
                    etName.text.toString().trim(),
                    etBio.text.toString().trim(),
                    selectedDate,
                    etGender.text.toString(),
                    when (isUserHandleChanged) {
                        true -> etHandle.text.toString().trim()
                        false -> null
                    },
                    etCity.text.toString().trim(),
                    etState.text.toString().trim()
                )
            }
        }
    }

    private fun showUserNameExistError(isAvailable: Boolean) {
        if (isAvailable) {
            binding.tilUsername.isErrorEnabled = false
            binding.tilUsername.isEndIconVisible = true
            binding.tilUsername.setEndIconDrawable(R.drawable.ic_check_green)
            binding.etName.requestFocus()
        } else {
            binding.tilUsername.isErrorEnabled = true
            binding.tilUsername.isEndIconVisible = false
            binding.tilUsername.setErrorIconDrawable(0)
            binding.tilUsername.error = "This username is already taken, use a different one"
        }
    }


    private fun showUserProfile(user: User) {
        userId = user.id
        if (user.city.isNotEmpty() || user.state.isNotEmpty()) {
            Analytics.setUserProperty(Analytics.UserProperty.UserLocation("${user.city} ${user.state}"))
        }
        binding.apply {
            etHandle.setText(user.handle)
            etName.setText(user.name)
            etState.setText(user.state, false)
            etCity.setText(user.city)
            etDob.setText(convertDatePattern(user.dob))
            etGender.setText(user.sex, false)
            ivProfilePic.loadUrl(args.profileData.profileURL)
            etBio.setText(args.profileData.status)
            selectedDate = user.dob

            etName.addTextChangedListener(onTextChanged = { text: CharSequence?, start: Int, before: Int, count: Int ->
                checkChanges(user)
            })

            etBio.addTextChangedListener(onTextChanged = { text: CharSequence?, start: Int, before: Int, count: Int ->
                checkChanges(user)
            })

            etHandle.addTextChangedListener(onTextChanged = { text: CharSequence?, start: Int, before: Int, count: Int ->
                isUserHandleChanged = !user.handle.equals(text.toString())
                checkChanges(user)
            })

            etState.addTextChangedListener(onTextChanged = { text: CharSequence?, start: Int, before: Int, count: Int ->
                checkChanges(user)
            })

            etCity.addTextChangedListener(onTextChanged = { text: CharSequence?, start: Int, before: Int, count: Int ->
                checkChanges(user)

            })

            etGender.addTextChangedListener(onTextChanged = { text: CharSequence?, start: Int, before: Int, count: Int ->
                checkChanges(user)
            })

            etDob.addTextChangedListener(onTextChanged = { text: CharSequence?, start: Int, before: Int, count: Int ->
                checkChanges(user)
            })
        }
    }

    private fun checkChanges(user: User) {
        binding.apply {
            tvSaveChanges.isEnabled = user.name != etName.text.toString().trim() ||
                    user.handle != etHandle.text.toString().trim() ||
                    args.profileData.status != etBio.text.toString().trim() ||
                    user.state != etState.text.toString().trim() ||
                    user.city != etCity.text.toString().trim() ||
                    user.sex != etGender.text.toString().trim() ||
                    convertDatePattern(user.dob) != etDob.text.toString().trim()
        }
    }

    private fun handleNavigation(success: Boolean) {
        when (success) {
            true -> {
                AppToast.show(binding.root.context, "Great! Your Profile's Updated")
                findNavController().popBackStack();
            }
        }
    }

    fun openImageChooser() {
        if (PermissionChecker.PERMISSION_GRANTED == PermissionChecker.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)) {
            selectImage()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }


    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            selectImage()
        } else {
            AppToast.show(requireContext(), "Camera Permission Needed")
        }
    }

    var resultLauncher = this.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            val data: Intent? = result.data
            if (data?.extras != null) {

                binding.apply {
                    ivProfilePic.loadUrl(getImageUri(requireActivity(), data.extras?.get("data") as Bitmap).toString())
                    ivPicker.visibility = View.GONE
                    tvRemove.visibility = View.VISIBLE
                }
            } else {
                binding.apply {
                    ivProfilePic.loadUrl(data?.data.toString())
                    ivPicker.visibility = View.GONE
                    tvRemove.visibility = View.VISIBLE
                }
            }

            if (data?.extras != null) {
                updateProfilePic(getImageUri(requireActivity(), data.extras?.get("data") as Bitmap))
            } else {
                updateProfilePic(data?.data)
            }
        }
    }

    private fun updateProfilePic(path: Uri?) {
        val parcelFileDescriptor =
            requireActivity().contentResolver.openFileDescriptor(path!!, "r", null) ?: return

        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(requireActivity().cacheDir, requireActivity().contentResolver.getFileName(path))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        viewModel.updateProfilePic(file)
    }

    private fun openDatePickerForDob(etDob: EditText) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        c.add(Calendar.YEAR, -3)

        val dpd =
            DatePickerDialog(requireContext(), R.style.DatePickerTheme, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, monthOfYear, dayOfMonth)
                var sdf = SimpleDateFormat("dd-MM-yyyy")
                etDob.setText(sdf.format(calendar.time))
                sdf = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'")
                selectedDate = sdf.format(calendar.time)
            }, year, month, day)
        dpd.datePicker.maxDate = c.timeInMillis
        dpd.show()
    }

    fun getImageUri(inContext: Activity, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 70, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private fun selectImage() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Select Photo")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> {
                            takePictureIntent.putExtra(
                                "android.intent.extras.CAMERA_FACING",
                                CameraCharacteristics.LENS_FACING_FRONT
                            )  // Tested on API 24 Android version 7.0(Samsung S6)
                        }
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                            takePictureIntent.putExtra(
                                "android.intent.extras.CAMERA_FACING",
                                CameraCharacteristics.LENS_FACING_FRONT
                            ) // Tested on API 27 Android version 8.0(Nexus 6P)
                            takePictureIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
                        }
                        else -> takePictureIntent.putExtra(
                            "android.intent.extras.CAMERA_FACING",
                            0
                        )  // Tested API 21 Android version 5.0.1(Samsung S4)
                    }
                    resultLauncher.launch(takePictureIntent)
                }
                options[item] == "Choose from Gallery" -> {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    resultLauncher.launch(intent)
                }
                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }
}