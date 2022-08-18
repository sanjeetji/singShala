package com.sensibol.lucidmusic.singstr.gui.app.feed.user

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.sensibol.lucidmusic.singstr.gui.app.feed.FeedFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class UserFeedFragment : FeedFragment() {

    private val args: UserFeedFragmentArgs by navArgs()

    override val feedSlotsVM: UserFeedSlotsViewModel by viewModels()

    override val feedTypeId: String get() = args.userId
}