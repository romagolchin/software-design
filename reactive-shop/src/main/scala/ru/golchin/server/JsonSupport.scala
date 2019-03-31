package ru.golchin.server

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import ru.golchin.model.{Cost, Product, User}
import spray.json._

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol{
  implicit val jsonUserFormat: RootJsonFormat[User] = jsonFormat4(User)
  implicit val jsonProductFormat: RootJsonFormat[Product] = jsonFormat2(Product)
  implicit val jsonCostFormat: JsonFormat[Cost] = new JsonFormat[Cost] {
    override def write(cost: Cost): JsValue = JsObject(
      "value" -> JsNumber(cost.value),
      "currency" -> JsString(cost.currency.toString)
    )

    override def read(json: JsValue): Cost = json match {
      case JsObject(m) =>
        val cost = m.get("value").flatMap { v =>
          m.get("currency").map { curr => Cost(v.convertTo[Double], curr.convertTo[String]) }
        }
        if (cost.isEmpty)
          throw DeserializationException("not all fields provided ")
        cost.get
      case _ => throw DeserializationException("expected object")
    }
  }

}
