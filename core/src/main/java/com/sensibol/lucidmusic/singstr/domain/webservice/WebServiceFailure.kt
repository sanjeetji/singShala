package com.sensibol.lucidmusic.singstr.domain

import com.sensibol.android.base.domain.exception.Failure.DataFailure

class WebServiceFailure {
    class NoNetworkFailure(
        msg: String = "Network not available!"
    ) : DataFailure(msg)

    class AuthenticationFailure(
        msg: String = "Authentication error!"
    ) : DataFailure(msg)

    class NetworkTimeOutFailure(
        msg: String = "Network timeout!"
    ) : DataFailure(msg)

    class NetworkDataFailure(
        msg: String = "Error parsing data!"
    ) : DataFailure(msg)

    class UnknownNetworkFailure(
        msg: String = "Unknown network error!"
    ) : DataFailure(msg)
}