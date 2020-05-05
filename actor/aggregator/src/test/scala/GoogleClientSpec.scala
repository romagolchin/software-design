import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.testkit.TestKit
import org.scalatest.Matchers
import org.mockserver.integration.ClientAndServer
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, WordSpecLike}
import org.scalatest.mockito.MockitoSugar
import org.mockserver.integration.ClientAndServer._
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpRequest._
import org.mockserver.model.HttpResponse._
import org.scalactic.source.Position
import ru.golchin.actor.GoogleSearchClient

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}


class GoogleClientSpec(_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with WordSpecLike
    with BeforeAndAfter
    with BeforeAndAfterAll
    with MockitoSugar {

  val port = 8080

  val path = "/customsearch/v1"

  val getRequest: HttpRequest = request()
    .withMethod("GET")
    .withPath(path)

  var mockServer: ClientAndServer = startClientAndServer(port)


  val client = new GoogleSearchClient(system, ActorMaterializer(), "http", "localhost", Some(port.toString))

  implicit val ec: ExecutionContext = system.dispatcher

  def this() = this(ActorSystem("ActorsSpec"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  "GoogleClient" should {
    "succeed on ok" in {
      mockServer.when(getRequest).respond(
        response()
          .withStatusCode(200)
          .withBody(
            """
              |{"items": [{"link": "result"}]}
            """.stripMargin)
      )

      client.search("q") onComplete {
        case Success(v) => v shouldBe Seq("result")
        case Failure(exception) => throw exception
      }
    }
  }
  it should {
    "fail on not found" in {
      mockServer.when(getRequest).respond(
        response()
          .withStatusCode(404)
      )
      client.search("q") onComplete {
        case Success(_) => fail()
      }
    }
  }

  override protected def before(fun: => Any)(implicit pos: Position): Unit = {
    mockServer = startClientAndServer(port)
  }

  override protected def after(fun: => Any)(implicit pos: Position): Unit = mockServer.stop()
}
