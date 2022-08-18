package com.sensibol.lucidmusic.singstr.gui.app.feed.comments

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.bold
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.AllReplyComment
import com.sensibol.lucidmusic.singstr.domain.model.Comment
import com.sensibol.lucidmusic.singstr.domain.model.SearchData
import com.sensibol.lucidmusic.singstr.domain.model.SearchTags
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.app.search.SearchViewModel
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentCoverCommentsBinding
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentCoverReplyCommentsBinding
import com.sensibol.lucidmusic.singstr.gui.loadCenterCropImageFromUrl
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import com.sensibol.lucidmusic.singstr.gui.prettyViewsCount
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
internal class CoverReplyCommentsFragment : BaseFragment() {
    override val layoutResId: Int get() = R.layout.fragment_cover_reply_comments
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentCoverReplyCommentsBinding::inflate
    override val binding get():FragmentCoverReplyCommentsBinding = super.binding as FragmentCoverReplyCommentsBinding

    private val viewModel: CoverCommentsViewModel by viewModels()

    private val singstrViewModel: SingstrViewModel by activityViewModels()

    val args: CoverReplyCommentsFragmentArgs by navArgs()

    var nameChars: String = ""
    var userName: String = ""
    var commentText: String = ""
    var beforeUserName: String = ""
    var checkUserName: String = ""
    var userIdList: MutableList<String> = mutableListOf()

    private var intCount = 0
    private var initialStringLength = 0
    private var strText = ""

    @Inject
    internal lateinit var coverReplyCommentsAdapter: CoverReplyCommentsAdapter

    @Inject
    internal lateinit var coverCommentsUserAdapter: CoverCommentsUserAdapter

    override fun onInitView() {

        viewModel.apply {
            observe(getCommentReply, ::showComments)
            observe(addCommentReply, ::showCommentReplyResult)
            observe(getUserNames, ::showUserName)
            loadFeedCommentReply(args.commentId)
        }

        binding.apply {


            /*ivProfilePicture.loadCenterCropImageFromUrl(args.comment.userMini.dpUrl)
            singleTextView(tvUserComment, args.comment.userMini.handle, args.comment.comment, args.comment.userMini.id)
            tvTimestamp.text = SimpleDateFormat("dd MMM", Locale.ROOT).format(Date(TimeUnit.SECONDS.toMillis(
                args.comment.timestamp))
            )*/



            rvReplyComment.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = coverReplyCommentsAdapter
            }

            rvSearch.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = coverCommentsUserAdapter
            }

            vCommentBox.setOnClickListener {
                commentBoxVi.visibility = VISIBLE
            }
            commentInput.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (checkUserName.equals("exist")) {
                        Timber.e("========= Else Codition")
                        checkUserName = ""
                    } else {
                        if (s?.contains("@") == true) {
                            nameChars = commentInput.text.toString().substringAfterLast("@")
                            viewModel.loadUserName(nameChars, "user");
                        }
                    }

                    beforeUserName = commentInput.text.toString().substringBefore("@")
                    commentText = commentInput.text.toString()
                    tvCharCount.text = s?.length.toString()
                    tvDone.isEnabled = s?.length != 0
                    commentInput.setSelection(commentInput.text.length)
                }
            })
            textInputLayout.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) textInputLayout.hint = "" else textInputLayout.hint = "Write Comment.."
            }

            tvDone.setOnClickListener {

                Analytics.logEvent(
                    Analytics.Event.PostCommentEvent(
                        Analytics.Event.Param.SongId("NA"),
                        Analytics.Event.Param.CoverId(args.coverId),
                        Analytics.Event.Param.UserId(
                            if (singstrViewModel.user.value != null)
                                singstrViewModel.user.value.toString()
                            else "NA"
                        )
                    )
                )
                viewModel.loadAddFeedCommentReply(args.coverId, args.commentId, commentInput.text.toString(), userIdList)

                commentInput.text?.clear()
                commentBoxVi.visibility = GONE
            }
            ivBackFeed.setOnClickListener {
                findNavController().popBackStack()
            }

            coverReplyCommentsAdapter.onUserIDClicked = {
                val selfUserId = singstrViewModel.user.value?.id
                if (selfUserId == it) {
                    findNavController().navigate(CoverReplyCommentsFragmentDirections.actionCoverReplyCommentsFragmentToProfileFragment())
                } else {
                    findNavController().navigate(CoverReplyCommentsFragmentDirections.toOtherUserProfileFragment(it))
                }
            }

            coverCommentsUserAdapter.onUserClickListener = {
                Timber.e("========= User Detilas is : " + it)
                userName = it.firstName + " " + it.lastName
                if (userName != "") {
                    checkUserName = "exist"
                } else {
                }
                commentInput.setText(beforeUserName + userName)
                userIdList.add(it.id)

                Timber.e("========== User id List is : " + userIdList)
                rvSearch.visibility = GONE
            }

        }
    }

    private fun showUserName(searchData: SearchData) {
        if (searchData.user.isNotEmpty()) {
            Timber.e("=======   searchData:" + searchData.user)

            binding.apply {
                rvSearch.visibility = VISIBLE
            }
            coverCommentsUserAdapter.users = searchData.user

        }
    }


    private fun showComments(commentData: AllReplyComment) {
        binding.apply {
            ivProfilePicture.loadCenterCropImageFromUrl(commentData.user.profileUrl)
            singleTextView(
                binding.tvUserComment, commentData.user.userHandle,
                commentData.comment, commentData.user.id
            )
            tvTimestamp.text = SimpleDateFormat("dd MMM", Locale.ROOT).format(
                Date(TimeUnit.SECONDS.toMillis(commentData.timestamp))
            )
        }

        if (commentData.replyList.isEmpty()) {
            Toast.makeText(context, "Be the First to Reply", Toast.LENGTH_SHORT).show()
        } else {
            coverReplyCommentsAdapter.comments = commentData.replyList
            binding.tvTotalReply.text = prettyViewsCount(commentData.totalReply.toLong())
        }
    }

    private fun addCommentResult(isSuccessful: Boolean) {
        if (isSuccessful) viewModel.loadCoverComments(args.coverId)
    }

    private fun showCommentReplyResult(msg: String) {
        if (msg.equals("Reply added")) viewModel.loadFeedCommentReply(args.commentId)
    }

    private fun singleTextView(textView: TextView, name: String, comment: String, id: String) {
        val spanText = SpannableStringBuilder()
        spanText.bold {
            append("@$name")
        }
//        spanText.setSpan(AbsoluteSizeSpan(43),0,spanText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spanText.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
//                onUserIDClicked(id)
            }

            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.isUnderlineText = false // this remove the underline
            }
        }, spanText.length - name.length, spanText.length, 0)
        spanText.append(" $comment")

        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setText(spanText, TextView.BufferType.SPANNABLE)
    }


}