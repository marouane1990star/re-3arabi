package com.re3arabi.provider

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class ArabicProvider : MainAPI() { // <--- لاحظ حرف I كبير هنا
    override var mainUrl = "https://example.com"
    override var name = "عربي"
    override var lang = "ar"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)

    override val mainPage = mainPageOf(
        "$mainUrl/latest" to "أحدث الإضافات"
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        return newHomePageResponse(request.name, emptyList())
    }

    override suspend fun search(query: String): List<SearchResponse> {
        return emptyList()
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
