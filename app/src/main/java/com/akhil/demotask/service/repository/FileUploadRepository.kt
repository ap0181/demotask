package com.akhil.demotask.service.repository

import com.akhil.demotask.model.FileUploadResponse
import com.akhil.demotask.model.request.FileUploadRequest
import com.akhil.demotask.service.APIService
import com.akhil.demotask.service.SafeApiRequest
import com.akhil.demotask.util.createPartFromImageUrl

class FileUploadRepository (private val api: APIService) : SafeApiRequest() {
    suspend fun UploadFile(request: FileUploadRequest): FileUploadResponse {
        return apiRequest {
            api.UploadFile(
                createPartFromImageUrl("file", request.file)
            )
        }
    }
}