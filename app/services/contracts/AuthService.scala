package services.contracts

import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import java.util.{Base64, UUID}

import actions.UserAuthRequest
import models.User
import play.api.cache.SyncCacheApi
import play.api.mvc.{AnyContent, Cookie, RequestHeader}

import scala.concurrent.duration.Duration

trait AuthService {
  val cacheApi: SyncCacheApi
  val mda: MessageDigest = MessageDigest.getInstance("SHA-512")
  val cookieHeader = "X-Auth-Token"

  /**
    * First time
    *
    * @param userCode
    * @param plainPassword
    * @return
    */
  def login(userCode: String, plainPassword: String): Option[Cookie] =
    checkUser(userCode, plainPassword).map(createCookie)

  /**
    * Check if the user is auth from the request info
    *
    * @param request
    * @return
    */
  def auth(implicit request: RequestHeader): Option[User] = for {
    c <- request.cookies.get(cookieHeader)
    user <- cacheApi.get[User](c.value)
  } yield user

  /**
    *
    * @param request
    * @return
    */
  def logout(request: UserAuthRequest[AnyContent]): Cookie = {
    for {
      c <- request.cookies.get(cookieHeader)
    } cacheApi.remove(c.value)
    Cookie(cookieHeader, "", Some(-1))
  }


  protected def checkUser(userCode: String, plainPassword: String): Option[User]

  protected def createCookie(user: User): Cookie = {
    val randomPart = UUID.randomUUID().toString.toUpperCase
    val userPart = user.userId.toString.toUpperCase
    val key = s"$randomPart|$userPart"
    val token = Base64.getEncoder.encodeToString(mda.digest(key.getBytes))
    val duration = Duration.create(10, TimeUnit.HOURS)
    cacheApi.set(token, user, duration)
    Cookie(cookieHeader, token, Some(duration.toSeconds.toInt))
  }
}
