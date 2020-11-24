package models

import slick.jdbc.PostgresProfile.api._

class MyDatabase {

  val database=Database.forConfig("postgres")

}
