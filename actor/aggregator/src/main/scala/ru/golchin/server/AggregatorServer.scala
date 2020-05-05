package ru.golchin.server

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import ru.golchin.actor._

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

object AggregatorServer extends JsonSupport {
  def main(args: Array[String]) {

    implicit val system: ActorSystem = ActorSystem("my-system")
    implicit val materializer: ActorMaterializer =  ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher


    val route =
      path("search") {
        get {
          parameter("q") { query =>
            implicit val timeout: Timeout = 20 . seconds
            val aggregatingActor: ActorRef = system.actorOf(
              AggregatingActor.props(Seq(new ThrowingSearchClient, new SlowSearchClient,
                new GoogleSearchClient(system, materializer)), 10 . seconds))
            complete((aggregatingActor ? Request(query, 5)).mapTo[AggregateResponse])
          }
        }
      }

    val port = if (!args.isEmpty) Integer.parseInt(args(0)) else 8080
    Http().bindAndHandle(route, "localhost", port)

    println(s"Server started on port $port")

  }
}
