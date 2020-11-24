package models

import play.api.mvc.ControllerComponents
import slick.dbio.DBIO
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global

case class UserDetail(name: String, id: Int, age: Int)

import scala.concurrent.Future

class Dao(db: MyDatabase) extends {

  val user = TableQuery[User]

  def createUserClass = {

    user.schema.createIfNotExists

  }

  def insertTable(users: (String, Int, Int)) = {

    db.database.run(user += users)
  }

  //
  //  def dao:Future[Unit]={
  //    db.database.run(DBIO.seq(createUserClass
  //      ,insertTable))
  //  }

  def getData: Future[List[UserDetail]] = {
    val query = user.result
    db.database.run(query.map(users => users.toList.map(users => UserDetail(users._1
      , users._2,users._3))))
  }

  def delete(id: Int): Future[Int] =
    db.database.run(user.filter(_.id === id).delete)

}
