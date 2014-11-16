package importer.models

import org.joda.time.LocalDate
import scala.collection.immutable

case class SeriesID(id: Long)

case class EpisodeID(id: Long)

case class TvSearchResult(
  seriesId: SeriesID,
  seriesName: String,
  description: String,
  banner: String
)

case class TvEpisode(
  episodeId: EpisodeID,
  episodeName: String,
  episodeNumber: Int,
  seasonNumber: Int,
  airDate: LocalDate
)

case class TvSeason(
  seasonNumber: Int,
  episodes: immutable.Seq[TvEpisode]
)

case class TvSeries(
  seriesId: SeriesID,
  seriesName: String,
  description: String,
  banner: String,
  seasons: immutable.Seq[TvSeason]
)


