package models

object EpisodeSummary {
  def fromTvEpisode(tvEpisode: TvEpisode): Option[EpisodeSummary] = {
    Some(EpisodeSummary(
      episodeId = tvEpisode.episodeId,
      episodeNumber = tvEpisode.episodeNumber.toString,
      episodeName = tvEpisode.episodeName,
      airDate = tvEpisode.airDate.map(_.toString).getOrElse("UNKNOWN")
    ))
  }
}

case class EpisodeSummary private(
    episodeId: EpisodeID,
    episodeNumber: String,
    episodeName: String,
    airDate: String)
