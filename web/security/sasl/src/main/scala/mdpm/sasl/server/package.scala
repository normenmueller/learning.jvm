package mdpm
package sasl
package object server {
  import javax.security.sasl.Sasl

  // SASL Server Mechanisms
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
   *  - "javax.security.sasl.sendmaxbuffer"
   *  - "com.sun.security.sasl.digest.realm"
   *  - "com.sun.security.sasl.digest.utf8"
   */
  case object DIGEST extends Mechanism {
    override val name: String = "DIGEST-MD5"

    override val config: Map[String, String] = Map(
      Sasl.QOP -> "auth",
      Sasl.STRENGTH -> "high",
      Sasl.MAX_BUFFER -> "XXX",
      "javax.security.sasl.sendmaxbuffer" -> "XXX",
      "com.sun.security.sasl.digest.realm" -> "XXX",
      "com.sun.security.sasl.digest.utf8" -> "XXX"
    )

    override val policy: Map[String, String] = Map(
      Sasl.POLICY_NOANONYMOUS -> "true",
      Sasl.POLICY_NOPLAINTEXT -> "true"
    )
  }

}
