package com.sensibol.lucidmusic.singstr.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

data class Singup(
    val status: Int,
    val message: String,
    val data: UserDetail
) {
    data class UserDetail(
        val id: String,
        val loginType: Int,
        val socialId: String,
        val email: String,
        val password: String,
        val firstName: String,
        val lastName: String,
        val contactNumber:String,
        val profileImg: String,
        val status: Int,
        val userHandle: String,
        val displayName: String,
        val sex:Int,
        val devices:List<device>
    ){
        data class device(
            val accessToken: String
        )
    }
}


