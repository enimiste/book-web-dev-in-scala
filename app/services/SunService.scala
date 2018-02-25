package services

import java.time.format.DateTimeFormatter
import java.time.{ZoneId, ZonedDateTime}

import model.SunInfo
import play.api.libs.ws.WSClient

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class SunService(ws: WSClient) {
  def getSunInfo(lat: Double, lng: Double): Future[SunInfo] = {
    val sunRespF = ws.url(s"http://api.sunrise-sunset.org/json?lat=$lat&lng=$lng&formatted=0").get()
    for {
      sr <- sunRespF
    } yield {
      val json = sr.json
      val sunriseJsonStr = (json \ "results" \ "sunrise").as[String]
      val sunsetJsonStr = (json \ "results" \ "sunset").as[String]


      val sunriseTime = ZonedDateTime.parse(sunriseJsonStr)
      val sunsetTime = ZonedDateTime.parse(sunsetJsonStr)

      val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        .withZone(ZoneId.of("Africa/Casablanca"))

      SunInfo(formatter.format(sunriseTime), formatter.format(sunsetTime))
    }
  }
}
