// WebSocketRoutes.scala
import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Flow, Sink, Source}

class MessagingRoutes(implicit system: ActorSystem) {

  def messageFlow: Flow[Message, Message, Any] = Flow[Message].map {
    case TextMessage.Strict(text) =>
      TextMessage(s"Server received: $text")
    case _ =>
      TextMessage("Unsupported message type")
  }

  val routes: Route = path("ws") {
    handleWebSocketMessages(messageFlow)
  }
}
