# Spring Boot Scalekit OIDC Demo

A comprehensive Spring Boot application demonstrating enterprise authentication using Scalekit's OIDC provider. This demo showcases user authentication, profile management, and secure session handling.

## 🚀 Features

- **OIDC Authentication**: Enterprise-grade authentication using Scalekit
- **User Dashboard**: Protected dashboard with user information
- **Profile Management**: Complete user profile with OIDC claims
- **Secure Sessions**: Spring Security integration with OAuth2
- **Responsive UI**: Bootstrap-based responsive design
- **Token Management**: ID token display and management

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Scalekit account with configured OIDC application

## 🛠️ Setup Instructions

### 1. Clone and Navigate to Project

```bash
git clone <your-repo-url>
cd springboot-scalekit
```

### 2. Configure Scalekit Settings

Create or update `src/main/resources/application-local.properties` with your Scalekit configuration:

```properties
# Replace these values with your actual Scalekit configuration
scalekit.env-url=https://your-env.scalekit.io
scalekit.client-id=your_client_id_here
scalekit.client-secret=your_client_secret_here
scalekit.redirect-uri=http://localhost:8080/auth/callback

# Server Configuration (optional)
server.port=8080
```

### 3. Environment Variables (Alternative)

You can also configure using environment variables:

```bash
export SCALEKIT_ENV_URL=https://your-env.scalekit.io
export SCALEKIT_CLIENT_ID=your_client_id_here
export SCALEKIT_CLIENT_SECRET=your_client_secret_here
export SCALEKIT_REDIRECT_URI=http://localhost:8080/auth/callback
```

### 4. Build and Run

```bash
# Build the application
mvn clean compile

# Run the application
mvn spring-boot:run

# Or run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

The application will start at `http://localhost:8080`

## 🔧 Getting Your Scalekit Configuration

To get your Scalekit configuration values:

1. **Sign up/Login to Scalekit**: Visit [Scalekit Dashboard](https://scalekit.com)

2. **Create an Application**:
   - Navigate to Applications section
   - Click "Create Application"
   - Choose "Web Application" type

3. **Get Configuration Values**:
   - **Environment URL**: Found in your dashboard (e.g., `https://your-env.scalekit.io`)
   - **Client ID**: Available in your application settings
   - **Client Secret**: Generated in your application settings
   - **Redirect URI**: Configure as `http://localhost:8080/auth/callback`

4. **Configure Allowed Redirect URIs**:
   - Add `http://localhost:8080/auth/callback` to allowed redirect URIs
   - Add `http://localhost:8080` to allowed post-logout redirect URIs

## 🌐 Application Routes

| Route | Description | Authentication Required |
|-------|-------------|----------------------|
| `/` | Home page with login option | No |
| `/login` | Custom login page | No |
| `/dashboard` | User dashboard | Yes |
| `/profile` | Complete user profile with claims | Yes |
| `/oauth2/authorization/scalekit` | Initiate OIDC flow | No |
| `/auth/callback` | OIDC callback endpoint | No |
| `/logout` | Logout and session termination | Yes |

## 🔒 Security Features

- **OIDC Authentication**: Standards-compliant OpenID Connect flow
- **Session Management**: Secure session handling with Spring Security
- **CSRF Protection**: Built-in CSRF protection for forms
- **Secure Logout**: Proper session invalidation and cleanup
- **Route Protection**: Automatic protection of authenticated routes

## 🎨 User Interface

The application includes:
- **Responsive Design**: Mobile-friendly Bootstrap UI
- **Navigation**: Dynamic navigation based on authentication state
- **Dashboard**: Clean overview of user information
- **Profile Page**: Detailed view of OIDC claims and token information
- **Token Viewer**: Raw JWT token display with copy functionality

## 🚦 Testing the Application

1. **Start the application** following the setup instructions
2. **Visit** `http://localhost:8080`
3. **Click** "Sign in with Scalekit"
4. **Authenticate** using your configured identity provider
5. **Explore** the dashboard and profile pages
6. **Test logout** functionality

## 📊 Troubleshooting

### Common Issues

**Application won't start:**
- Verify Java 17+ is installed: `java -version`
- Check Maven installation: `mvn -version`
- Ensure all dependencies are downloaded: `mvn dependency:resolve`

**Authentication fails:**
- Verify Scalekit configuration values are correct
- Check that redirect URI matches exactly (including port)
- Confirm client ID and secret are valid
- Review Scalekit application settings

**Pages not loading:**
- Check server port (default: 8080)
- Verify no other applications are using the same port
- Check application logs for errors

### Debug Mode

Enable debug logging by adding to `application.yml`:

```yaml
logging:
  level:
    com.example.scalekit: DEBUG
    org.springframework.security: DEBUG
    org.springframework.security.oauth2: TRACE
```

## 🔍 Code Structure

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
│           └── profile.html                # User profile
└── test/                                   # Test files
```

## 📦 Dependencies

- **Spring Boot 3.2.0**: Application framework
- **Spring Security**: Authentication and authorization
- **Spring OAuth2 Client**: OIDC client implementation
- **Scalekit SDK 2.0.4**: Scalekit integration
- **Thymeleaf**: Template engine
- **Bootstrap 5.1.3**: UI framework (via CDN)

## 🤝 Support

For issues related to:
- **This demo application**: Check the troubleshooting section or create an issue
- **Scalekit service**: Visit [Scalekit Documentation](https://docs.scalekit.com)
- **Spring Boot**: Check [Spring Boot Documentation](https://spring.io/projects/spring-boot)

## 📄 License

This project is provided as a demonstration and learning resource. Please refer to individual dependency licenses for production use.