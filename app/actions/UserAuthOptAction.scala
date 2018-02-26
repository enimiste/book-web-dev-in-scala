package actions

import models.User
import play.api.mvc._
import services.contracts.AuthService

import scala.concurrent.{ExecutionContext, Future}

case class UserAuthOptRequest[A](user: Option[User], request: Request[A]) extends WrappedRequest[A](request)

class UserAuthOptAction(authService: AuthService,
                        ec: ExecutionContext,
                        parsers: PlayBodyParsers) extends ActionBuilder[UserAuthOptRequest, AnyContent] {
  override def parser: BodyParser[AnyContent] = parsers.defaultBodyParser

  override def invokeBlock[A](request: Request[A], block: UserAuthOptRequest[A] => Future[Result]): Future[Result] = {
    block(UserAuthOptRequest(authService.auth(request), request))
  }

  override protected def executionContext: ExecutionContext = ec
}
