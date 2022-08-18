package com.sensibol.lucidmusic.singstr.domain.model

object UserFixture {

    fun newUser(
        id: String = "",
        name: String = "Thomas",
        handle: String = "neo",
        mobileNumber: String = "9876543210",
        email: String = "neo@matix.net",
        isVerified: Boolean = true,
        dpUrl: String = "https://www.matix.com/dp/neo.jpg",
        status: String = "There is no spoon!"
    ): User = User(
        id = id,
        name = name,
        handle = handle,
        mobileNumber = mobileNumber,
        email = email,
        isVerified = isVerified,
        dpUrl = dpUrl,
        status = status,
        // FIXME - dob format
        dob = "Sept 13, 1971",
        city = "Lower Downtown",
        sex = "Male",
        state = "Washington",
        isOnBoarded = true
    )
}