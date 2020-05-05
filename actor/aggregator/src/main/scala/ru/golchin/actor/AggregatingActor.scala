package ru.golchin.actor

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.Future.successful
import scala.concurrent.duration._

class AggregatingActor(childrenClients: Seq[SearchClient],
                       implicit val timeout: Timeout = 1.seconds) extends Actor with ActorLogging {

  import context.dispatcher

  def filterSuccessfulToMap[S, T](futures: Seq[Future[(S, T)]]): Future[Map[S, T]] = {
    futures.map(_.map(List(_))
      .recover { case t => log.warning(t.getClass + " " + t.getMessage); List() })
      .foldLeft(successful(Map[S, T]())) {
        (f1: Future[Map[S, T]], f2: Future[List[(S, T)]]) =>
          f1.flatMap(v1 => f2.map(v2 => v1 ++ v2))
      }
  }

  override def receive: Receive = {
    case message@Request(query, _) =>
      log.info(s"received $query")
      val responseMap = filterSuccessfulToMap(childrenClients.map(client => {
        val searchActor = context.actorOf(SearchActor.props(client), client.provider)
        (searchActor ? message)
          .map { case Response(results) => (client.provider, results) }
      })).map(AggregateResponse)
      responseMap pipeTo sender() andThen {
        case _ =>
          log.info("aggregate response")
      }
  }
}

object AggregatingActor {
  def props(childrenClients: Seq[SearchClient], timeout: Timeout = 1 . seconds): Props =
    Props(new AggregatingActor(childrenClients, timeout))
}

