package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.TeacherDetails
import com.sensibol.lucidmusic.singstr.domain.webservice.FeedWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.LearnWebService
import javax.inject.Inject

class GetTeacherUseCase @Inject constructor(
    private val learnWebService: LearnWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(teacherId: String?): TeacherDetails {
        val authToken = appDatabase.getAuthToken()
        return learnWebService.getTeacherDetails(authToken, teacherId)
//        return TeacherDetails("ANVHXamd", "Christie Mills", 46, "https://akm-img-a-in.tosshub.com/indiatoday/images/story/202102/google_pay__7__1200x768.jpeg?WJeXdcrm_vaY0K7AWpMp5bXZ65NH_4dg&size=770:433", "10 yrs experience", 215, "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.",
//            listOf(
//                TeacherDetails.Attributes(
//                    listOf(
//                        TeacherDetails.Attributes.Details("Professor Of Music", "Cal Poly Pomona, Pomona, CA (2004 - Present)"),
//                        TeacherDetails.Attributes.Details("BA, Music/Popular Music", "University of Liverpool, CA (2002-2004)")), "Experience", 0),
//                TeacherDetails.Attributes(
//                    listOf(
//                        TeacherDetails.Attributes.Details("Professor of Music", "Cali Poly Ponmona, CA (2021 - Present)"),
//                        TeacherDetails.Attributes.Details("BA, Music/Popular Music", "University of Liverpool, CA (2002-2004)")), "Qualification", 0)
//            )
//        )
    }
}