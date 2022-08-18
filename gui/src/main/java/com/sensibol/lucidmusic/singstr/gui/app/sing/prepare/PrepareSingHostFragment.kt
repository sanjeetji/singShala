package com.sensibol.lucidmusic.singstr.gui.app.sing.prepare

import android.Manifest.permission.CAMERA
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavArgument
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.AppToast
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.karaoke.Doorway
import com.sensibol.lucidmusic.singstr.domain.model.SingMode
import com.sensibol.lucidmusic.singstr.domain.model.SingScore
import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.app.sing.prepare.mode.SingModeFragmentArgs
import com.sensibol.lucidmusic.singstr.gui.app.sing.prepare.song.SongSelectorHostFragmentArgs
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentPrepareSingHostBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
internal class PrepareSingHostFragment : BaseFragment() {

    override val layoutResId: Int get() = R.layout.fragment_prepare_sing_host
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding get() = FragmentPrepareSingHostBinding::inflate
    override val binding: FragmentPrepareSingHostBinding get() = super.binding as FragmentPrepareSingHostBinding

    private val singstrViewModel: SingstrViewModel by activityViewModels()

    private val args: PrepareSingHostFragmentArgs by navArgs()

    override fun onInitView() {
        singstrViewModel.apply {
            observe(cameraFacing, ::startCamera)
        }
        Timber.v("onInitView: IN")
        binding.apply {
            pvViewFinder.visibility = GONE
        }
        if (PERMISSION_GRANTED == checkSelfPermission(requireContext(), CAMERA)) {
            setupView()
        } else {
            requestCameraPermissionLauncher.launch(CAMERA)
        }
        Timber.v("onInitView: OUT")
    }

    internal fun onCoverComplete(singMode: String, singScore: SingScore, song: SongMini) {
        Timber.d("onCoverComplete: $singScore")

        findNavController().navigate(
            when (singMode) {
                SingMode.PRACTICE.argName -> PrepareSingHostFragmentDirections.toPracticeResultFrag(song, singScore)
                else -> PrepareSingHostFragmentDirections.toCoverResultFragment(song, singScore)
            }
        )
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            setupView()
        } else {
            findNavController().popBackStack(R.id.prepareSingHostFragment, true)
            AppToast.show(requireContext(), "Camera Permission Needed")
        }
    }

    private fun setupView() {
        Timber.v("setupView: IN")
        binding.apply {
            pvViewFinder.visibility = VISIBLE
            ivDefaultBG.visibility = GONE
            ibClose.setOnClickListener {
                findNavController().popBackStack(R.id.prepareSingHostFragment, true)
            }
        }
        (childFragmentManager.findFragmentById(R.id.navHostFragmentPrepareSing) as NavHostFragment).navController.apply {
            graph = navInflater.inflate(R.navigation.prepare_sing).apply {
                val songId = args.songId
                if (songId.isNullOrBlank()) {
                    startDestination = R.id.songSelectorHostFragment
                    SongSelectorHostFragmentArgs(args.singMode).toBundle().let { bundle ->
                        bundle.keySet().forEach { key ->
                            addArgument(key, NavArgument.Builder().setDefaultValue(bundle[key]).build())
                        }
                    }
                } else {
                    startDestination = R.id.singModeFragment
                    SingModeFragmentArgs(songId, args.singMode).toBundle().let { bundle ->
                        bundle.keySet().forEach { key ->
                            addArgument(key, NavArgument.Builder().setDefaultValue(bundle[key]).build())
                        }
                    }
                }
            }
        }
        startCamera(Doorway.CameraFacing.FRONT)
        Timber.v("setupView: OUT")
    }

    internal fun switchCamera(cameraFacing: Int) {
        singstrViewModel.switchCamera(cameraFacing)
    }

    private fun startCamera(cameraFacing: Int) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
                .also {
                    it.setSurfaceProvider(binding.pvViewFinder.surfaceProvider)
                }
            try {
                cameraProvider.unbindAll()
                if (cameraFacing == Doorway.CameraFacing.FRONT) {
                    cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_FRONT_CAMERA, preview)
                } else {
                    cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview)
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }
}