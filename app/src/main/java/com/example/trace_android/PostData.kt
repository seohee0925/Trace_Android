package com.example.trace_android

import java.util.Date

data class PostData(
    var id: Long,
    var combinedImage: String,
    var content: String,
    var place: String,
    var date: Date
)
