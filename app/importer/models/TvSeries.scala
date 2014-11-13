package models

import scala.collection.immutable

case class SeriesID(id: Long)

case class TvSearchResult(
  seriesId: SeriesID,
  seriesName: String
)
