package mdpm
// cf. https://blog.codecentric.de/en/2017/09/jwt-authentication-akka-http/

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.{Directive1, Route}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.pattern._
import com.typesafe.scalalogging.LazyLogging
import io.circe.JsonObject
import java.time.format.{DateTimeFormatter, FormatStyle}
import java.time.{Duration, Instant, ZoneId}
import java.util.Locale
import scala.util.{Failure, Success, Try}

case class Name(salutation: String, first: String, last: String)

object Name {
  import io.circe.{Decoder, Encoder}
  import io.circe.generic.semiauto._
  implicit val decoder: Decoder[Name] = deriveDecoder
  implicit val encoder: Encoder[Name] = deriveEncoder
}

object HttpApi extends LazyLogging {
  import FailFastCirceSupport._
  import io.circe.generic.auto._
  import io.circe.syntax._
  import io.circe.jawn
  import pdi.jwt._

  final val ID = "http-api"

  final case class LoginRequest(username: String, password: String)

  // Token expiry period in days = 1
  private val expiry = 1
  private val key = "changeme"
  private val alg = JwtAlgorithm.HS256
  // https://stackoverflow.com/questions/25229124/format-instant-to-string
  private val formatter = DateTimeFormatter
    .ofLocalizedDateTime( FormatStyle.SHORT )
    .withLocale( Locale.GERMANY )
    .withZone( ZoneId.systemDefault() )

  def routes: Route = login ~ user

  def apply(host: String, port: Int) = Props(new HttpApi(host, port))

  /** We are sending credentials as JSON content. For the sake of simplicity, we will just check whether the
   *  username is "admin" and the password is "admin", too. Regularly, this data should be checked in a separate
   *  service. If the credentials are incorrect, we simply return "unauthorized" HTTP status code. If the credentials
   *  are correct, we respond with "OK" status code and the JWT (the access token). JWT contains two properties in its
   *  claims:
   *  - user – which is just a username
   *  - expiredAt – the period after the JWT will not be valid anymore
   *
   *  The login curl command looks like this:
   *
   *  {{{
   *  curl -i -X POST localhost:8000 -d '{"username": "admin", "password": "admin"}' -H "Content-Type: application/json"
   *  }}}
   */
  private def login: Route = post {
    entity(as[LoginRequest]) {
      case lr @ LoginRequest("admin", "admin") =>
        val now: Instant = Instant.now()
        val exp: Instant = now.plus(Duration.ofDays(expiry))

        // TODO `signup` might just return status `OK` and `Access-Token` Http header
        val con = JsonObject.fromMap(Map("name" -> Name("Mr.", "Normen", "Müller").asJson))
        val claim = JwtClaim(
          content    = con.asJson.noSpaces,
          issuer     = None,
          subject    = Some(lr.username),
          audience   = None,
          expiration = Some(exp.getEpochSecond),
          notBefore  = None,
          issuedAt   = Some(now.getEpochSecond)
        )
        logger.debug(s"Claim:\n${claim.toJson}")
        logger.debug(s"Issues at: ${formatter.format(now)} (${now.getEpochSecond})")
        logger.debug(s"Expire at: ${formatter.format(exp)} (${exp.getEpochSecond})")

        respondWithHeader(RawHeader("Access-Token", JwtCirce.encode(claim, key, alg))) {
          complete(StatusCodes.OK)
        }
      case LoginRequest(_, _) => complete(StatusCodes.Unauthorized)
    }
  }

  /**
   * {{{
   * curl -i localhost:8000 -H "Authorization: <jwt>"
   * }}}
   */
  private def user: Route = get {
    authenticated { claims =>
      complete(s"User ${claims.getOrElse("user", "")} accessed secured content!")
    }
  }

  /** The JWT can be obtained from the “Access-Token” header and saved on client side. Upon each request, we will have
   *  to send the JWT as a value of the “authorization” header in order to access secured resources. Now, we are going
   *  to make a special directive method “authenticated” which will check JWT validity.
   *
   *  The method first tries to obtain value from the "authorization" header. If it fails to obtain that value, it will
   *  simply respond with the status "unauthorized". If it obtains the JWT, first it is going to check whether the token
   *  expired. If the token has expired, it is going to respond with "unauthorized" status code and the "token expired"
   *  message. If the token has not expired, it will check the validity of the token and if it is valid, it will
   *  "provide" claims so that we can use them further (e.g. for authorization). In all other cases, the method will
   *  return the "unauthorized" status code.
   */
  private def authenticated: Directive1[Try[JwtClaim]] =
    optionalHeaderValueByName("Authorization").flatMap {
      case Some(jwt) if isValid(jwt) => provide(claim(jwt))
      case _ => complete(StatusCodes.Unauthorized)
    }

  private def claim(jwt: String): Try[JwtClaim] = JwtCirce.decode(jwt, key, Seq(alg))

  private def isValid(jwt: String): Boolean = claim(jwt) match {
    case Success(c) =>
      logger.debug((c.issuedAt map Instant.ofEpochSecond map (iat => s"Issues at: ${formatter.format(iat)}")).toString)
      logger.debug((c.expiration map Instant.ofEpochSecond map (iat => s"Expire at: ${formatter.format(iat)}")).toString)
      c.isValid
    case Failure(e) => false
  }
}

final class HttpApi private (host: String, port: Int) extends AnyRef with Actor with ActorLogging {
  import HttpApi._
  import context.dispatcher

  private implicit val materializer = ActorMaterializer()

  Http(context.system).bindAndHandle(routes, host, port).pipeTo(self)

  override def receive: Receive = {
    case ServerBinding(address) => log.info("Server successfully bound at {}:{}", address.getHostName, address.getPort)
    case Failure(cause) =>
      log.error("Failed to bind server", cause)
      context.system.terminate()
  }

}
