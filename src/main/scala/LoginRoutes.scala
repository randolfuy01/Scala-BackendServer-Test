import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import java.security.MessageDigest

class LoginRoutes {

  def hashPassword(str: String, algo: String): String = {

    val digest = MessageDigest.getInstance(algo)

    val hashBytes = digest.digest(str.getBytes("UTF-8"))

    
    hashBytes.map("%02x".format(_)).mkString
  }

  def routes: Route = concat(
    path("create-user") {
      post {
        entity(as[String]) { body =>
          val Array(email, password) = body.split(",").map(_.trim)
          val hashedPassword = hashPassword(password, "SHA-256")
          complete(s"Email: $email with hashed $hashedPassword")
          
        }
      }
    },
    path("login-user") {
      post {
        entity(as[String]) { body =>
          val Array(email, password) = body.split(",").map(_.trim)
          val hashedPassword = hashPassword(password, "SHA-256")
          complete(s"Login attempt with Email: $email with hashed $hashedPassword")
        }
      }
    }
  )
}