package com.akhil.demotask.service.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.akhil.demotask.service.repository.FileUploadRepository

class FileUploadModelFactory(
    private val repository: FileUploadRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FileUploadViewModel(repository) as T
    }
}