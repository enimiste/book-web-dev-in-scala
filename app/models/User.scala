package models

import java.util.UUID

import scalikejdbc.WrappedResultSet

case class User(userId: UUID, userCode: String, password: String)

object User {
  def fromDb(record: WrappedResultSet): User =
    User(UUID.fromString(record.string("user_id")), record.string("user_code"), record.string("password"))
}
