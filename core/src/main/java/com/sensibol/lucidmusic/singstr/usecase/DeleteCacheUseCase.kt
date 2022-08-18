package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.AppFileSystem
import javax.inject.Inject

class DeleteCacheUseCase @Inject constructor(
    private val fileSystem: AppFileSystem
) {
    suspend operator fun invoke() = fileSystem.deleteCache()
}