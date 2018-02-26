package controllers

import java.util.concurrent.TimeUnit
import javax.inject._

import actions.{UserAuthAction, UserAuthOptAction}
import actors.StatsActor
import actors.StatsActor.GetStats
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import forms.UserLoginData
import models.CombinedData
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc._
import services.contracts.{AuthService, SunService, WeatherService}

import scala.concurrent.ExecutionContext.Implicits.global

class Application @Inject()(components: ControllerComponents, ws: WSClient,
                            sunService: SunService,
                            weatherService: WeatherService,
                            authService: AuthService,
                            userAuthAction: UserAuthAction,
                            userAuthOptAction: UserAuthOptAction,
                            actorSystem: ActorSystem)
  extends AbstractController(components) {
  val userLoginDataForm = Form {
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(UserLoginData.apply)(UserLoginData.unapply)
  }

  def index: Action[AnyContent] = userAuthOptAction { request =>
    Ok(views.html.index(request.user))
  }

  def profile: Action[AnyContent] = userAuthAction { request =>
    Ok(views.html.profile(request.user))
  }

  def logout: Action[AnyContent] = userAuthAction { request =>
    val cookie = authService.logout(request)
    Redirect("/login").withCookies(cookie)
  }

  def data: Action[AnyContent] = Action.async { request =>
    /*
      Region: Rabat-Sale
      Country: Morocco
      Latitude: 34.0252778
      Longitude: -6.8361111

      Region: Agadir
      Country: Morocco
      Latitude: 30.4
      Longitude: -9.6

      Url : http://api.sunrise-sunset.org/json?lat=34.0252778&lng=-6.8361111&formatted=0
      Url : http://api.openweathermap.org/data/2.5/weather?lat=34.0252778&lon=-6.8361111&units=metric&appId=
     */
    val lat = request.getQueryString("lat").map(_.toDouble).getOrElse(34.0252778)
    val lng = request.getQueryString("lng").map(_.toDouble).getOrElse(-6.8361111)

    implicit val timeout: Timeout = Timeout(5, TimeUnit.SECONDS)
    val actor = actorSystem.actorSelection(StatsActor.path)
    val countF = (actor ? GetStats).mapTo[Int]

    val eventualInfoF = sunService.getSunInfo(lat, lng)
    val eventualTupleF = weatherService.getTemperature(lat, lng)
    for {
      sinf <- eventualInfoF
      wt <- eventualTupleF
      count <- countF
    } yield {
      Ok(Json.toJson(CombinedData(if (sinf.city.nonEmpty) sinf else sinf.copy(city = Some(wt._1)), wt._2, count)))
    }
  }

  def loginForm: Action[AnyContent] = Action {
    Ok(views.html.login())
  }

  def login: Action[AnyContent] = Action { implicit request =>
    userLoginDataForm.bindFromRequest().fold(
      _ => Ok(views.html.login(Some("Wrong data"))),
      userLoginData => {
        authService.login(userLoginData.username, userLoginData.password) match {
          case Some(cookie) => Redirect("/").withCookies(cookie)
          case None => Ok(views.html.login(Some("Login Failed")))
        }
      })
  }
}
