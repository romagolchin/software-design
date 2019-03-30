package ru.golchin.server

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import ru.golchin.model.Currency.Currency
import ru.golchin.model.{Cost, Currency, Product, User}
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
          m.get("currency").map { curr => Cost(v.convertTo[Double], jsonCurrencyFormat.read(curr)) }
        }
        if (cost.isEmpty)
          throw DeserializationException("not all fields provided ")
        cost.get
      case _ => throw DeserializationException("expected object")
    }
  }
  implicit val jsonCurrencyFormat: JsonFormat[Currency] = enumFormat(Currency)

  implicit def enumFormat[T <: Enumeration](implicit enu: T): RootJsonFormat[T#Value] =
    new RootJsonFormat[T#Value] {
      def write(obj: T#Value): JsValue = JsString(obj.toString)

      def read(json: JsValue): T#Value = {
        json match {
          case JsString(txt) => enu.withName(txt)
          case somethingElse => throw DeserializationException(s"Expected a value from enum $enu instead of $somethingElse")
        }
      }
    }
}
