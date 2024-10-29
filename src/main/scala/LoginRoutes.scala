import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

class LoginRoutes {

  def routes: Route = concat(
    path("create-user") {
      post {
        entity(as[String]) { body =>
          val Array(email, password) = body.split(",").map(_.trim)
          complete(s"Email: $email with password $password")
        }
      }
    },

    path("login-user") {
      post {
        entity(as[String]) { body =>
          val Array(email, password) = body.split(",").map(_.trim)
          complete(s"Login attempt with Email: $email with password $password")
        }
      }
    }
  )
}
