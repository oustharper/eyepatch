package importer.models

case class SeriesID(id: Long)

case class TvSearchResult(
  seriesId: SeriesID,
  seriesName: String,
  description: String,
  banner: String
)

case class TvSeries(
  seriesId: SeriesID,
  seriesName: String,
  description: String,
  banner: String
)
