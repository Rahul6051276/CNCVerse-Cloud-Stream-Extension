package com.Bollyfilx

import com.lagradost.cloudstream3.ErrorLoadingException
import com.lagradost.cloudstream3.HomePageList
import com.lagradost.cloudstream3.HomePageResponse
import com.lagradost.cloudstream3.LoadResponse
import com.lagradost.cloudstream3.LoadResponse.Companion.addActors
import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.MainPageRequest
import com.lagradost.cloudstream3.SearchResponse
import com.lagradost.cloudstream3.SearchResponseList
import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.TvType
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.fixUrl
import com.lagradost.cloudstream3.fixUrlNull
import com.lagradost.cloudstream3.getDurationFromString
import com.lagradost.cloudstream3.getQualityFromString
import com.lagradost.cloudstream3.mainPageOf
import com.lagradost.cloudstream3.newHomePageResponse
import com.lagradost.cloudstream3.newMovieLoadResponse
import com.lagradost.cloudstream3.newMovieSearchResponse
import com.lagradost.cloudstream3.toNewSearchResponseList
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.loadExtractor
import kotlinx.coroutines.runBlocking
import org.jsoup.nodes.Element

class Bollyflix : MainAPI() {
    // यहाँ हमने Bollyflix का नया वर्किंग डोमेन डाला है
    override var mainUrl: String = "https://bollyflix.to" 
    override var name = "Bollyflix"
    override val hasMainPage = true
    override var lang = "hi"
    override val hasDownloadSupport = true
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries, TvType.Anime)

    // आपके भेजे गए कोड के आधार पर कैटेगरी लिंक्स
    override val mainPage = mainPageOf(
        "" to "Latest Movies",
        "movies/bollywood/" to "Bollywood Movies",
        "movies/hollywood/" to "Hollywood Movies",
        "movies/south-hindi-dubbed/" to "South Hindi Dubbed",
        "movies/dual-audio-movies/" to "Dual Audio",
        "web-series/" to "Web Series",
        "anime/" to "Anime"
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val url = if (page <= 1) "$mainUrl/${request.data}" else "$mainUrl/${request.data}page/$page/"
        val document = app.get(url).document
        
        // Bollyflix में मूवी कार्ड 'article.latestPost' टैग के अंदर है
        val items = document.select("article.latestPost").mapNotNull { it.toSearchResult() }

        return newHomePageResponse(
            list = HomePageList(
                name = request.name,
                list = items,
                isHorizontalImages = false),
            hasNext = true
        )
    }

    private fun Element.toSearchResult(): SearchResponse? {
        // नाम और लिंक के लिए 'h2.title a' का इस्तेमाल
        val aTag = selectFirst("h2.title a") ?: return null
        val img = selectFirst("img") ?: return null

        val href = fixUrl(aTag.attr("href"))
        val title = aTag.text().trim().replace("Download ", "") // 'Download' शब्द हटाना

        val posterUrl = fixUrlNull(img.attr("src"))
        
        return newMovieSearchResponse(title, href, TvType.Movie) {
            this.posterUrl = posterUrl
        }
    }

    override suspend fun search(query: String, page: Int): SearchResponseList {
        // Bollyflix का सर्च पैटर्न: ?s=query
        val searchUrl = if (page <= 1) "$mainUrl/?s=$query" else "$mainUrl/page/$page/?s=$query"
        val document = app.get(searchUrl).document
        
        val results = document.select("article.latestPost").mapNotNull { el ->
            el.toSearchResult()
        }.toNewSearchResponseList()
        
        return results
    }

    override suspend fun load(url: String): LoadResponse {
        val document = app.get(url).document
        
        // मूवी की जानकारी (Title, Poster, Plot) निकालना
        val title = document.selectFirst("h1.title")?.text()?.replace("Download ", "")?.trim() ?: "Bollyflix Title"
        val poster = document.selectFirst("div.featured-thumbnail img")?.attr("src")
        val description = document.selectFirst("div.post-single-content p")?.text()?.trim()
        
        // यह चेक करना कि मूवी है या सीरीज
        val tvtag = if (url.contains("series") || url.contains("season")) TvType.TvSeries else TvType.Movie

        return newMovieLoadResponse(title, url, tvtag, url) {
            this.posterUrl = poster
            this.plot = description
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val document = app.get(data).document
        
        // Bollyflix के डाउनलोड बटनों को ढूंढना (प्रायः a.btn या a.dl-btn)
        document.select("a.btn, a.dl-btn, div.download-links a").forEach {
            val href = it.attr("href")
            
            // HubCloud, GDFlix, Gofile जैसे लिंक्स को आपकी मास्टर चाबी (loadExtractor) खुद संभाल लेगी
            if (href.startsWith("http")) {
                loadExtractor(href, data, subtitleCallback, callback)
            }
        }
        return true
    }
}
