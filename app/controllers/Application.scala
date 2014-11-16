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

case class ShowToAdd(seriesID: SeriesID)

object Application extends Controller {

  val searchForm = Form(
    mapping(
      "query" -> nonEmptyText
    )(SearchRequest.apply)(SearchRequest.unapply)
  )

  val addForm = Form(
    mapping(
      "show_to_add" -> nonEmptyText
    )(seriesIDAsString => ShowToAdd(SeriesID(seriesIDAsString.toLong)))(showToAdd => Some(showToAdd.seriesID.toString))
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

  def addshow = Action.async { implicit request =>
    addForm.bindFromRequest.fold(
      formWithErrors => {
        Future {
          BadRequest(views.html.shows(List.empty))
        }
      },
      showToAdd => {
        DB.withConnection { implicit conn =>
          SQL("insert into Shows(seriesId) values ({seriesId})")
            .on('seriesId -> showToAdd.seriesID.id).executeInsert()
          val ids: List[Long] = SQL("Select * from Shows")().map(row => row[Long]("seriesId")).toList

          val seriesFutures = ids map { id =>
            Search.getSeriesInfo(SeriesID(id = id))
          }

          Future.sequence(seriesFutures) map { series =>
            Ok(views.html.shows(series.flatten.toList))
          }
        }
      }
    )
  }

}