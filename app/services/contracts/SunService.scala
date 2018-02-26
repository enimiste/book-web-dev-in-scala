package services.contracts

import models.SunInfo

import scala.concurrent.Future

trait SunService {
  /**
    *
    * @param lat
    * @param lng
    * @return SunInfo("HH:mm:ss", "HH:mm:ss", "City name")
    */
  def getSunInfo(lat: Double, lng: Double): Future[SunInfo]
}
