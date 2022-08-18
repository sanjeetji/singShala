package com.sensibol.lucidmusic.singstr.gui.app.feed

import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.bold
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.domain.model.Comment
import com.sensibol.lucidmusic.singstr.gui.checkUserHandle
import com.sensibol.lucidmusic.singstr.gui.databinding.TileUserCommentBinding
import javax.inject.Inject
import kotlin.properties.Delegates

internal class FeedBottomSheetCommentAdapter @Inject constructor() : RecyclerView.Adapter<FeedBottomSheetCommentAdapter.FeedBottomSheetCommentVH>() {

    internal var onCommentClickListener: () -> Unit = {}
    internal var onUserIDClicked: (String) -> Unit = { _ -> }

    internal var comments: List<Comment> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedBottomSheetCommentVH = FeedBottomSheetCommentVH(
        TileUserCommentBinding.inflate(parent.layoutInflater, parent, false)
    )

    override fun onBindViewHolder(holder: FeedBottomSheetCommentVH, position: Int) = holder.bind(comments[position]).apply {
        holder.binding.tvCaption.setOnClickListener {
            onCommentClickListener()
        }
    }

    override fun getItemCount(): Int = comments.size

    inner class FeedBottomSheetCommentVH(val binding: TileUserCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {

            binding.apply {
                singleTextView(tvUserName, comment.userMini.handle, comment.comment, comment.userMini.id)
            }
        }
    }

    private fun singleTextView(textView: TextView, name: String, comment: String, id: String) {
        val spanText = SpannableStringBuilder()
        spanText.bold {
            append("${name.checkUserHandle()}")
        }
//        spanText.setSpan(AbsoluteSizeSpan(40),0,spanText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
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