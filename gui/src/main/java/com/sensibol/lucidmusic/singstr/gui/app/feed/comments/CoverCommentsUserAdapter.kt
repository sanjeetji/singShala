package com.sensibol.lucidmusic.singstr.gui.app.feed.comments

import android.service.autofill.UserData
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.bold
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.domain.model.Comment
import com.sensibol.lucidmusic.singstr.domain.model.SearchData
import com.sensibol.lucidmusic.singstr.gui.app.search.AdvanceSearchAdapter
import com.sensibol.lucidmusic.singstr.gui.app.search.SearchNode
import com.sensibol.lucidmusic.singstr.gui.databinding.TileFeedCommentFullviewBinding
import com.sensibol.lucidmusic.singstr.gui.databinding.TileSearchUserBinding
import com.sensibol.lucidmusic.singstr.gui.loadCenterCropImageFromUrl
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import dagger.hilt.android.scopes.FragmentScoped
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.properties.Delegates
import android.widget.LinearLayout
import com.sensibol.lucidmusic.singstr.gui.databinding.TileCommentSearchUserBinding


@FragmentScoped
internal class CoverCommentsUserAdapter @Inject constructor() : RecyclerView.Adapter<CoverCommentsUserAdapter.SearchUserVH>() {

    var onUserClickListener: (SearchData.User) -> Unit = {}

    internal var users: List<SearchData.User> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    inner class SearchUserVH(val binding: TileCommentSearchUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: SearchData.User) {
            binding.tvUserName.text = user.firstName
            binding.ivProfilePic.loadUrl(user.profileImg)
            binding.tvUserName.setOnClickListener {
                onUserClickListener(user)
            }
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoverCommentsUserAdapter.SearchUserVH =
        SearchUserVH(TileCommentSearchUserBinding.inflate(parent.layoutInflater, parent, false))


    override fun onBindViewHolder(holder: CoverCommentsUserAdapter.SearchUserVH, position: Int) {
        holder.bind(users[position])
    }



}