import actors.StatsActor
import actors.StatsActor.Ping
import akka.actor.{ActorRef, Props}
import com.softwaremill.macwire.wire
import controllers.{Application, AssetsComponents}
import filters.StatsFilter
import play.api
import play.api.ApplicationLoader.Context
import play.api.db.{DBComponents, HikariCPComponents}
import play.api.db.evolutions.EvolutionsComponents
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc.{ControllerComponents, DefaultControllerComponents, EssentialFilter}
import play.api.routing.Router
import play.api.{ApplicationLoader, BuiltInComponentsFromContext, Logger, LoggerConfigurator}
import play.filters.HttpFiltersComponents
import router.Routes
import scalikejdbc.config.DBs
import services.{SunService, WeatherService}

import scala.concurrent.Future

class AppApplicationLoader extends ApplicationLoader {
  override def load(context: ApplicationLoader.Context): api.Application = {
    LoggerConfigurator(context.environment.classLoader).foreach { cfg =>
      cfg.configure(context.environment)
    }

    new AppComponents(context).application
  }
}

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context)
  with AhcWSComponents with AssetsComponents
  with HttpFiltersComponents with EvolutionsComponents
  with DBComponents with HikariCPComponents{
  override lazy val controllerComponents: ControllerComponents = wire[DefaultControllerComponents]

  override def router: Router = wire[Routes]

  lazy val prefix: String = "/"
  lazy val applicationController: Application = wire[Application]

  lazy val sunService: SunService = wire[SunService]
  lazy val weatherService: WeatherService = wire[WeatherService]

  lazy val statsFilter: StatsFilter = wire[StatsFilter]
  override lazy val httpFilters: Seq[EssentialFilter] = Seq(statsFilter)

  lazy val statsActor: ActorRef = actorSystem.actorOf(Props(wire[StatsActor]), StatsActor.name)

  val onStart: Unit = {
    Logger.info("The app is about to start")
    DBs.setupAll()
    applicationEvolutions
    statsActor ! Ping
  }

  applicationLifecycle.addStopHook(() => {
    Logger.info("The app is about to stop")
    DBs.closeAll()
    Future.successful(Unit)
  })


}
