package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.AppFileSystem
import javax.inject.Inject

class GetMixFilePathUseCase @Inject constructor(
    private val appFileSystem: AppFileSystem
) {
    suspend operator fun invoke() = appFileSystem.getUniqueMixFile()
}