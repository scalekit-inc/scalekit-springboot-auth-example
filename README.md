## Spring Boot Scalekit Example App

A simple Spring Boot app that shows how to add secure sign-in with Scalekit (OIDC). You can it as a starting point or as a reference to integrate enterprise-grade authentication.

### 🚀 What this example includes

- **OIDC authentication**: Sign in with Scalekit
- **Protected pages**: A dashboard that requires authentication
- **Spring Security setup**: OAuth 2.0 client configuration
- **Responsive UI**: Bootstrap-based layout
- **Token visibility**: Example of showing ID token claims

## 📋 Prerequisites

- **Java 17 or higher**
- **Maven 3.6+**
- **Scalekit account** with an OIDC application

## 🛠️ Quick start

### 1) Clone and open the project

```bash
git clone <your-repo-url>
cd springboot-example
```

### 2) Configure Scalekit

Pick one method below.

Method A — application-local.properties (recommended for local dev):

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

Method B — environment variables:

```bash
export SCALEKIT_ENV_URL=https://your-env.scalekit.io
export SCALEKIT_CLIENT_ID=YOUR_CLIENT_ID
export SCALEKIT_CLIENT_SECRET=YOUR_CLIENT_SECRET
export SCALEKIT_REDIRECT_URI=http://localhost:8080/auth/callback
```

Important:

- Never commit secrets to source control.
- Ensure the redirect URI exactly matches what is configured in Scalekit.

### 3) Build and run

```bash
# Build
mvn clean compile

# Run (default profile)
mvn spring-boot:run

# Or run with the local profile (uses application-local.properties)
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

The application will start at `http://localhost:8080`

## 🔧 Getting your Scalekit configuration

To find your required values:

1. **Sign in to Scalekit**: Visit [Scalekit Dashboard](https://scalekit.com)

2. **Create an application**:

   - Go to the Applications section
   - Click Create Application
   - Choose Web Application

3. **Copy configuration values**:

   - **Environment URL** (e.g., `https://your-env.scalekit.io`)
   - **Client ID**
   - **Client Secret**
   - **Redirect URI**: `http://localhost:8080/auth/callback`

4. **Allowed redirect URIs**:
   - Add `http://localhost:8080/auth/callback`
   - Optionally add `http://localhost:8080` as a post-logout redirect

## 🌐 Application routes

| Route                            | Description                 | Auth required |
| -------------------------------- | --------------------------- | ------------- |
| `/`                              | Home page with login option | No            |
| `/login`                         | Custom login page           | No            |
| `/dashboard`                     | Protected dashboard         | Yes           |
| `/oauth2/authorization/scalekit` | Start the OIDC flow         | No            |
| `/auth/callback`                 | OIDC callback               | No            |
| `/logout`                        | Logout and end session      | Yes           |

## 🚦 Test the app

1. Start the app (see Quick start)
2. Visit `http://localhost:8080`
3. Click Sign in with Scalekit
4. Authenticate with your provider
5. Open the dashboard and then try logout

## 📊 Troubleshooting

### Common issues

**Application will not start**

- Check Java: `java -version` (must be 17+)
- Check Maven: `mvn -version`
- Download dependencies: `mvn dependency:resolve`
- Try a clean run: `mvn clean spring-boot:run`

**Authentication fails**

- Verify env URL, client ID, and client secret
- Ensure the redirect URI matches exactly (including port)
- Check allowed redirect URIs in Scalekit
- Inspect logs (enable DEBUG below)

### Enable debug logging

Add this to `src/main/resources/application.yml`:

```yaml
logging:
  level:
    com.example.scalekit: DEBUG
    org.springframework.security: DEBUG
    org.springframework.security.oauth2: TRACE
```

## 🔍 Code structure

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

## 📦 Dependencies

- Spring Boot
- Spring Security (OAuth 2.0 Client)
- Scalekit SDK
- Thymeleaf
- Bootstrap (via CDN)

See `pom.xml` for exact versions.

## 🤝 Support

For help:

- **This demo**: See Troubleshooting above or open an issue
- **Scalekit**: See the Docs: [Scalekit Documentation](https://docs.scalekit.com)
- **Spring Boot**: See [Spring Boot Documentation](https://spring.io/projects/spring-boot)

## 📄 License

This project is for demonstration and learning. Refer to dependency licenses for production use.
