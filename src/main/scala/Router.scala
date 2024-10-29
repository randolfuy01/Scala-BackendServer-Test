import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.ws.Message
import akka.actor.ActorSystem

class Router {

    implicit val system: ActorSystem = ActorSystem("MyActorSystem")

    private val loginRoutes = new LoginRoutes()
    private val messagingRoutes = new MessagingRoutes()

    val allRoutes: Route = concat(
        loginRoutes.routes,
        messagingRoutes.routes
    )  
}
