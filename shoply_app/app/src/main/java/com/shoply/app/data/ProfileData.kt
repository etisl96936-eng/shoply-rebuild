package com.shoply.app.data

data class ProfileData(
    val displayName: String = "",
    val email: String = "",
    val preferredStores: List<String> = emptyList()
)