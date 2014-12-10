package models

object SeriesStatusSummary {
  def fromTvSeries(tvSeries: TvSeries): SeriesStatusSummary = {
    val seriesSummary = SeriesSummary.fromTvSeries(tvSeries).get
    val totalEpisodes = tvSeries.seasons.foldLeft(0)((total, season) => total + season.episodes.size)
    val downloadedEpisodes = 0
    SeriesStatusSummary(
      seriesSummary = seriesSummary,
      totalEpisodes = totalEpisodes,
      downloadedEpisodes = downloadedEpisodes
    )
  }
}

case class SeriesStatusSummary(seriesSummary: SeriesSummary, totalEpisodes: Int, downloadedEpisodes: Int)