package models

import scala.collection.immutable

object SeasonSummary {
  def fromTvSeason(tvSeason: TvSeason): Option[SeasonSummary] = {
    val episodes = tvSeason.episodes.map(
      EpisodeSummary.fromTvEpisode(_)
    )
    Some(SeasonSummary(seasonNumber = tvSeason.seasonNumber.toString, episodes = episodes))
  }
}

case class SeasonSummary private(
    seasonNumber: String,
    episodes: immutable.Seq[Option[EpisodeSummary]]
)

