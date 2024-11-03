import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Flow
import scala.concurrent.Future
import spray.json._
import DefaultJsonProtocol._
import java.util.UUID

class MessagingRoutes(implicit system: ActorSystem) {

  import ParsedMessageJsonProtocol._
  import system.dispatcher
  import scala.concurrent.duration._

  def messageFlow: Flow[Message, Message, Any] = Flow[Message].mapAsync(1) {
    case tm: TextMessage =>
      tm.toStrict(5.seconds).flatMap { strictMessage =>
        val text = strictMessage.text
        val parseResult = parseTextToMessage(text)
        parseResult match {
          case Some(parsedMessage) =>
            Future {
              val status = CassandraConnection.messaging(
                parsedMessage.chat,
                parsedMessage.sender,
                parsedMessage.message
              )
              if (status == 200) TextMessage("Message successfully sent.")
              else TextMessage("Failed to send message.")
            }
          case None =>
            Future.successful(TextMessage("Invalid message format."))
        }
      }
    case _ =>
      Future.successful(TextMessage("Unsupported message type"))
  }

  def routes: Route = concat(
    path("chat") {
      handleWebSocketMessages(messageFlow)
    }
  )

  private def parseTextToMessage(text: String): Option[ParsedMessage] = {
    try {
      Some(text.parseJson.convertTo[ParsedMessage])
    } catch {
      case e: Exception =>
        println(s"Failed to parse message: ${e.getMessage}")
        None
    }
  }
}

case class ParsedMessage(chat: UUID, sender: String, message: String)

object ParsedMessageJsonProtocol extends DefaultJsonProtocol {
  // Add an implicit JsonFormat for UUID
  implicit val uuidFormat: JsonFormat[UUID] = new JsonFormat[UUID] {
    def write(uuid: UUID): JsValue = JsString(uuid.toString)
    def read(json: JsValue): UUID = json match {
      case JsString(s) => UUID.fromString(s)
      case _           => deserializationError("Expected UUID as JsString")
    }
  }

  implicit val parsedMessageFormat: RootJsonFormat[ParsedMessage] = jsonFormat3(
    ParsedMessage.apply
  )
}
