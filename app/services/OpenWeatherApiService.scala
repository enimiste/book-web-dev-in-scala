package services

import play.api.libs.ws.WSClient
import services.contracts.WeatherService

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class OpenWeatherApiService(ws: WSClient, openWeatherAppId: String) extends WeatherService{

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
