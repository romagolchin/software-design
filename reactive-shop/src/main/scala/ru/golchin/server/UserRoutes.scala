package ru.golchin.server

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Source
import ru.golchin.db.UserRepository
import ru.golchin.model.User


trait UserRoutes extends JsonSupport with UserRepository with ActorSupport {
  val userRoutes: Route =
    path("users") {
      post {
        entity(as[User]) { user =>
          Source.single(user).runWith(insertUser)
          complete("inserted " + user)
        }
      }
    }
}
