package ru.golchin.db

import com.mongodb.reactivestreams.client.{MongoClient, MongoCollection, MongoDatabase}
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import ru.golchin.model._

trait DatabaseSupport {
  val client: MongoClient
  val database: MongoDatabase = client.getDatabase("Shop")
  val codecRegistry: CodecRegistry =
    fromRegistries(fromProviders(classOf[User], classOf[Product], classOf[Cost]), DEFAULT_CODEC_REGISTRY)
  def getCollection[T](collectionName: String, clazz: Class[T]): MongoCollection[T] =
    database.getCollection(collectionName, clazz).withCodecRegistry(codecRegistry)
}