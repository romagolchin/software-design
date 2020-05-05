import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.TestKit
import akka.util.Timeout
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import ru.golchin.actor._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future._
import scala.concurrent.duration._
import scala.util.{Failure, Success}


class ActorsSpec(_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with WordSpecLike
    with BeforeAndAfterAll
    with MockitoSugar {


  def this() = this(ActorSystem("ActorsSpec"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  implicit val timeout: Timeout = 2.seconds
  implicit val ec: ExecutionContext = system.dispatcher

  val normalClient: SearchClient = mock[SearchClient]
  when(normalClient.provider) thenReturn "normal"


  "SearchActor" should {
    "limit results" in {
      when(normalClient.search(any())) thenReturn successful(Seq("1", "2"))

      val searchActor = system.actorOf(SearchActor.props(normalClient))
      searchActor ? Request("q", 1) onComplete {
        case Success(v) => v shouldBe Response(Seq("1"))
        case Failure(_) => fail()
      }
    }
  }

  "AggregatingActor" should {
    "succeed regardless of its children success" in {
      when(normalClient.search(any())) thenReturn successful(Seq("result"))

      val failingClient = mock[SearchClient]
      when(failingClient.provider) thenReturn "failing"
      when(failingClient.search(any())) thenReturn failed(new NullPointerException)

      val aggregatingActor = system.actorOf(AggregatingActor.props(Seq(normalClient, failingClient)))

      aggregatingActor ? Request("q", 10) onComplete {
        case Success(v) => v shouldBe AggregateResponse(Map("normal" -> Seq("result")))
        case Failure(_) => fail()
      }
    }
  }

}
