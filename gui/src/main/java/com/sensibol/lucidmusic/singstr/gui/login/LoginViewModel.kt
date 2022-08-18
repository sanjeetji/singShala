package com.sensibol.lucidmusic.singstr.gui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.displayName
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.AuthToken
import com.sensibol.lucidmusic.singstr.domain.model.CheckUserExists
import com.sensibol.lucidmusic.singstr.domain.model.Singup
import com.sensibol.lucidmusic.singstr.domain.model.UserHandle
import com.sensibol.lucidmusic.singstr.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val syncAccessTokenUseCase: SyncAccessTokenUseCase,
    private val signupUserUseCase: SignupUserUseCase,
    private val checkUserExistUseCase: CheckUserExistUseCase,
    private val saveTokenUseCase: SaveTokenUseCase,
    private val userHandleUseCase: UserHandleUseCase,
    private val generateAccessPhoneTokenUseCase: GenerateAccessPhoneTokenUseCase,
    private val setUserOnBoardedUseCase: SetUserOnBoardedUseCase
) : BaseViewModel() {

    lateinit var  firstName: String
    var  lastName: String = ""
    lateinit var  phoneNumber: String
    lateinit var  userHandleValue: String
    lateinit var  gender:String
    lateinit var dob:String
    lateinit var singerType: String


    // TODO - explore SingleLiveData
    private val _isUserLoginSuccessful: MutableLiveData<Boolean> by lazy { MutableLiveData() }
    internal val isUserLoginSuccessful: LiveData<Boolean> get() = _isUserLoginSuccessful

    internal fun updateAuthTokenFromServer(provider: String, token: String) {
        launchUseCases {
            syncAccessTokenUseCase(provider, token)
            _isUserLoginSuccessful.postValue(true)
        }
    }

    private val _singupUser: MutableLiveData<Singup> by lazy { MutableLiveData() }
    internal val singupUser: LiveData<Singup> get() = _singupUser

    internal fun performSignUp(loginType: Int, socialId: String) {
        launchUseCases {

            var displayName = firstName
            if(lastName.isNotBlank()){
                displayName = "$firstName $lastName"
            }
            _singupUser.postValue(signupUserUseCase(loginType, socialId, firstName, lastName, userHandleValue
                , gender, phoneNumber, "", dob, displayName, singerType))
        }
    }

    private val _checkUserExisten: MutableLiveData<CheckUserExists> by lazy { MutableLiveData() }
    internal val checkUserExisten: LiveData<CheckUserExists> get() = _checkUserExisten

    internal fun checkUserExisten(checkValue: String) {
        launchUseCases {
            _checkUserExisten.postValue(checkUserExistUseCase(checkValue))
        }
    }


    internal fun saveAuthToken(token: AuthToken) {
        launchUseCases {
            saveTokenUseCase(token)
        }
    }

    private val _phoneToken: MutableLiveData<AuthToken> by lazy { MutableLiveData() }
    internal val phoneToken: LiveData<AuthToken> get() = _phoneToken


    internal fun generateAccessPhoneToken(phoneNumber: String) {
        launchUseCases {
            _phoneToken.postValue(generateAccessPhoneTokenUseCase("Sensibol","MOBILE",phoneNumber,))
        }
    }

    private val _userHandle: MutableLiveData<UserHandle> by lazy { MutableLiveData() }
    internal val userHandle: LiveData<UserHandle> get() = _userHandle

    internal fun userHandle(firstName: String,lastName:String) {
        launchUseCases {
            _userHandle.postValue(userHandleUseCase(firstName, lastName))
        }
    }

    internal fun setUserOnBoarded(isUserOnBoarded: Boolean) {
        Timber.v("setUserOnBoarded: IN")
        launchUseCases {
            setUserOnBoardedUseCase(isUserOnBoarded)
        }
        Timber.v("setUserOnBoarded: OUT")
    }

}