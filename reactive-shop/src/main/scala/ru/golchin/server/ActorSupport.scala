package ru.golchin.server

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext

trait ActorSupport {
  implicit val system: ActorSystem = ActorSystem("reactive-shop")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher
}
