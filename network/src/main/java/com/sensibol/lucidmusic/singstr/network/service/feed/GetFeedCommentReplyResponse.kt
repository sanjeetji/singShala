package com.sensibol.lucidmusic.singstr.network.service.feed

import com.sensibol.lucidmusic.singstr.domain.model.AllReplyComment
import com.sensibol.lucidmusic.singstr.domain.model.Comment
import com.sensibol.lucidmusic.singstr.domain.model.ReplyComment
import com.sensibol.lucidmusic.singstr.domain.model.UserMini
import com.sensibol.lucidmusic.singstr.network.data.response.UserMiniResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


internal fun GetFeedCommentReplyResponse.toReplyComment() = ReplyComment(
    status = status,
    message = message,
    data = data.map { it.toAllReplyComment() }
)

internal fun GetFeedCommentReplyResponse.ReplyCommnet.toAllReplyComment() = AllReplyComment(
    id = id,
    attemptId = attemptId,
    comment = comment,
    timestamp = timestamp,
    user = user.toUserMiniData(),
    time = time,
    totalReply = totalCommentReply,
    replyList = replyData.map { it.toReplyCommentData() }
)

internal fun GetFeedCommentReplyResponse.ReplyCommnet.UserMiniData.toUserMiniData() = AllReplyComment.UserMiniData(
    id = id,
    first_name = first_name,
    last_name = last_name,
    profileUrl = profile_url,
    displayName = display_name,
    userHandle = user_handle
)

internal fun GetFeedCommentReplyResponse.ReplyCommnet.ReplyCommentData.toReplyCommentData() = AllReplyComment.ReplyCommentData(
    id = _id,
    attemptId = attempt_id,
    commentId = comment_id,
    reply = reply,
    time = time,
    timestamp = timestamp,
    user = user.toUserMini()
)

internal fun GetFeedCommentReplyResponse.ReplyCommnet.ReplyCommentData.UserMini.toUserMini() = AllReplyComment.ReplyCommentData.UserMini(
    id = id,
    first_name = first_name,
    last_name = last_name,
    profile_url = profile_url,
    display_name = display_name,
    user_handle = user_handle

)

@JsonClass(generateAdapter = true)
data class GetFeedCommentReplyResponse(
    @Json(name = "status")
    val status:Int,
    @Json(name = "message")
    val message:String,
    @Json(name = "data")
    val data:List<ReplyCommnet>
) {
    @JsonClass(generateAdapter = true)
    data class ReplyCommnet(
        @Json(name = "_id")
        val id: String,
        @Json(name = "attempt_id")
        val attemptId: String,
        @Json(name = "comment")
        val comment: String,
        @Json(name = "timestamp")
        val timestamp: Long,
        @Json(name = "user")
        val user: UserMiniData,
        @Json(name = "time")
        val time: String,
        @Json(name = "TotalReply")
        val totalCommentReply:Int,
        @Json(name = "replyData")
        val replyData:List<ReplyCommentData>
        )
    {
        @JsonClass(generateAdapter = true)
        data class UserMiniData(
            @Json(name = "id")
            val id: String,

            @Json(name = "first_name")
            val first_name: String,

            @Json(name = "last_name")
            val last_name: String,

            @Json(name = "profile_url")
            val profile_url: String,

            @Json(name = "display_name")
            val display_name: String,

            @Json(name = "user_handle")
            val user_handle: String,
        )
        @JsonClass(generateAdapter = true)
        data class ReplyCommentData(
            @Json(name = "_id")
            val _id: String,

            @Json(name = "attempt_id")
            val attempt_id: String,

            @Json(name = "comment_id")
            val comment_id: String,

            @Json(name = "reply")
            val reply: String,

            @Json(name = "time")
            val time: String,

            @Json(name = "timestamp")
            val timestamp: Long,

            @Json(name = "user")
            val user: UserMini,
        ){
            @JsonClass(generateAdapter = true)
            data class UserMini(
                @Json(name = "id")
                val id: String,

                @Json(name = "first_name")
                val first_name: String,

                @Json(name = "last_name")
                val last_name: String,

                @Json(name = "profile_url")
                val profile_url: String,

                @Json(name = "display_name")
                val display_name: String,

                @Json(name = "user_handle")
                val user_handle:String
            )
        }
    }
}
