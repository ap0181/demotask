package com.akhil.demotask.service.viewmodel

import androidx.lifecycle.ViewModel
import com.akhil.demotask.model.request.FileUploadRequest
import com.akhil.demotask.service.repository.FileUploadRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FileUploadViewModel(
    private val repository: FileUploadRepository
) : ViewModel() {
    suspend fun UploadFile(
        request: FileUploadRequest
    ) = withContext(Dispatchers.IO) { repository.UploadFile(request) }
}