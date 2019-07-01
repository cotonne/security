# OpenID

## application.yml

Add:

    spring:
      security:
        oauth2:
          client:
            registration:
              google:
                client-id: <client-id>
                client-secret: <client-secret>

Environment properties can also be used.


## issuer-uri

/.well-known/openid-configuration will be added by org.springframework.security.oauth2.client.registration.ClientRegistrations
From https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfig
OpenID Providers supporting Discovery MUST make a JSON document available at the path formed by
concatenating the string /.well-known/openid-configuration to the Issuer.

# Flow

## Accessing URL as anonymous user

    curl http://localhost:8080/hello
    > GET /login HTTP/1.1
    < http://localhost:8080/oauth2/authorization/google
    
    curl http://localhost:8080/oauth2/authorization/google
    > GET /oauth2/authorization/google HTTP/1.1
    < https://accounts.google.com/o/oauth2/v2/auth?
        response_type=code&
        client_id=<...>&
        scope=openid%20email%20profile&
        state=<A_VALUE>&
        redirect_uri=http://localhost:8080/login/oauth2/code/google

## Authenticate with the provider

 - Authenticate
 - At the end, the provider redirects to the application


    http://localhost:8080/login/oauth2/code/google?
       state=<A_VALUE>&
       code=<CODE_TO_EXCHANGE_AGAINST_A_TOKEN>&
       scope=email+profile+openid+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile&
       authuser=0&
       session_state=XXX&
       prompt=consent


## Coming back to the application

The code is captured by Spring. The callstack is:.
 - org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter.attemptAuthentication
 - org.springframework.security.oauth2.client.oidc.authentication.OidcAuthorizationCodeAuthenticationProvider.authenticate
 - org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient.getTokenResponse

```
> POST https://oauth2.googleapis.com/token
> Accept:"application/json;charset=UTF-8"
> Content-Type:"application/x-www-form-urlencoded;charset=UTF-8"
> Authorization:"Basic <BASE64 ENCODED CLIENT-ID:CLIENT-SECRET>"
> {grant_type=[authorization_code], 
> code=[<CODE_TO_EXCHANGE_AGAINST_A_TOKEN>], 
> redirect_uri=[http://localhost:8080/login/oauth2/code/google
> ]}

< 200
< Content-Type:"application/json; charset=utf-8"
< Vary:"X-Origin", "Referer", "Origin,Accept-Encoding", 
< Date:"Thu, 20 Jun 2019 12:55:06 GMT"
< Server:"scaffolding on HTTPServer2"
< Cache-Control:"private"
< X-XSS-Protection:"0"
< X-Frame-Options:"SAMEORIGIN"
< X-Content-Type-Options:"nosniff"
< {
<  access-token: "<>",
<  id-token: "<>"
< }
```

Contenu de l'id-token:
 - Header: {"alg":"RS256","kid":"6864289ffa51e4e17f14edfaaf5130df40d89e7d","typ":"JWT"}
 * Le kid se retrouve sous : https://www.googleapis.com/oauth2/v3/certs
 - Payload:
```
 {
   "iss": "https://accounts.google.com",
   "azp": "...",
   "aud": "...",
   "sub": "...",
   "email": "...",
   "email_verified": true,
   "at_hash": "...",
   "name": "...",
   "picture": "...",
   "given_name": "...",
   "family_name": "...",
   "locale": "en",
   "iat": 1561035306,
   "exp": 1561038906
 }
```

It uses `org.springframework.security.oauth2.client.oidc.authentication.OidcTokenValidator.validateIdToken` to validate
the token.


		// The Claims requested by the profile, email, address, and phone scope values
		// are returned from the UserInfo Endpoint (as described in Section 5.3.2),
		// when a response_type value is used that results in an Access Token being issued.
		// However, when no Access Token is issued, which is the case for the response_type=id_token,
		// the resulting Claims are returned in the ID Token.
		// The Authorization Code Grant Flow, which is response_type=code, results in an Access Token being issued.


# Microsoft Azure

 - Member of / Directory.Read.All: https://docs.microsoft.com/en-us/graph/api/user-list-memberof?view=graph-rest-1.0&tabs=javascript
 - Exemple : https://docs.microsoft.com/fr-fr/java/azure/spring-framework/configure-spring-boot-starter-java-app-with-azure-active-directory?view=azure-java-stable
 - https://docs.microsoft.com/fr-fr/azure/active-directory/develop/v2-permissions-and-consent#admin-restricted-permissions
 - https://github.com/microsoft/azure-spring-boot/tree/master/azure-spring-boot-starters/azure-active-directory-spring-boot-starter

## on_behalf_of

 - https://github.com/AzureAD/azure-activedirectory-library-for-java/blob/dev/src/main/java/com/microsoft/aad/adal4j/AuthenticationContext.java#L309
 - https://paulryan.com.au/wp-content/uploads/2017/08/onbehalfof-flow.png
 
 
    <dependency>
      <groupId>com.microsoft.azure</groupId>
      <artifactId>azure-spring-boot-starter</artifactId>
    </dependency>

### Get token

 - Assume that the user has been authenticated on an application using the OAuth 2.0 authorization code grant flow. 
 - At this point, the application has an access token for API A (token A) 
     with the user’s claims and consent to access the middle-tier web API (API A).
 - Now, API A needs to make an authenticated request to the downstream web API (API B).

(1) 
 -  cf. https://docs.microsoft.com/fr-fr/azure/active-directory/develop/v2-permissions-and-consent
 - Tous les droits doivent être demandées dès le début
 
Ce mode est plutôt adapté pour les SPA:
 - Le client est la SPA
 - Un backend A est le Resource Server
 - La SPA a un Access Token pour accéder à A
 - A a besoin de connaître les droits de l'utilisateur. 
   A doit donc demander un AccessToken à l'utilisateur

Théoriquement, pour avoir les droits sur Microsoft Graph, 
on devrait refaire la procédure.
 * Le backend A devient le client
 * Microsoft Graph est le Resource Server (B)
 * Il faut donc que A obtient un Access Token pour se connecter à M.G..

A demande un Access Token pour accéder à B. Il fournit l'Access Token qu'il a reçu.

API A => Microsoft identity platform:

    POST https://login.microsoftonline.com/<tenant>/oauth2/v2.0/token?
        grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&
        client_id=<APP-ID>&
        client-secret=<APP-SECRET>&
        assertion=<ACCESS TOKEN pour A>&
        scope=https://graph.microsoft.com/user.read+offline_access& // (1)
        requested_token_use=on_behalf_of
        
Microsoft identity platform => API A:
    Renvoie un Access Token pour se connecter à B (noté Access Token B)
    
API A => B:

    GET /me?api-version=2013-11-08 HTTP/1.1
    Host: graph.windows.net
    Authorization: Bearer <Access Token B>

Storing tokens: https://auth0.com/docs/security/store-tokens


# Threat modeling

Based on [OAuth 2.0 Threat Model and Security Considerations](https://tools.ietf.org/html/rfc6819#section-2.2)

## Assumptions

 * The attacker has full access to the network and may eavesdrop on any communication
 * Two of the three parties involved in the OAuth protocol may
         collude to mount an attack against the 3rd party.  For example,
         the client and authorization server may be under control of an
         attacker and collude to trick a user to gain access to resources.

## Threats / Elements

### Client

 - Stealing client-secret
 - Obtaining Refresh Token or Access Token: tokens should be kept in transient memory
 - Compromised Browser: client applications should not request user credentials
 - Open redirect: 
 
### Server

#### Authorization Endpoint 

 - Phising
 - User untintentionally grants too much scope
 - Malicious client ==> on_behalf_of
 - Open redirect
 
#### Token Endpoint

 - Eavesdropping Access Token
 - Get Access Token from DB
 - Disclosure of Access Token during transmission 
 - Get Access to client secret
 - Brute force of client secret
 
## Threat / Flow

### Authorization Code Grant Flow

