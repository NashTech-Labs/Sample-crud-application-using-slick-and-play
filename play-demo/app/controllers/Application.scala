package controllers

import akka.actor.{ActorSystem, Props}
import models.{Dao, MyDatabase, User, UserDetail}
import javax.inject._
import play.api.Logging
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import play.api.routing.sird.POST
import services.DemoActor
import views.html._


import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.TimeoutException
import scala.concurrent.duration.DurationInt

@Singleton
class Application @Inject()(cc: MessagesControllerComponents) extends MessagesAbstractController(cc) with Logging {
  val database = new MyDatabase
  val daoObj = new Dao(database)
  val system=ActorSystem()
  private val userDetail = ArrayBuffer[UserDetail]()


  val demoActorref = system.actorOf(
    Props(
      new DemoActor),
    "DemoActor"
  )

  val form: Form[UserDetail] = Form (
    mapping(
      "name" -> nonEmptyText,
      "id" -> number,
      "age" -> number
    )(UserDetail.apply)(UserDetail.unapply)
  )

  def getData(): Action[AnyContent] = Action.async{ implicit request: Request[AnyContent] =>

daoObj.getData.map(data=>
  Ok(data.toString()))

  }

  val mainPostUrl = routes.Application.save()

  def add: Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    // pass an unpopulated form to the template
    Ok(views.html.editUserDetail(form, mainPostUrl))
  }

  def save = Action { implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[UserDetail] =>
      logger.debug("CAME INTO errorFunction")
      // this is the bad case, where the form had validation errors.
      // show the user the form again, with the errors highlighted.
      BadRequest(views.html.editUserDetail(formWithErrors, mainPostUrl))
    }


    val successFunction = { data: UserDetail =>
      logger.debug("CAME INTO successFunction")
      // this is the SUCCESS case, where the form was successfully parsed as a UserDetail
      val userData = UserDetail(
        data.name,
        data.id,
        data.age
      )
      logger.debug(userData.toString)
      userDetail.append(userData)
      daoObj.insertTable((data.name,data.id,data.age))
      println(userDetail)
      Redirect(routes.Application.add()).flashing("info" -> "user detail added (trust me)")

    }
    val formValidationResult: Form[UserDetail] = form.bindFromRequest
    formValidationResult.fold(
      errorFunction,   // sad case
      successFunction  // happy case
    )
  }
  def printMessage(): Action[AnyContent] = Action{ implicit request: Request[AnyContent] =>

  system.scheduler.scheduleAtFixedRate(0.seconds,5.seconds,demoActorref,"message")
    Ok("message will print after 5 seconds")

  }

  def deleteData(id:Int): Action[AnyContent] = Action.async{ implicit request: Request[AnyContent] =>
    daoObj.delete(id).map(result=>
      Ok("data deleted"))

  }
}