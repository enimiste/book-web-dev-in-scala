package services

import models.User
import org.mindrot.jbcrypt.BCrypt
import play.api.cache.SyncCacheApi
import scalikejdbc._
import services.contracts.AuthService

class DbAuthService(val cacheApi: SyncCacheApi) extends AuthService {
  override protected def checkUser(userCode: String, plainPassword: String): Option[User] =
    DB.readOnly { implicit session =>
      val userOpt = sql"SELECT * FROM users WHERE user_code=$userCode"
        .map(User.fromDb).single().apply()
      userOpt.flatMap { user =>
        if (BCrypt.checkpw(plainPassword, user.password)) Some(user) else None
      }
    }
}
