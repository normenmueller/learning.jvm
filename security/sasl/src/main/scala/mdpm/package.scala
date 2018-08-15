import java.security.MessageDigest

package object mdpm {

  private val md5 = MessageDigest.getInstance("MD5")

  def cipher(s: String): String =
    md5.digest(s.map(_.toByte).toArray).mkString

}
