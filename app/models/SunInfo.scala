package models

import play.api.libs.json.{Json, OWrites}

case class SunInfo(sunrise: String, sunset: String, city: Option[String] = None)

object SunInfo {
  implicit val write: OWrites[SunInfo] = Json.writes[SunInfo]
}