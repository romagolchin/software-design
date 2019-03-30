package ru.golchin.db

import akka.{Done, NotUsed}
import akka.stream.alpakka.mongodb.scaladsl.MongoSource
import akka.stream.alpakka.mongodb.scaladsl.MongoSink
import akka.stream.scaladsl.Source
import akka.stream.scaladsl.Sink
import com.mongodb.reactivestreams.client.MongoCollection
import org.mongodb.scala.model.Filters._
import ru.golchin.model.Product

import scala.concurrent.Future

trait ProductRepository extends DatabaseSupport {
  val productCollection: MongoCollection[Product] = getCollection("Products", classOf[Product])
  val insertProduct: Sink[Product, Future[Done]] = MongoSink.insertOne(productCollection)
  def findByTitle(title: String): Source[Product, NotUsed] =
    MongoSource(productCollection.find(equal("title", title)))
}
