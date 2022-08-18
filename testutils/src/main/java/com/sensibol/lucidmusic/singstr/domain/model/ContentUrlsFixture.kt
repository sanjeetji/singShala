package com.sensibol.lucidmusic.singstr.domain.model

import com.sensibol.lucidmusic.singstr.domain.utils.UniqueFixture

object ContentUrlsFixture {

    fun newContentUrls(
        mediaUrl: String = UniqueFixture.newUrl("media"),
        mediaHash: String = UniqueFixture.newStringId(),
        metadataUrl: String = UniqueFixture.newUrl("media"),
        metadataHash: String = UniqueFixture.newStringId(),
    ): ContentUrls = ContentUrls(
        mediaUrl = mediaUrl,
        mediaHash = mediaHash,
        metadataUrl = metadataUrl,
        metadataHash = metadataHash,
    )
}