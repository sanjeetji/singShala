package com.sensibol.lucidmusic.singstr.gui.app.feed.comments

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextWatcher
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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.Comment
import com.sensibol.lucidmusic.singstr.domain.model.SearchData
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentCoverCommentsBinding
import com.sensibol.lucidmusic.singstr.gui.loadCenterCropImageFromUrl
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
internal class CoverCommentsFragment : BaseFragment() {
    override val layoutResId: Int get() = R.layout.fragment_cover_comments
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentCoverCommentsBinding::inflate
    override val binding get():FragmentCoverCommentsBinding = super.binding as FragmentCoverCommentsBinding

    private val viewModel: CoverCommentsViewModel by viewModels()

    private val singstrViewModel: SingstrViewModel by activityViewModels()

    val args: CoverCommentsFragmentArgs by navArgs()

    var nameChars: String = ""
    var userName:String = ""
    var commentText:String = ""
    var beforeUserName:String = ""
    var checkUserName:String = ""
    var userIdList:MutableList<String> = mutableListOf()

    @Inject
    internal lateinit var coverCommentsAdapter: CoverCommentsAdapter

    @Inject
    internal lateinit var coverCommentsUserAdapter: CoverCommentsUserAdapter

    override fun onInitView() {
        viewModel.apply {
            observe(comments, ::showComments)
//            observe(addComment, ::addCommentResult)
            observe(addCommentOnCover, ::addCoverCommentResult)
            observe(addCommentReply,::showMsg)
            observe(getUserNames, ::showUserName)
            loadCoverComments(args.coverId)
        }
        binding.apply {
            rvCommentList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = coverCommentsAdapter
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

                    if (checkUserName.equals("exist")){
                        Timber.e("========= Else Codition")
                        checkUserName = ""
                    }else{
                        if (s?.contains("@") == true){
                            nameChars = commentInput.text.toString().substringAfterLast("@")
                            viewModel.loadUserName(nameChars,"user");
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


                viewModel.loadAddFeedCommentOnCover(commentInput.text.toString(), args.coverId,userIdList)

//                viewModel.loadAddFeedComments(args.coverId, commentInput.text.toString())
//                Timber.d("Commenting visible:" + textInputLayout.editText.toString())
//                textInputLayout.editText?.onEditorAction(EditorInfo.IME_ACTION_DONE)
                commentInput.text?.clear()
                commentBoxVi.visibility = GONE
            }
            ivBackFeed.setOnClickListener {
                findNavController().popBackStack()
            }
            coverCommentsAdapter.onUserIDClicked = {
                val selfUserId = singstrViewModel.user.value?.id
                if (selfUserId == it) {
                    findNavController().navigate(CoverCommentsFragmentDirections.actionCoverCommentsFragmentToProfileFragment())
                } else {
                    findNavController().navigate(CoverCommentsFragmentDirections.toOtherUserProfileFragment(it))
                }
            }

            coverCommentsAdapter.onReplyClick = {
                findNavController().navigate(CoverCommentsFragmentDirections.toCoverReplyCommentsFragment(args.coverId,it.id))
            }

            coverCommentsUserAdapter.onUserClickListener = {
                Timber.e("========= User Detilas is : "+it)
                userName = it.firstName+" "+it.lastName
                if (userName != ""){
                    checkUserName = "exist"
                }else{
                }
                commentInput.setText(beforeUserName+userName)
                userIdList.add(it.id)

                Timber.e("========== User id List is : "+userIdList)
                rvSearch.visibility = GONE
            }
        }
    }

    private fun addCoverCommentResult(msg: String) {
        if (msg.equals("Comment added")) viewModel.loadCoverComments(args.coverId)
    }

    private fun showMsg(msg: String) {
        Toast.makeText(requireContext(),msg,Toast.LENGTH_SHORT).show()
    }

    private fun addCommentResult(isSuccessful: Boolean) {
        if (isSuccessful) viewModel.loadCoverComments(args.coverId)
    }

    private fun showComments(comments: List<Comment>) {
        if (comments.isEmpty()) {
            Toast.makeText(context, "Be the First to Comment", Toast.LENGTH_SHORT).show()
        } else {
            coverCommentsAdapter.comments = comments
        }
    }

    private fun showUserName(searchData: SearchData) {
        if (searchData.user.isNotEmpty()){
            Timber.e("=======   searchData:" + searchData.user)

            binding.apply {
                rvSearch.visibility = VISIBLE
            }
            coverCommentsUserAdapter.users = searchData.user

        }
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