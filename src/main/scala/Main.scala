// Main.scala
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import scala.io.StdIn
import scala.concurrent.ExecutionContextExecutor

object Main {
  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem = ActorSystem("my-actor-system")
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val loginRoutes = new LoginRoutes()
    val route: Route = loginRoutes.routes

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println("Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()

    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => {
        system.terminate()
        println("Server stopped.")
      })
  }
}
