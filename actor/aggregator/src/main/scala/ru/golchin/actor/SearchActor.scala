package ru.golchin.actor

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.pipe

class SearchActor(val client: SearchClient) extends Actor with ActorLogging {
  val engine: String = client.provider

  import context.dispatcher

  override def receive: Receive = {
    case Request(query, limit) =>
      log.info(s"replying to query $query with provider $engine")

      client.search(query).map(r => Response(r.take(limit))) pipeTo sender()
  }
}

object SearchActor {
  def props(searchClient: SearchClient): Props =
    Props(new SearchActor(searchClient))
}
