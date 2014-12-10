package models

object SeriesSummary {
  def fromTvSeries(tvSeries: TvSeries): Option[SeriesSummary] = {
    Some(SeriesSummary(
      seriesID = tvSeries.seriesId,
      seriesName = tvSeries.seriesName,
      isActive = tvSeries.active
    ))
  }
}

case class SeriesSummary private(
    seriesID: SeriesID,
    seriesName: String,
    isActive: Option[Boolean]
)
