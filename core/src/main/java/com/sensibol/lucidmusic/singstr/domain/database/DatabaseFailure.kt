package com.sensibol.lucidmusic.singstr.domain

import com.sensibol.android.base.domain.exception.Failure.DataFailure

class DatabaseFailure {
    class UserNotLoggedIn(
        msg: String = "User not logged in!"
    ) : DataFailure(msg)
}