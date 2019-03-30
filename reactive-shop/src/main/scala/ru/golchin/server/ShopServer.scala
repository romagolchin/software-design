package ru.golchin.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives.concat
import com.mongodb.reactivestreams.client.{MongoClient, MongoClients}

import scala.concurrent.ExecutionContext

object ShopServer extends App with UserRoutes with ProductRoutes {
  lazy val client: MongoClient = MongoClients.create()
  lazy val routes = concat(userRoutes, productRoutes)

  private val host: String = if (args.length > 1) args(1) else "localhost"
  private val port: Int = if (args.nonEmpty) Integer.parseInt(args(0)) else 8080

  Http().bindAndHandle(routes, host, port)
}
