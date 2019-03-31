package ru.golchin.server

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Sink, Source}
import ru.golchin.db.{ProductRepository, UserRepository}
import ru.golchin.model.{Currency, Product, User}

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait ProductRoutes extends JsonSupport with ProductRepository with UserRepository with ActorSupport {
  val productRoutes: Route =
    pathPrefix("products") {
      concat(
        get {
          path(Segment) { title =>
            parameterMap { params => {
              val user = params.get("user")
              if (user.isEmpty)
                complete("No user specified")
              val productSource = findByTitle(title)
              val userSource = findByLogin(params("user"))
              val eventualProduct: Future[Product] = productSource.zip(userSource)
                .runWith(Sink.head)
                .map { case (product: Product, user: User) =>
                  val convertedCost = Currency.convert(product.cost, user.currency)
                  Product(product.title, convertedCost)
                }
              onComplete(eventualProduct) {
                case Success(v) => complete(v.toString)
                case Failure(ex) => complete(s"An error occurred: ${ex.getMessage}")
              }
            }
            }
          }
        },
        post {
          entity(as[Product]) { product =>
            Source.single(product).runWith(insertProduct)
            complete("inserted " + product.toString)
          }
        }
      )
    }
}
