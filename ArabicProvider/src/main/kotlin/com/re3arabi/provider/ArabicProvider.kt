package com.re3arabi.provider

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class ArabicProvider : MainAPI() {
    override var mainUrl = "https://example.com"
    override var name = "عربي"
    override var lang = "ar"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)

    override val mainPage = mainPageOf(
        "$mainUrl/latest" to "أحدث الإضافات",
        "$mainUrl/movies" to "أفلام",
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val document = app.get(request.data).document
        val items = document.select("div.movie-item").mapNotNull { el ->
            val title = el.select("h3").text().trim()
            val href = fixUrl(el.select("a").attr("href"))
            val img = fixUrlNull(el.select("img").attr("src"))
            if (title.isEmpty() || href.isEmpty()) return@mapNotNull null
            newMovieSearchResponse(title, href, TvType.Movie) {
                this.posterUrl = img
            }
        }
        return newHomePageResponse(request.name, items)
    }

    override suspend fun search(query: String): List<SearchResponse> {
        return emptyList() // سنقوم بتفعيلها لاحقاً
    }

    override suspend fun load(url: String): LoadResponse {
        return newMovieLoadResponse("Movie", url, TvType.Movie, url) {
            this.posterUrl = ""
            this.plot = ""
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        return true
    }
}
