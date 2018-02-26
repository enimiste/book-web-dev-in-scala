package services.contracts

import scala.concurrent.Future

trait WeatherService {
  /**
    *
    * @param lat
    * @param lng
    * @return ("city name", temp in C°)
    */
  def getTemperature(lat: Double, lng: Double): Future[(String, Double)]
}
