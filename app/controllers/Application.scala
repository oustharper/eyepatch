package controllers

import anorm._
import importer.search.Search
import importer.models.{SeriesID, TvSearchResult}
import play.api._
import play.api.db._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import play.api.Play.current
import scala.concurrent.Future

case class SearchRequest(query: String)

case class ShowToAdd(seriesID: SeriesID, baseDir: String)

object Application extends Controller {

  val searchForm = Form(
    mapping(
      "query" -> nonEmptyText
    )(SearchRequest.apply)(SearchRequest.unapply)
  )

  val addForm = Form(
    mapping(
      "show_to_add" -> longNumber,
      "base_dir" -> nonEmptyText
    ) { case formContent: (Long, String) =>
      val (seriesIdAsLong, baseDir) = formContent
      ShowToAdd(SeriesID(seriesIdAsLong), baseDir)
    } { showToAdd: ShowToAdd =>
      ShowToAdd.unapply(showToAdd).map(tuple => (tuple._1.id, tuple._2))
    }
  )

  def shows = Action.async {
    DB.withConnection { implicit conn =>
      val ids: List[Long] = SQL("Select * from Shows")().map(row => row[Long]("seriesId")).toList

      val seriesFutures = ids map { id =>
        Search.getSeriesInfo(SeriesID(id = id))
      }

      Future.sequence(seriesFutures) map { series =>
        Ok(views.html.shows(series.flatten.toList))
      }
    }
  }

  def show(seriesId: String) = Action.async {
    Search.getSeriesInfo(SeriesID(id = seriesId.toLong)) map { show =>
      Ok(views.html.show(show.get))
    }
  }

  def showsearch = Action {
    Ok(views.html.search(searchForm, Seq.empty))
  }

  def showsearchresults = Action.async { implicit request =>
    searchForm.bindFromRequest.fold(
      formWithErrors => {
        Future {
          BadRequest(views.html.search(formWithErrors, Seq.empty))
        }
      },
      searchRequest => {
        Search.findShow(searchRequest.query) map { searchResults: Seq[TvSearchResult] =>
          Ok(views.html.search(searchForm, searchResults))
        }
      }
    )
  }

  def addshow(seriesId: String) = Action.async {
    Search.getSeriesInfo(SeriesID(id = seriesId.toLong)) map { show =>
      Ok(views.html.addshow(show.get))
    }
  }

  def javascriptRoutes = Action { implicit request =>
    Ok(
      Routes.javascriptRouter("jsRoutes")(
      )
    ).as("text/javascript")
  }
}