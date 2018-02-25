package filters

import actors.StatsActor
import actors.StatsActor.RequestReceived
import akka.actor.ActorSystem
import akka.stream.Materializer
import play.api.Logger
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.Future

class StatsFilter(actorSystem: ActorSystem,  val mat: Materializer) extends Filter{
  override def apply(nextFilter: RequestHeader => Future[Result])
                    (header: RequestHeader): Future[Result] = {
    Logger.info(s"Serving another request : ${header.path}")
    actorSystem.actorSelection(StatsActor.path) ! RequestReceived
    nextFilter(header)
  }
}
