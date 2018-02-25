package services

import play.api.libs.ws.WSClient

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class WeatherService(ws: WSClient) {
  val openWeatherAppId = "30b9a4541627b5cd44f33c64b89fa5e2"

  def getTemperature(lat: Double, lng: Double): Future[(String, Double)] = {
    val weatherRespF = ws.url(s"http://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lng&units=metric&appId=$openWeatherAppId").get()
    for {
      wr <- weatherRespF
    } yield {
      val temp = (wr.json \ "main" \ "temp").as[Double]
      val city = (wr.json \ "name").as[String]
      (city, temp)
    }
  }
}
