import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

class Router {
    private val loginRoutes = new LoginRoutes()

    val allRoutes: Route = concat(
        loginRoutes.routes
    )  
}
