package services

import models.SunInfo
import services.contracts.SunService

import scala.concurrent.Future

class DummySunService extends SunService{
  override def getSunInfo(lat: Double, lng: Double): Future[SunInfo] =
    Future.successful(SunInfo("12:00:00", "17:00:01", Some("Dummy City")))
}
