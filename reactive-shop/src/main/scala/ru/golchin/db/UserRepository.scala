package ru.golchin.db

import akka.{Done, NotUsed}
import akka.stream.alpakka.mongodb.scaladsl.MongoSource
import akka.stream.alpakka.mongodb.scaladsl.MongoSink
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.mongodb.reactivestreams.client.MongoCollection
import org.mongodb.scala.model.Filters._
import ru.golchin.model.User

import scala.concurrent.Future

trait UserRepository extends DatabaseSupport {
  val userCollection: MongoCollection[User] = getCollection("Users", classOf[User])

  val insertUser: Sink[User, Future[Done]] = MongoSink.insertOne(userCollection)
  def userExists(login: String): Source[Boolean, NotUsed] =
    MongoSource(userCollection.countDocuments(equal("login", login))).via(Flow.fromFunction(_ > 0))
  def findByLogin(login: String): Source[User, NotUsed] = MongoSource(userCollection.find(equal("login", login)))
}
