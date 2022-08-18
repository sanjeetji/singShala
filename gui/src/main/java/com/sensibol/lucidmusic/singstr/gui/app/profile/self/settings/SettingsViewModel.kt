package com.sensibol.lucidmusic.singstr.gui.app.profile.self.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.DailyChallenge
import com.sensibol.lucidmusic.singstr.domain.model.DeleteUser
import com.sensibol.lucidmusic.singstr.usecase.DeleteCacheUseCase
import com.sensibol.lucidmusic.singstr.usecase.DeleteUserProfileUseCase
import com.sensibol.lucidmusic.singstr.usecase.GetCacheSizeBytesUseCase
import com.sensibol.lucidmusic.singstr.usecase.ReportBugUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    private val getCacheSizeBytesUseCase: GetCacheSizeBytesUseCase,
    private val reportBugUseCase: ReportBugUseCase,
    private val deleteCacheUseCase: DeleteCacheUseCase,
    private val deleteUserProfileUseCase: DeleteUserProfileUseCase
) : BaseViewModel() {

    private val _cacheSizeBytes: MutableLiveData<Long> by lazy { MutableLiveData() }
    internal val cacheSizeBytes: LiveData<Long> = _cacheSizeBytes

    internal fun getCacheSizeBytes() {
        launchUseCases {
            _cacheSizeBytes.postValue(getCacheSizeBytesUseCase())
        }
    }

    internal fun deleteCache() {
        launchUseCases {
            deleteCacheUseCase()
            _cacheSizeBytes.postValue(getCacheSizeBytesUseCase())
        }
    }

    private val _reportBug: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    internal val reportBug: LiveData<Boolean> = _reportBug

    internal fun reportBug(message: String) {
        launchUseCases {
            _reportBug.postValue(reportBugUseCase(message))
        }
    }

    private val _deleteUserProfile: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    internal val deleteUserProfile: LiveData<String> = _deleteUserProfile

    internal fun loadDeleteUser() {
        launchUseCases {
            _deleteUserProfile.postValue(deleteUserProfileUseCase())
        }
    }
}