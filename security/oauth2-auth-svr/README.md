# OAuth 2.0 Authorization Server

The *authorization server* gets consent from the *resource owner* and issues access tokens to clients for accessing protected resources hosted by a *resource server*.

The *resource server* is implemented [here]().

Note the fact that, smaller API providers may use the same application and URL space for both the authorization server and resource server. For didactic reasons we have --- despite a very small API --- separated the two applications.
