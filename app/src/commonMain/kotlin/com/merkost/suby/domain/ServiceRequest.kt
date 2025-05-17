package com.merkost.suby.domain

data class ServiceRequest(
    val serviceName: String,
    val website: String = "",
    val description: String = ""
)