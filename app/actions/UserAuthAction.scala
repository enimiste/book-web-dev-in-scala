package actions

import models.User
import play.api.mvc._
import services.contracts.AuthService

import scala.concurrent.{ExecutionContext, Future}

case class UserAuthRequest[A](user: User, request: Request[A]) extends WrappedRequest[A](request)

class UserAuthAction(authService: AuthService,
                     ec: ExecutionContext,
                     parsers: PlayBodyParsers) extends ActionBuilder[UserAuthRequest, AnyContent] {
  override def parser: BodyParser[AnyContent] = parsers.defaultBodyParser

  override def invokeBlock[A](request: Request[A], block: UserAuthRequest[A] => Future[Result]): Future[Result] = {
    authService.auth(request) match {
      case Some(user) => block(UserAuthRequest(user, request))
      case None => Future.successful(Results.Redirect("/login"))
    }
  }

  override protected def executionContext: ExecutionContext = ec
}
