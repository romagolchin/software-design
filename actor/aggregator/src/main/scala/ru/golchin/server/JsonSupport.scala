package ru.golchin.server

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import ru.golchin.actor.AggregateResponse
import spray.json._
import spray.json.RootJsonFormat


trait JsonSupport extends SprayJsonSupport  with DefaultJsonProtocol {
  implicit val responseJsonFormat = jsonFormat1(AggregateResponse)
}
