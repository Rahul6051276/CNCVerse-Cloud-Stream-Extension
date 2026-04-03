package com.Bollyflix

import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.utils.ExtractorApi
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.Qualities

class VikingFile : ExtractorApi() {
    override val name: String = "ViKiNG FiLE"
    override val mainUrl: String = "https://vikingfile.com"
    override val requiresReferer: Boolean = false

    override suspend fun getUrl(url: String, referer: String?): List<ExtractorLink>? {
        val response = app.get(url).document
        // वीडियो-जेएस प्लेयर से असली .mkv/.mp4 लिंक निकालना
        val videoSrc = response.selectFirst("video.vjs-tech")?.attr("src") 
            ?: response.selectFirst("source")?.attr("src")

        return if (!videoSrc.isNullOrEmpty()) {
            listOf(
                ExtractorLink(
                    source = name,
                    name = name,
                    url = videoSrc,
                    referer = url,
                    quality = Qualities.P720.value,
                    isM3u8 = videoSrc.contains(".m3u8")
                ) 
            )
        } else null
    }
}
