package controllers

import importer.search.Search
import play.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def tvshow = Action.async {
    Search.findShow("Battlestar") map { shows =>
      Ok(views.html.index(shows.toString))
    }
  }

}