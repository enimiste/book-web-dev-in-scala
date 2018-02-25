package model

import play.api.libs.json.{Json, OWrites}

case class CombinedData(sunInfo: SunInfo, temperature: Double, requestsCount: Int)

object CombinedData {
  implicit val write: OWrites[CombinedData] = Json.writes[CombinedData]
}
