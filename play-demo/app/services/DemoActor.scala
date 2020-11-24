package services

import akka.actor.Actor
import play.api.Logging

class DemoActor extends Actor with Logging{

  override def receive: Receive = {
    case "message" => println("i am printing after 5 seconds")
    case _ => println("invalid messaged")
  }
}
