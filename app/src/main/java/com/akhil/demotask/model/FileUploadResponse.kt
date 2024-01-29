package com.akhil.demotask.model

data class FileUploadResponse(
    val file_name: String,
    val file_path: String,
    val message: String,
    val success: Boolean
)