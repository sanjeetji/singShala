package com.sensibol.lucidmusic.singstr.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class UserMini(
    open val id: String,
    open val handle: String,
    open val dpUrl: String,
    open var displayName: String = "NA"
) : Parcelable
