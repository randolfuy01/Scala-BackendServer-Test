import akka.http.scaladsl.model.StatusCodes._
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

          val status = CassandraConnection.createUser(email, hashedPassword)

          complete(
            status match {
              case 200 => OK -> "User created successfully"
              case 409 => Conflict -> "User already exists"
              case 500 => InternalServerError -> "Error creating user"
            }
          )
        }
      }
    },
    path("login-user") {
      post {
        entity(as[String]) { body =>
          val Array(email, password) = body.split(",").map(_.trim)
          val hashedPassword = hashPassword(password, "SHA-256")

          val status = CassandraConnection.loginUser(email, hashedPassword)

          complete(
            status match {
              case 200 => OK -> "Login successful"
              case 401 => Unauthorized -> "Invalid email or password"
              case 500 => InternalServerError -> "Error during login"
            }
          )
        }
      }
    }
  )
}
