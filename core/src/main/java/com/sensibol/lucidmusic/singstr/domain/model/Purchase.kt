package com.sensibol.lucidmusic.singstr.domain.model

data class Purchase(
    val id: String,
    val userId: String,
    val emailId: String,
    val firstName: String,
    val lastName: String,
    val productId: String,
    val productPrice: String,
    val productName: String,
    val productDesc: String,
    val orderId: String,
    val platform: String,
    val purchaseTime: String,
    val status: String,
    val mode: String,
    val initTime: String,
    val completeTime: String,
    val lastUpdate: String,
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
