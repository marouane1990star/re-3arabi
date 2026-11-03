package com.re3arabi.provider

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class ArabicProvider : MainAPI() {
    override var mainUrl = "https://example-arabic-site.com"
    override var name = "عربي"
    override var lang = "ar"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)

    override val mainPage = mainPageOf(
        "$mainUrl/latest" to "أحدث الإضافات",
        "$mainUrl/movies" to "أفلام",
        "$mainUrl/series" to "مسلسلات",
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val document = app.get("${request.data}/page/$page").document
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
        val document = app.get("$mainUrl/search?q=$query").document
        return document.select("div.movie-item").mapNotNull { el ->
            val title = el.select("h3").text().trim()
            val href = fixUrl(el.select("a").attr("href"))
            val img = fixUrlNull(el.select("img").attr("src"))
            if (title.isEmpty() || href.isEmpty()) return@mapNotNull null
            newMovieSearchResponse(title, href, TvType.Movie) {
                this.posterUrl = img
            }
        }
    }

    override suspend fun load(url: String): LoadResponse {
        val document = app.get(url).document
        val title = document.select("h1.title").text().trim()
        val poster = fixUrlNull(document.select("img.poster").attr("src"))
        val description = document.select("div.plot").text().trim()
        val year = document.select("span.year").text().trim().toIntOrNull()
        val episodes = document.select("div.episode a").mapIndexed { index, el ->
            val epHref = fixUrl(el.attr("href"))
            val epName = el.text().trim().ifEmpty { "الحلقة ${index + 1}" }
            newEpisode(epHref) {
                this.name = epName
                this.episode = index + 1
            }
        }
        return if (episodes.isNotEmpty()) {
            newTvSeriesLoadResponse(title, url, TvType.TvSeries, episodes) {
                this.posterUrl = poster
                this.plot = description
                this.year = year
            }
        } else {
            newMovieLoadResponse(title, url, TvType.Movie, url) {
                this.posterUrl = poster
                this.plot = description
                this.year = year
            }
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val document = app.get(data).document
        var found = false
        document.select("iframe, video source, a.watch").forEach { el ->
            val link = el.attr("src").ifEmpty { el.attr("href") }
            if (link.isNotEmpty()) {
                callback.invoke(
                    newExtractorLink(
                        source = this.name,
                        name = this.name,
                        url = fixUrl(link),
                        type = ExtractorLinkType.M3U8
                    ) {
                        this.referer = mainUrl
                        this.quality = Qualities.Unknown.value
                    }
                )
                found = true
            }
        }
        return found
    }
}
