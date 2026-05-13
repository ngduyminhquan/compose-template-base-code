package com.project.composeproject.data.core.m3u

data class M3uChannel(
    val url: String,
    val name: String,
    val groupTitle: String,
    val logo: String,
    val tvgId: String,
    val resolution: String = ""
)