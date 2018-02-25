package actors

import actors.StatsActor.{GetStats, Ping, RequestReceived}
import akka.actor.Actor
import play.api.Logger

object StatsActor {
  val name = "statsActor"
  val path = s"/user/$name"

  case object Ping

  case object RequestReceived

  case object GetStats

}

class StatsActor extends Actor {
  var counter: Int = 0

  override def receive: Receive = {
    case Ping => ()
    case RequestReceived => counter += 1
    case GetStats => sender() ! counter
  }
}
