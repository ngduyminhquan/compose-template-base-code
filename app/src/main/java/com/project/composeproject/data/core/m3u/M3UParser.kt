package com.project.composeproject.data.core.m3u

object M3UParser {

    private val tvgIdRegex = Regex("tvg-id=\"([^\"]+)\"")
    private val logoRegex = Regex("tvg-logo=\"([^\"]+)\"")
    private val groupTitleRegex = Regex("group-title=\"([^\"]+)\"")
    private val resolutionRegex = Regex("(?:tvg-res|resolution)=\"([^\"]+)\"")
    private val nameResolutionRegex = Regex("(?i)\\b(4K|UHD|1080[pi]|720[pi]|SD|FHD|HD)\\b")

    fun parse(content: String): List<M3uChannel> {
        val channels = mutableListOf<M3uChannel>()

        var currentName: String? = null
        var currentGroupTitle: String? = null
        var currentLogo: String? = null
        var currentTvgId: String? = null
        var currentResolution: String? = null

        fun resetCurrentChannel() {
            currentName = null
            currentGroupTitle = null
            currentLogo = null
            currentTvgId = null
            currentResolution = null
        }

        for (line in content.lineSequence()) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) {
                continue
            }

            if (trimmed.startsWith("#EXTINF:")) {
                resetCurrentChannel()

                val namePart = trimmed.substringAfterLast(",", "").trim()
                currentName = namePart.ifEmpty { null }

                val attributesPart = trimmed.substringAfter("#EXTINF:").substringBeforeLast(",")

                currentTvgId = tvgIdRegex.find(attributesPart)?.groupValues?.get(1)
                currentLogo = logoRegex.find(attributesPart)?.groupValues?.get(1)
                currentGroupTitle = groupTitleRegex.find(attributesPart)
                    ?.groupValues
                    ?.get(1)
                    ?.substringBefore(';')
                    ?.trim()
                    ?.ifEmpty { null }

                if (currentName.isNullOrEmpty()) {
                    currentName = trimmed.substringAfterLast(",", "").trim().ifEmpty { null }
                }

                currentResolution = resolutionRegex.find(attributesPart)?.groupValues?.get(1)

                if (currentResolution == null) {
                    currentResolution = currentName
                        ?.let(nameResolutionRegex::find)
                        ?.value
                }
                continue
            }

            if (trimmed.startsWith("#") || currentName.isNullOrEmpty()) {
                continue
            }

            channels.add(
                M3uChannel(
                    name = currentName ?: "",
                    url = trimmed,
                    groupTitle = currentGroupTitle.orEmpty(),
                    logo = currentLogo.orEmpty(),
                    tvgId = currentTvgId.orEmpty(),
                    resolution = currentResolution.orEmpty()
                )
            )
            resetCurrentChannel()
        }

        return channels
    }
}