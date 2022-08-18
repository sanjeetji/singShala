package com.sensibol.lucidmusic.singstr.gui.app.feed.generic

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.sensibol.lucidmusic.singstr.gui.app.feed.FeedFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class GenericFeedFragment : FeedFragment() {

    private val args: GenericFeedFragmentArgs by navArgs()

    override val feedSlotsVM: GenericFeedSlotsViewModel by activityViewModels()

    override val feedTypeId: String get() = "ignored"

    override fun onDestroy() {
        feedSlotsVM.reset()
        super.onDestroy()
    }
}