# The Accord - Smart Contact Manager

The Accord is a Spring Boot based smart contact manager for storing, organizing, and managing personal or professional contacts from one clean web dashboard. It supports manual login, Google/GitHub OAuth login, profile images, contact CRUD, feedback/testimonials, and responsive public pages built with Thymeleaf and Tailwind CSS.

## GitHub Description

Smart Contact Manager built with Spring Boot, Thymeleaf, MySQL, OAuth2, Cloudinary, and Tailwind CSS.

## Features

- User registration and login
- Google and GitHub OAuth login
- Email verification support
- Contact create, read, update, delete workflow
- Favorite contacts
- Contact profile images and user profile images
- User dashboard and profile pages
- Feedback/testimonial system
- Responsive home, about, services, contact, login, and register pages
- MySQL database support with a ready `database.sql`
- Optional H2 local profile for quick testing
- Docker Compose support for MySQL and phpMyAdmin

## Tech Stack

- Java 17
- Spring Boot 3.2.5
- Spring MVC
- Spring Security
- Spring Data JPA / Hibernate
- OAuth2 Client
- Thymeleaf
- MySQL
- H2 Database
- Cloudinary
- Tailwind CSS
- Flowbite
- Maven

## Project Structure

```text
scm2.0/
├── src/main/java/com/scm/          # Spring Boot application code
├── src/main/resources/templates/   # Thymeleaf pages
├── src/main/resources/static/      # CSS, JS, images, uploads
├── database.sql                    # Local MySQL database import
├── docker-compose.yml              # MySQL/phpMyAdmin setup
├── pom.xml                         # Maven dependencies
└── LOCAL_SETUP.md                  # Extra local setup notes
```

## Requirements

- Java 17
- Maven
- MySQL Server, or Docker, or the included H2 profile
- Node.js only if you want to work on Tailwind/frontend assets

## Run Locally With MySQL

Import the database:

```powershell
mysql -u root -p < database.sql
```

Default local database settings:

```text
Database: scm20
Host: localhost
Port: 3306
Username: root
Password: root1234
```

Start the app:

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"
mvn spring-boot:run
```

Open:

```text
http://localhost:8080
```

## Run Without MySQL

Use the H2 profile for a quick local demo:

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

## Demo Login

```text
Email: demo@scm.local
Password: admin123
```

## Docker Database Option

Start MySQL and phpMyAdmin:

```powershell
docker compose up -d mysql phpmyadmin
```

phpMyAdmin:

```text
http://localhost:8081
```

Run the app against Docker MySQL:

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"
$env:MYSQL_PORT="3307"
mvn spring-boot:run
```

## OAuth Setup

Create OAuth apps with these callback URLs:

```text
Google: http://localhost:8080/login/oauth2/code/google
GitHub: http://localhost:8080/login/oauth2/code/github
```

Then start the app with your credentials:

```powershell
$env:APP_OAUTH_ENABLED="true"
$env:APP_OAUTH_GOOGLE_CLIENT_ID="your_google_client_id"
$env:APP_OAUTH_GOOGLE_CLIENT_SECRET="your_google_client_secret"
$env:APP_OAUTH_GITHUB_CLIENT_ID="your_github_client_id"
$env:APP_OAUTH_GITHUB_CLIENT_SECRET="your_github_client_secret"
mvn spring-boot:run
```

You can also create a local file named `local-secrets.properties` in the project root. This file is ignored by Git and is automatically loaded by the app:

```properties
APP_OAUTH_ENABLED=true
APP_OAUTH_GOOGLE_CLIENT_ID=your_google_client_id
APP_OAUTH_GOOGLE_CLIENT_SECRET=your_google_client_secret
APP_OAUTH_GITHUB_CLIENT_ID=your_github_client_id
APP_OAUTH_GITHUB_CLIENT_SECRET=your_github_client_secret
```

After downloading the ZIP from GitHub, recreate this file locally if you want OAuth login to work with your own credentials.

## Useful Environment Variables

```text
SERVER_PORT
MYSQL_HOST
MYSQL_PORT
MYSQL_DB
MYSQL_USER
MYSQL_PASSWORD
CLOUDINARY_NAME
CLOUDINARY_API_KEY
CLOUDINARY_API_SECRET
EMAIL_HOST
EMAIL_PORT
EMAIL_USERNAME
EMAIL_PASSWORD
APP_OAUTH_ENABLED
APP_OAUTH_GOOGLE_CLIENT_ID
APP_OAUTH_GOOGLE_CLIENT_SECRET
APP_OAUTH_GITHUB_CLIENT_ID
APP_OAUTH_GITHUB_CLIENT_SECRET
```

## Notes

- Do not commit real OAuth, email, database, or Cloudinary secrets.
- The app uses `spring.jpa.hibernate.ddl-auto=update`, so JPA can keep tables aligned during local development.
- If your project path contains `&`, regular `mvn` is usually safer than the Maven wrapper batch file on Windows.

## Author

Built by Binit Ghosh and team.
