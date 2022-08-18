package com.sensibol.lucidmusic.singstr.gui.app.learn.mylist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.lucidmusic.singstr.domain.model.MyLessonMini
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.TileMyListBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.properties.Delegates

class MyListAdapter @Inject constructor() :
    RecyclerView.Adapter<MyListAdapter.ImproveTuneVH>() {

    var onTuneClickListener: (MyLessonMini) -> Unit = {}
    var onRemoveClickListener: (MyLessonMini) -> Unit = {}

    var collections: List<MyLessonMini> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }


    inner class ImproveTuneVH(private val binding: TileMyListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(myLesson: MyLessonMini) {
            binding.apply {
                tvMyListTitle.text = myLesson.title
                tvTuningLabel.text = myLesson.difficulty
                try {
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                    val past =sdf.parse(myLesson.addedTime)
                    sdf.timeZone = TimeZone.getDefault();
                    val now = Date()

                    val seconds :Long = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time)
                    val minutes :Long = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time)
                    val hours :Long = TimeUnit.MILLISECONDS.toHours(now.time - past.time)
                    val days :Long = TimeUnit.MILLISECONDS.toDays(now.time - past.time)

                    Timber.d(myLesson.addedTime+"time added the list\n seconds:"+seconds+"\n minutes: "+minutes+"\n hours: "+hours+"\n days : "+days)
                    if (seconds < 60) {
                        tvItemAddedAt.text = "Added ${seconds} seconds ago."
                    } else if (minutes < 60) {
                        tvItemAddedAt.text = "Added ${minutes} minutes ago."
                    } else if (hours < 24) {
                        tvItemAddedAt.text = "Added ${hours} hours ago."
                    } else {
                        tvItemAddedAt.text = "Added ${days} days ago."
                    }
                } catch (e: Exception) {
                }


                tvTuningLabel.text = myLesson.type
                ivThumbnail.loadUrl(myLesson.thumbnailUrl)

                ivOverFlow.setOnClickListener {

                    val wrapper: Context = ContextThemeWrapper(binding.root.context, R.style.popUpMenuStyle)
                    val popupMenu = PopupMenu(wrapper, it)
                    popupMenu.menuInflater.inflate(R.menu.mylist_menu, popupMenu.menu)
                    popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                        when (item.itemId) {
                        R.id.remove ->
                        {
                            onRemoveClickListener(collections[adapterPosition])
                        }

                       R.id.share ->
                       {

                       }

                        }
                        true
                    })
                    popupMenu.show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ImproveTuneVH(TileMyListBinding.inflate(LayoutInflater.from(parent.context), parent, false)).apply {
            itemView.setOnClickListener {
                onTuneClickListener(collections[adapterPosition])
            }
        }


    override fun getItemCount() = collections.size

    override fun onBindViewHolder(holder: ImproveTuneVH, position: Int) {
        holder.bind(collections[position])
    }
}