package com.Bollyflix

import com.lagradost.cloudstream3.HomePageList
import com.lagradost.cloudstream3.HomePageResponse
import com.lagradost.cloudstream3.LoadResponse
import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.MainPageRequest
import com.lagradost.cloudstream3.SearchResponse
import com.lagradost.cloudstream3.SearchResponseList
import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.TvType
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.fixUrl
import com.lagradost.cloudstream3.fixUrlNull
import com.lagradost.cloudstream3.mainPageOf
import com.lagradost.cloudstream3.newHomePageResponse
import com.lagradost.cloudstream3.newMovieLoadResponse
import com.lagradost.cloudstream3.newMovieSearchResponse
import com.lagradost.cloudstream3.toNewSearchResponseList
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.loadExtractor
import org.jsoup.nodes.Element

class Bollyflix : MainAPI() {
    // Bollyflix का वर्किंग डोमेन
    override var mainUrl: String = "https://bollyflix.to" 
    override var name = "Bollyflix"
    override val hasMainPage = true
    override var lang = "hi"
    override val hasDownloadSupport = true
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries, TvType.Anime)

    // आपके द्वारा भेजे गए कोड के अनुसार कैटेगरी लिंक्स
    override val mainPage = mainPageOf(
        "" to "Latest Movies",
        "movies/bollywood/" to "Bollywood Movies",
        "movies/hollywood/" to "Hollywood Movies",
        "movies/south-hindi-dubbed/" to "South Hindi Dubbed",
        "movies/dual-audio-movies/" to "Dual Audio",
        "web-series/" to "Web Series"
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        // Bollyflix का पेजिंग फॉर्मेट
        val url = if (page <= 1) "$mainUrl/${request.data}" else "$mainUrl/${request.data}page/$page/"
        val document = app.get(url).document
        
        // 'latestPost' ही मूवी का असली डिब्बा है
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
        // नाम और लिंक के लिए 'h2.title a'
        val aTag = selectFirst("h2.title a") ?: return null
        val img = selectFirst("img") ?: return null

        val href = fixUrl(aTag.attr("href"))
        val title = aTag.text().trim().replace("Download ", "") 

        val posterUrl = fixUrlNull(img.attr("src"))
        
        return newMovieSearchResponse(title, href, TvType.Movie) {
            this.posterUrl = posterUrl
        }
    }

    override suspend fun search(query: String, page: Int): SearchResponseList {
        // सर्च के लिए Bollyflix का असली पैरामीटर ?s= है
        val searchUrl = if (page <= 1) "$mainUrl/?s=$query" else "$mainUrl/page/$page/?s=$query"
        val document = app.get(searchUrl).document
        
        val results = document.select("article.latestPost").mapNotNull { el ->
            el.toSearchResult()
        }.toNewSearchResponseList()
        
        return results
    }

    override suspend fun load(url: String): LoadResponse {
        val document = app.get(url).document
        
        val title = document.selectFirst("h1.title")?.text()?.replace("Download ", "")?.trim() ?: "Bollyflix"
        val poster = document.selectFirst("div.featured-thumbnail img")?.attr("src")
        val description = document.selectFirst("div.post-single-content p")?.text()?.trim()
        
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
        
        // Bollyflix के डाउनलोड बटन
        document.select("a.btn, div.download-links a").forEach {
            val href = it.attr("href")
            
            if (href.startsWith("http")) {
                loadExtractor(href, data, subtitleCallback, callback)
            }
        }
        return true
    }
} 
