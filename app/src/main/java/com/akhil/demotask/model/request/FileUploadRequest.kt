package com.akhil.demotask.model.request

import com.google.gson.annotations.SerializedName

class FileUploadRequest {
    @field:SerializedName("file")
    var file: String? = null
}