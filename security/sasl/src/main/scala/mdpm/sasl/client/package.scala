package mdpm
package sasl
package object client {
  import javax.security.sasl.Sasl

  // SASL Client Mechanisms
  trait Mechanism {
    def name: String

    def properties: Map[String, String] = config ++ policy

    protected def config: Map[String, String]

    protected def policy: Map[String, String]
  }

  /** CRAM-MD5
   *
   * Configuration properties
   * n/a
   *
   * Callbacks
   * - AuthorizeCallback
   * - NameCallback
   * - PasswordCallback
   *
   * Policy
   * - Sasl.POLICY_NOANONYMOUS
   * - Sasl.POLICY_NOPLAINTEXT
   */
  case object CRAM extends Mechanism {
    override val name: String = "CRAM-MD5"

    override val config: Map[String, String] = Map.empty

    override val policy: Map[String, String] = Map(
      Sasl.POLICY_NOANONYMOUS -> "true",
      Sasl.POLICY_NOPLAINTEXT -> "true"
    )
  }

  /** DIGEST-MD5
   *
   * Configuration properties
   *  - Sasl.QOP
   *  - Sasl.STRENGTH
   *  - Sasl.MAX_BUFFER
   *  - Sasl.SERVER_AUTH
   *  - "javax.security.sasl.sendmaxbuffer"
   *  - "com.sun.security.sasl.digest.cipher"
   */
  case object DIGEST extends Mechanism {
    override val name: String = "DIGEST-MD5"

    override val config: Map[String, String] = Map(
      Sasl.QOP -> "auth",
      Sasl.STRENGTH -> "high",
      Sasl.MAX_BUFFER -> "1024",
      Sasl.SERVER_AUTH -> "no",
      "javax.security.sasl.sendmaxbuffer" -> "1014",
      "com.sun.security.sasl.digest.cipher" -> "MD5"
    )

    override val policy: Map[String, String] = Map(
      Sasl.POLICY_NOANONYMOUS -> "true",
      Sasl.POLICY_NOPLAINTEXT -> "true"
    )
  }

}
