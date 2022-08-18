package com.sensibol.lucidmusic.singstr.gui.app.feed.comments

import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.bold
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.domain.model.AllReplyComment
import com.sensibol.lucidmusic.singstr.domain.model.Comment
import com.sensibol.lucidmusic.singstr.gui.databinding.TileFeedCommentFullviewBinding
import com.sensibol.lucidmusic.singstr.gui.loadCenterCropImageFromUrl
import dagger.hilt.android.scopes.FragmentScoped
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.properties.Delegates

@FragmentScoped
internal class CoverReplyCommentsAdapter @Inject constructor() : RecyclerView.Adapter<CoverReplyCommentsAdapter.FeedCommentVH>() {

    internal var onUserIDClicked: (userId: String) -> Unit = { _ -> }

    internal var comments: List<AllReplyComment.ReplyCommentData> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    inner class FeedCommentVH(val binding: TileFeedCommentFullviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: AllReplyComment.ReplyCommentData) {
            binding.tvReply.visibility = GONE
            binding.tvTimestamp.text = SimpleDateFormat("dd MMM", Locale.ROOT).format(Date(TimeUnit.SECONDS.toMillis(comment.timestamp)))
            binding.ivProfilePicture.loadCenterCropImageFromUrl(comment.user.profile_url)
            singleTextView(binding.tvUserComment, comment.user.user_handle, comment.reply,comment.user.id)//here need to change displayName to userHandle
        }
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedCommentVH =
        FeedCommentVH(TileFeedCommentFullviewBinding.inflate(parent.layoutInflater, parent, false))


    override fun onBindViewHolder(holder: FeedCommentVH, position: Int) {
        holder.bind(comments[position])
    }

    private fun singleTextView(textView: TextView, name: String, comment: String, id: String) {
        val spanText = SpannableStringBuilder()
        spanText.bold {
            append("@$name")
        }
//        spanText.setSpan(AbsoluteSizeSpan(43),0,spanText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spanText.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                onUserIDClicked(id)
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