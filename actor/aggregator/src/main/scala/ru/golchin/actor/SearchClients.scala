package ru.golchin.actor

import java.nio.charset.StandardCharsets

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.stream.Materializer
import akka.util.ByteString
import spray.json.DefaultJsonProtocol._
import spray.json._
import spray.json.lenses.JsonLenses._

import scala.concurrent.{ExecutionContextExecutor, Future}

trait SearchClient {
  implicit val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global

  def provider: String

  def search(query: String): Future[Seq[String]]
}

class DefaultSearchClient() extends SearchClient {
  override def provider: String = "default"

  override def search(query: String): Future[Seq[String]] = Future {
    Range(0, 5).map(_.toString)
  }
}

class ThrowingSearchClient extends SearchClient {
  override def provider: String = "throwing"

  override def search(query: String): Future[Seq[String]] = Future {
    throw new NullPointerException()
  }
}

class SlowSearchClient extends SearchClient {
  override def provider: String = "slow"

  override def search(query: String): Future[Seq[String]] = Future {
    Thread.sleep(20000)
    Seq()
  }
}

abstract class WebSearchClient(val provider: String,
                               val baseUrl: String,
                               val params: Map[String, String],
                               val queryParamName: String,
                               implicit val system: ActorSystem,
                               implicit val materializer: Materializer) extends SearchClient {
  def this(provider: String,
           proto: String = "https",
           host: String,
           port: Option[String] = None,
           path: String,
           params: Map[String, String],
           queryParamName: String,
           system: ActorSystem,
           materializer: Materializer) =
    this(provider, s"$proto://$host${
      port map {
        ":" + _
      } getOrElse ""
    }$path", params, queryParamName, system, materializer)

  override implicit val ec: ExecutionContextExecutor = system.dispatcher

  override def search(query: String): Future[Seq[String]] = {
    val uri = Uri(baseUrl).withQuery(Query(params.updated(queryParamName, query)))
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = uri))
    val linkLens = 'items / * / 'link
    responseFuture.flatMap { res =>
      val content = res.entity.dataBytes
        .runFold(ByteString(""))(_ ++ _)
        .map(_.decodeString(StandardCharsets.UTF_8).parseJson)
      content.map(_.extract[String](linkLens))
    }
  }
}

class GoogleSearchClient(system: ActorSystem,
                         materializer: Materializer,
                         proto: String = "https",
                         host: String = "www.googleapis.com",
                         port: Option[String] = None) extends WebSearchClient(
  "google",
  proto,
  host,
  port,
  "/customsearch/v1",
  Map(("key", "AIzaSyAKnyMOV_njBmbPHTUBEpwRZ19iozIF09s"), ("cx", "003006310301426727607:vxajyrymc6u")),
  "q",
  system,
  materializer) {
}
