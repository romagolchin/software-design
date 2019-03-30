package ru.golchin.db

import akka.{Done, NotUsed}
import akka.stream.alpakka.mongodb.scaladsl.MongoSource
import akka.stream.alpakka.mongodb.scaladsl.MongoSink
import akka.stream.scaladsl.{Sink, Source}
import com.mongodb.reactivestreams.client.MongoCollection
import org.mongodb.scala.model.Filters._
import ru.golchin.model.User

import scala.concurrent.Future

trait UserRepository extends DatabaseSupport {
  val userCollection: MongoCollection[User] = getCollection("Users", classOf[User])

  val insertUser: Sink[User, Future[Done]] = MongoSink.insertOne(userCollection)
  def findByLogin(login: String): Source[User, NotUsed] = MongoSource(userCollection.find(equal("login", login)))
}
