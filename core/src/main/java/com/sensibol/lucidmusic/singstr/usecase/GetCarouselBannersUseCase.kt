package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.CarouselBanner
import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import javax.inject.Inject

class GetCarouselBannersUseCase @Inject constructor(
    private val appDatabase: AppDatabase,
    private val userWebService: UserWebService
) {
    suspend operator fun invoke(): List<CarouselBanner> = userWebService.fetchCorouselList(appDatabase.getAuthToken())
}