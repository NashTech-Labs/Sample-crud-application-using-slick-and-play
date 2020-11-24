package models

import slick.jdbc.PostgresProfile.api._

class User(tag: Tag) extends Table[(String, Int, Int)](tag, "employee") {
  def name = column[String]("NAME")
  def id = column[Int]("ID", O.PrimaryKey)
  def age = column[Int]("AGE")

  def * = (name, id,age)

}
