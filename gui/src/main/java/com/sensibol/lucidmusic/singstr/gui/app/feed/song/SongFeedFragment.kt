package com.sensibol.lucidmusic.singstr.gui.app.feed.song

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.sensibol.lucidmusic.singstr.gui.app.feed.FeedFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class SongFeedFragment : FeedFragment() {

    private val args: SongFeedFragmentArgs by navArgs()

    override val feedSlotsVM: SongFeedSlotsViewModel by viewModels()

    override val feedTypeId: String get() = args.songId

}