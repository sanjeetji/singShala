package com.sensibol.lucidmusic.singstr.network.service.user


import com.sensibol.lucidmusic.singstr.domain.model.UserReviewAccount
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


internal fun GetUserReviewRemainingCount.UserReviewRemainingCountData.Account.toReviewAccount() = UserReviewAccount(
    reviews = reviews,
    lineReviews = lineReviews
)


@JsonClass(generateAdapter = true)
data class GetUserReviewRemainingCount(
    @Json(name = "data")
    val data: UserReviewRemainingCountData,
    @Json(name = "success")
    val success: Boolean
) {
    @JsonClass(generateAdapter = true)
    data class UserReviewRemainingCountData(
        @Json(name = "account")
        val account: Account
    ) {
        @JsonClass(generateAdapter = true)
        data class Account(
            @Json(name = "line_reviews")
            val lineReviews: Int,
            @Json(name = "reviews")
            val reviews: Int
        )
    }
}