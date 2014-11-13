package importer.search

import models.{SeriesID, TvSearchResult}
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws._
import scala.concurrent.Future

object Search {
  private val API_KEY = "AD99DFD81F047AEB"
  private val API_MIRROR = "http://thetvdb.com/api/"
  private val KEYED_API_MIRROR = s"$API_MIRROR/$API_KEY"
  private val SEARCH_ENDPOINT = s"$API_MIRROR/GetSeries.php"

  def findShow(query: String): Future[Seq[TvSearchResult]] = {
    WS.url(SEARCH_ENDPOINT).withQueryString(("seriesname" -> query)).get() map { response =>
      for {
        series <- response.xml \\ "Series"
      } yield {
        val seriesIDString: String = (series \\ "seriesid").text
        val seriesName: String = (series \\ "SeriesName").text
        TvSearchResult(seriesId = SeriesID(seriesIDString.toLong), seriesName = seriesName)
      }
    }
  }
}
