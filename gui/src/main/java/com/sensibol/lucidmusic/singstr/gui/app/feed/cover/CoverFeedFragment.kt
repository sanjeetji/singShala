package com.sensibol.lucidmusic.singstr.gui.app.feed.cover

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.sensibol.lucidmusic.singstr.gui.app.feed.FeedFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class CoverFeedFragment : FeedFragment() {

    private val args: CoverFeedFragmentArgs by navArgs()

    override val feedSlotsVM: CoverFeedSlotsViewModel by viewModels()

    override val feedTypeId: String get() = args.coverId

}