package com.sensibol.lucidmusic.singstr.gui.app.sing.result.cover

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.view.drawToBitmap
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.applozic.mobicomkit.uiwidgets.people.activity.MobiComKitPeopleActivity
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import com.sensibol.android.base.gui.AppToast
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.lucidmusic.singstr.domain.model.SingMode
import com.sensibol.lucidmusic.singstr.domain.model.names
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentScoreCardBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import java.io.File
import java.io.OutputStream
import kotlin.math.roundToInt

class ScoreCardFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_score_card
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentScoreCardBinding::inflate
    override val binding: FragmentScoreCardBinding get() = super.binding as FragmentScoreCardBinding

    private val args: ScoreCardFragmentArgs by navArgs()
    private val singstrViewModel: SingstrViewModel by activityViewModels()

    private var songLink: String? = null

    private lateinit var imageUri: Uri

    private var imageLoaded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        singstrViewModel.apply {
            loadUserProfile()
        }

        createSongDynamicLink()
    }

    override fun onInitView() {

        binding.apply {
            ivCoverThumbnail.loadUrl(args.songMini.thumbnailUrl)
            ivUserImage.loadUrl(singstrViewModel.user.value?.dpUrl.toString())
            tvArtist.text = args.songMini.artists.names
            tvSongTitle.text = args.songMini.title
            tvYourScore.text = args.singScore.totalScore.roundToInt().toString()
            tvBeatMyScoreTitle.text = "Beat my singing score for ${args.songMini.title}"

            btnDownloadImg.setOnClickListener {
                if (imageLoaded)
                    Toast.makeText(requireContext(), "Saved to Pictures.", Toast.LENGTH_SHORT).show()
                else
                    havePermission()
            }

            ivWhatsapp.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.CoverScoreCardShareEvent(
                        Analytics.Event.Param.SongId(args.songMini.id),
                        Analytics.Event.Param.GenreId("NA"),
                        Analytics.Event.Param.ArtistId(args.songMini.artists.names),
                        Analytics.Event.Param.TotalScore(args.singScore.totalScore.roundToInt()),
                        Analytics.Event.Param.SharedOnApp("Whatsapp")

                    )
                )
                shareIntentToApp("Whatsapp")
            }

            ivFacebook.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.CoverScoreCardShareEvent(
                        Analytics.Event.Param.SongId(args.songMini.id),
                        Analytics.Event.Param.GenreId("NA"),
                        Analytics.Event.Param.ArtistId(args.songMini.artists.names),
                        Analytics.Event.Param.TotalScore(args.singScore.totalScore.roundToInt()),
                        Analytics.Event.Param.SharedOnApp("Facebook")

                    )
                )
                shareIntentToApp("Facebook")
            }

            ivSingshala.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.CoverScoreCardShareEvent(
                        Analytics.Event.Param.SongId(args.songMini.id),
                        Analytics.Event.Param.GenreId("NA"),
                        Analytics.Event.Param.ArtistId(args.songMini.artists.names),
                        Analytics.Event.Param.TotalScore(args.singScore.totalScore.roundToInt()),
                        Analytics.Event.Param.SharedOnApp("SingShala")

                    )
                )
                shareIntentToApp("SingShala")
            }

            ivInstagram.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.CoverScoreCardShareEvent(
                        Analytics.Event.Param.SongId(args.songMini.id),
                        Analytics.Event.Param.GenreId("NA"),
                        Analytics.Event.Param.ArtistId(args.songMini.artists.names),
                        Analytics.Event.Param.TotalScore(args.singScore.totalScore.roundToInt()),
                        Analytics.Event.Param.SharedOnApp("Instagram")

                    )
                )
                shareIntentToApp("Instagram")
            }

            ivTwitter.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.CoverScoreCardShareEvent(
                        Analytics.Event.Param.SongId(args.songMini.id),
                        Analytics.Event.Param.GenreId("NA"),
                        Analytics.Event.Param.ArtistId(args.songMini.artists.names),
                        Analytics.Event.Param.TotalScore(args.singScore.totalScore.roundToInt()),
                        Analytics.Event.Param.SharedOnApp("Twitter")

                    )
                )
                shareIntentToApp("Twitter")
            }

            ivOthers.setOnClickListener {
                if (PermissionChecker.PERMISSION_GRANTED == PermissionChecker.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    shareScoreCardIntent()

                } else {
                    requestStoragePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }

            ibClose.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun havePermission(): Boolean {
        return if (PermissionChecker.PERMISSION_GRANTED == PermissionChecker.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            imageUri = getImageUri()
            true
        } else {
            requestStoragePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            false
        }

    }

    private fun getImageUri(): Uri {
        val cardBitmap = binding.clScoreCard.drawToBitmap()
        val share = Intent(Intent.ACTION_SEND)
        share.type = "image/*"
        val path = MediaStore.Images.Media.insertImage(
            requireActivity().contentResolver,
            cardBitmap, "Title", null
        )
        imageLoaded = true
        return Uri.parse(path)
    }

    private fun shareScoreCardIntent() {
        if (!imageLoaded) {
            havePermission()
            return
        }

        Analytics.logEvent(
            Analytics.Event.CoverScoreCardShareEvent(
                Analytics.Event.Param.SongId(args.songMini.id),
                Analytics.Event.Param.GenreId("NA"),
                Analytics.Event.Param.ArtistId(args.songMini.artists.names),
                Analytics.Event.Param.TotalScore(args.singScore.totalScore.roundToInt()),
                Analytics.Event.Param.SharedOnApp("Saved To Device")

            )
        )

        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Beat my singing score for ${args.songMini.title} $songLink")
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
        shareIntent.type = "image/*"
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private val requestStoragePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            imageUri = getImageUri()
        } else {
            AppToast.show(requireContext(), "Storage Permission Needed")
            findNavController().navigateUp()
        }
    }

    private fun shareIntentToApp(appName: String) {

        if (!imageLoaded) {
            havePermission()
            return
        }

        val sharingMsg = "Beat my singing score for ${args.songMini.title} $songLink"

        val pacakgeName = when (appName) {
            "Whatsapp" -> "com.whatsapp"
            "SingShala" -> "SingShala"
            "Twitter" -> "com.twitter.android"
            "Instagram" -> "com.instagram.android"
            "Facebook" -> "com.facebook.katana"
            else -> null
        }

        if(pacakgeName == "SingShala"){
            val shareIntent = Intent(requireContext(), MobiComKitPeopleActivity::class.java)
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_TEXT, sharingMsg)
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
            shareIntent.type = "image/*"
            startActivity(shareIntent)
            return
        }
        copyToClipBoard(sharingMsg)
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.setPackage(pacakgeName!!)
        shareIntent.putExtra(Intent.EXTRA_TEXT, sharingMsg)
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
        shareIntent.type = "image/*"
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            if (appName == "Instagram" || appName == "Instagram")
                Toast.makeText(requireContext(), "Link is copied ! Use paste to send link too.", Toast.LENGTH_SHORT).show()

            startActivity(shareIntent)

        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "$appName is not installed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createSongDynamicLink() {
        Firebase.dynamicLinks.shortLinkAsync {
            link = Uri.parse("https://www.singshala.com/app/songs/${args.songMini.id}/${SingMode.RECORD}")
            domainUriPrefix = "https://singshala.page.link"
            androidParameters("com.lucidmusic.singstr") {
            }
//            iosParameters("com.example.ios") {
//                appStoreId = "123456789"
//                minimumVersion = "1.0.1"
//            }
            googleAnalyticsParameters {
                source = "android"
                medium = "social"
                campaign = "score-card"
            }
            socialMetaTagParameters {
                title = "Singhshala Lesson "
                description = "Beat my singing score for ${args.songMini.title}"
            }
        }.addOnSuccessListener { (shortLink, flowchartLink) ->
            songLink = shortLink.toString()
        }
    }

    private fun copyToClipBoard(message: String) {
        val clipBoard: android.content.ClipboardManager =
            ContextCompat.getSystemService(requireContext(), android.content.ClipboardManager::class.java) as android.content.ClipboardManager
        val clipData = ClipData.newPlainText("label", message)
        clipBoard.setPrimaryClip(clipData)
    }
}