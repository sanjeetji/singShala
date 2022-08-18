package com.sensibol.lucidmusic.singstr.domain.model

data class SubscriptionPlan(
    val id: String,
    val name: String,
    val displayName: String,
    val type: String,
    val order: Int,
    val discountPrice: Int,
    val originalPrice: Int,
    val store: String,
    val currency: String,
    val img: String,
    val desc: String,
    val freeTrialDesc: String,
    val unlockInfo: List<UnlockInfo>
) {
    data class UnlockInfo(
        val type: String,
        val credit: Int,
        val contest_id: String,
        val audition_id: String,
        val numberOfReview: Int,
        val validity: Int,
        val expiry: String
    )
}
