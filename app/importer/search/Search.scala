package importer.search

import java.io.{FileOutputStream, File}
import java.util.zip.ZipFile
import importer.models.{SeriesID, TvSearchResult, TvSeries}
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee._
import play.api.libs.ws._
import scala.concurrent.Future
import scala.xml.XML

object Search {
  private val API_KEY = "AD99DFD81F047AEB"
  private val API_MIRROR = "http://thetvdb.com/api"
  private val KEYED_API_MIRROR = s"$API_MIRROR/$API_KEY"
  private val SEARCH_ENDPOINT = s"$API_MIRROR/GetSeries.php"
  private val SERIES_INFO_ENDPOINT = s"$KEYED_API_MIRROR/series"

  def findShow(query: String): Future[Seq[TvSearchResult]] = {
    WS.url(SEARCH_ENDPOINT).withQueryString(("seriesname" -> query)).get() map { response =>
      for {
        series <- response.xml \\ "Series"
      } yield {
        val seriesIDString: String = (series \\ "seriesid").text
        val seriesName: String = (series \\ "SeriesName").text
        val description: String = (series \\ "Overview").text
        val banner: String = (series \\ "banner").text
        TvSearchResult(
          seriesId = SeriesID(seriesIDString.toLong),
          seriesName = seriesName,
          description = description,
          banner = s"http://thetvdb.com/banners/$banner"
        )
      }
    }
  }

  def getSeriesInfo(seriesId: SeriesID): Future[Option[TvSeries]] = {
    val seriesIdString = seriesId.id.toString
    println(s"$SERIES_INFO_ENDPOINT/$seriesIdString/all/en.zip")
    WS.url(s"$SERIES_INFO_ENDPOINT/$seriesIdString/all/en.zip").getStream() flatMap {
      case (headers, body) =>
        val targetFile = File.createTempFile(seriesId.id.toString, ".zip")
        targetFile.deleteOnExit()
        val outputStream = new FileOutputStream(targetFile)

        val iteratee = Iteratee.foreach[Array[Byte]] { bytes =>
          outputStream.write(bytes)
        }

        val completedFileFuture = (body |>>> iteratee).andThen {
          case result =>
            outputStream.close()
            result.get
        }.map(_ => targetFile)

        completedFileFuture map { completedFile =>
          println(completedFile.getAbsolutePath)
          val seriesInfoZipFile = new ZipFile(completedFile)
          val xml = XML.load(seriesInfoZipFile.getInputStream(seriesInfoZipFile.getEntry("en.xml")))
          println(xml \\ "Series")
          (for {
            series <- xml \\ "Series"
          } yield {
            val seriesIDString: String = (series \\ "id").text
            val seriesName: String = (series \\ "SeriesName").text
            val description: String = (series \\ "Overview").text
            val banner: String = (series \\ "banner").text
            TvSeries(
              seriesId = SeriesID(seriesIDString.toLong),
              seriesName = seriesName,
              description = description,
              banner = s"http://thetvdb.com/banners/$banner"
            )
          }).headOption
        }
    }
  }
}
