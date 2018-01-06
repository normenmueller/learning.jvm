package mdpm
package oauth

import org.joda.time.{ DateTime, DateTimeZone }

case class User(
  id:                       String               = java.util.UUID.randomUUID().toString,
  created_at:               Option[DateTime]     = Some(DateTime.now(DateTimeZone.UTC)),
  created_by:               Option[String]       = None,
  email:                    String,
  password:                 String,
  first_name:               Option[String]       = None,
  last_name:                Option[String]       = None,
  deleted_at:               Option[DateTime]     = None
)

/* First we need to identify client application that are allowed to use OAuth2 with our API. Not anybody can connect
 * with our API: we want to validate third party apps to offer only trusted ones. This is similar to Facebook asking
 * you to register your app in their site for developers. */
case class OAuthApp(
  id:            String           = java.util.UUID.randomUUID().toString,
  created_at:    DateTime         = DateTime.now(DateTimeZone.UTC),
  deleted_at:    Option[DateTime] = None,
  client_id:     String           = java.util.UUID.randomUUID().toString,
  client_secret: String           = java.util.UUID.randomUUID().toString,
  name:          String,

  // the redirect_url may be passed as a parameters by the client or put in the config
  redirect_url:  Option[String]   = None
)

/* Store token and code to manage the OAuth 2.0 dance */
case class OAuthToken(
  id:            String           = java.util.UUID.randomUUID().toString,
  created_at:    DateTime         = DateTime.now(DateTimeZone.UTC),
  deleted_at:    Option[DateTime] = Some(DateTime.now(DateTimeZone.UTC).plusDays(1)),
  user_id:       String,
  client_id:     String,
  access_token:  String           = java.util.UUID.randomUUID().toString,
  refresh_token: String           = java.util.UUID.randomUUID().toString,
  code:          Option[String]   = Some(java.util.UUID.randomUUID().toString),
  scope:         Option[String]   = None
)
