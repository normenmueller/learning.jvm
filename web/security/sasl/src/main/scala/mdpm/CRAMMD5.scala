package mdpm

import scala.util.{Failure, Success, Try}
import com.typesafe.scalalogging.StrictLogging
import javax.security.auth.callback.{Callback, CallbackHandler, NameCallback, PasswordCallback}
import javax.security.sasl.{AuthorizeCallback, Sasl, SaslClient, SaslServer}

object CRAMMD5 extends App with StrictLogging {
  import collection.JavaConverters._
  // ---------------------------------------------------------------------
  // CLIENT: Send "username" to server, e.g.:
  // {{{
  // GET /challenge with { "username" : "..." }
  // }}}
  val username = "username01"
  // ---------------------------------------------------------------------
  // SERVER: Read response
  // ...
  // SERVER: The server's internal database
  implicit val db = Map(
    "username01" -> cipher("secure!11")
  , "username02" -> cipher("pr@sp4711")
  )
  // SERVER: Create a SASL server and compute challenge
  val ss: SaslServer = Sasl.createSaslServer(
    sasl.server.CRAM.name,
    "mdpm",
    "api.mdpm.de",
    sasl.server.CRAM.properties.asJava,
    new SaslServerHandler(username)
  )
  logger.debug("SASL Server created.")
  val challenge: Array[Byte] = ss.evaluateResponse(Array.emptyByteArray)
  logger.debug(s"SASL Server challenge: ${challenge.mkString}")
  // SERVER: Send challenge to client
  // { "challenge" : "..." }
  // ---------------------------------------------------------------------
  // CLIENT: Create a SASL client and compute response
  val sc: SaslClient = Sasl.createSaslClient(
    Array("CRAM-MD5"),
    null,
    "mdpm",
    "api.mdpm.de",
    sasl.client.CRAM.properties.asJava,
    new SaslClientHandler(username)
  )
  logger.debug("SASL Client created.")
  val response: Array[Byte] = sc.evaluateChallenge(challenge)
  logger.debug(s"SASL Client response: ${response.mkString}")
  // CLIENT Send response to server
  // POST /challenge with { "response" : "..." }
  // ---------------------------------------------------------------------
  // SERVER: Evaluate response
  Try(ss.evaluateResponse(response)) match {
    case Success(result) if result == null && ss.isComplete => logger.debug("Authentication successful.")
    case Failure(error) => logger.error(s"Authentication failed (Due to ${error.getLocalizedMessage})")
  }
}

class SaslServerHandler(username: String)(implicit db: Map[String, String])
  extends CallbackHandler with StrictLogging {

  override def handle(cbs: Array[Callback]): Unit = {
    cbs foreach {
      case cb: AuthorizeCallback =>
        logger.debug("AuthorizeCallback")
        cb.setAuthorized(true)
      case cb: NameCallback =>
        logger.debug(s"Username: $username")
        cb.setName(username)
      case cb: PasswordCallback =>
        logger.debug(s"Password: ${db(username)}")
        cb.setPassword(db(username).toCharArray)
    }
  }

}

class SaslClientHandler(username: String) extends CallbackHandler with StrictLogging {

  override def handle(cbs: Array[Callback]): Unit = {
    cbs foreach {
      case cb: NameCallback =>
        logger.debug(s"Username: $username")
        cb.setName(username)
      case cb: PasswordCallback =>
        // Happy path
        // User enters correct password
        logger.debug(s"Password: secure!11")
        cb.setPassword(cipher("secure!11").toCharArray)

        // Evil path
        // Let's assume user enters wrong password
//        logger.debug(s"Client - PasswordCallback: foobar")
//        cb.setPassword(cipher("foobar").toCharArray)
    }
  }

}
