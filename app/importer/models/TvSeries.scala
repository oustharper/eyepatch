package importer.models

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
  seasonNumber: Int
)

case class TvSeason(
  episodes: immutable.IndexedSeq[TvEpisode]
)

case class TvSeries(
  seriesId: SeriesID,
  seriesName: String,
  description: String,
  banner: String,
  seasons: immutable.IndexedSeq[TvSeason]
)


