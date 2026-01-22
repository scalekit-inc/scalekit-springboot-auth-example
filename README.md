## Spring Boot Scalekit Authentication Example

A simple Spring Boot app that shows how to add secure sign-in with Scalekit (OIDC). You can it as a starting point or as a reference to integrate enterprise-grade authentication.

What this example includes:

- The app signs users in with Scalekit using the OpenID Connect (OIDC) authorization flow.
- The `/dashboard` page is protected by Spring Security and redirects unauthenticated users to the login flow.
- The security configuration shows how to register an OAuth 2.0 client and wire login, callback, and logout endpoints.
- The Thymeleaf templates use Bootstrap classes so pages render well on desktop and mobile.
- After login, the dashboard displays selected ID token claims to demonstrate how to access user information.

### Prerequisites

- Java 17 or later is installed.
- Maven 3.6 or later is installed.
- You have a Scalekit account with an OIDC application. [Sign up](https://app.scalekit.com/)

## 🛠️ Quick start

### Configure Scalekit

Pick one method below.

_Method A_ — application-local.properties (recommended for local dev):

Create or update `src/main/resources/application-local.properties`:

```properties
# Replace placeholders with your values
scalekit.env-url=https://your-env.scalekit.io
scalekit.client-id=YOUR_CLIENT_ID
scalekit.client-secret=YOUR_CLIENT_SECRET
scalekit.redirect-uri=http://localhost:8080/auth/callback

# Optional server config
server.port=8080
```

_Method B_ — environment variables:

```bash
export SCALEKIT_ENV_URL=https://your-env.scalekit.io
export SCALEKIT_CLIENT_ID=YOUR_CLIENT_ID
export SCALEKIT_CLIENT_SECRET=YOUR_CLIENT_SECRET
export SCALEKIT_REDIRECT_URI=http://localhost:8080/auth/callback
```

Important:

- Never commit secrets to source control.
- Ensure the redirect URI exactly matches what is configured in Scalekit.

### Build and run

```bash
# Build
mvn clean compile

# Run (default profile)
mvn spring-boot:run

# Or run with the local profile (uses application-local.properties)
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

The application will start at `http://localhost:8080`

### Setup Scalekit

To find your required values:

1.  Visit [Scalekit Dashboard](https://scalekit.com) and proceed to _Settings_

2.  Copy the API credentails

    - **Environment URL** (e.g., `https://your-env.scalekit.dev`)
    - **Client ID**
    - **Client Secret**

3.  Authentication > Redirect URLs > Allowed redirect URIs:
    - Add `http://localhost:8080/auth/callback`
    - Optionally add `http://localhost:8080` as a post-logout redirect

### Application routes

| Route                            | Description                 | Auth required |
| -------------------------------- | --------------------------- | ------------- |
| `/`                              | Home page with login option | No            |
| `/login`                         | Custom login page           | No            |
| `/dashboard`                     | Protected dashboard         | Yes           |
| `/oauth2/authorization/scalekit` | Start the OIDC flow         | No            |
| `/auth/callback`                 | OIDC callback               | No            |
| `/logout`                        | Logout and end session      | Yes           |

### 🚦 Try the app

1. Start the app (see Quick start)
2. Visit `http://localhost:8080`
3. Click Sign in with Scalekit
4. Authenticate with your provider
5. Open the dashboard and then try logout

Stuck? [Contact us](https://docs.scalekit.com/support/contact-us/).

#### HTTP timeouts configuration

Spring Security's OAuth2 resource server fetches the JWKS from the issuer URL during JWT validation. If the issuer is unreachable, authentication requests will time out.

Use the Spring Security JWT timeout settings for your Spring Boot version. Follow the recommended configuration in the Spring docs: [JWT decoder timeouts](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html#oauth2resourceserver-jwt-timeouts)

#### Enable debug logging

Add this to `src/main/resources/application.yml`:

```yaml
logging:
  level:
    com.example.scalekit: DEBUG
    org.springframework.security: DEBUG
    org.springframework.security.oauth2: TRACE
```

#### Code structure

```
src/
├── main/
│   ├── java/com/example/scalekit/
│   │   ├── ScalekitDemoApplication.java    # Main application class
│   │   ├── config/
│   │   │   ├── ScalekitConfig.java         # Scalekit client configuration
│   │   │   └── SecurityConfig.java         # Spring Security configuration
│   │   └── controller/
│   │       ├── HomeController.java         # Home and login endpoints
│   │       └── DashboardController.java    # Protected endpoints
│   └── resources/
│       ├── application.yml                 # Main configuration
│       ├── application-local.properties    # Local configuration
│       └── templates/                      # Thymeleaf templates
│           ├── index.html                  # Home page
│           ├── login.html                  # Login page
│           ├── dashboard.html              # User dashboard
│           └── layout.html                 # Base layout
└── test/                                   # Test files
```

#### Dependencies

- Spring Boot
- Spring Security (OAuth 2.0 Client)
- Scalekit SDK
- Thymeleaf
- Bootstrap (via CDN)

See `pom.xml` for exact versions.

#### Support

- Read the Scalekit docs: [Documentation](https://docs.scalekit.com).
- Read the Spring Boot docs: [Documentation](https://spring.io/projects/spring-boot).

#### License 📄

This project is for demonstration and learning. Refer to dependency licenses for production use.
