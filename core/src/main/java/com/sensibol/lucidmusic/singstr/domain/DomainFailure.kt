package com.sensibol.lucidmusic.singstr.domain

import com.sensibol.android.base.domain.exception.Failure

class DomainFailure {

    class MetadataFileError(msg: String = "Error in metadata file") : Failure.DataFailure(msg)
    class MediaFileError(msg: String = "Error in media file") : Failure.DataFailure(msg)
}