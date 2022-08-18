package com.sensibol.lucidmusic.singstr.domain.model


data class AllReplyComment(
    val id: String,
    val attemptId: String,
    val comment: String,
    val timestamp: Long,
    val user: UserMiniData,
    val time:String,
    val totalReply:Int,
    val replyList:List<ReplyCommentData>
){
    data class UserMiniData(
        val id: String,
        val first_name: String,
        val last_name: String,
        val profileUrl: String,
        val displayName: String,
        val userHandle: String,
    )
    data class ReplyCommentData(
        val id: String,
        val attemptId: String,
        val commentId: String,
        val reply: String,
        val time: String,
        val timestamp: Long,
        val user:UserMini
    ){
        data class UserMini(
            val id: String,
            val first_name: String,
            val last_name: String,
            val profile_url: String,
            val display_name: String,
            val user_handle: String
        )
    }
}
