# Local setup

This project is configured to run with a local MySQL database named `scm20`.

## 1. Import the database

From MySQL Workbench, phpMyAdmin, or the MySQL command line, import:

```powershell
mysql -u root -p < database.sql
```

The `mysql` command is not currently available in this terminal, so use MySQL Workbench/phpMyAdmin or install MySQL Server with the command-line tools.

The default app settings expect:

- host: `localhost`
- port: `3306`
- database: `scm20`
- username: `root`
- password: `root1234`

If your MySQL password is different, start the app with:

```powershell
$env:MYSQL_PASSWORD="your_mysql_password"
mvn spring-boot:run
```

## 2. Run the app

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"
mvn spring-boot:run
```

Then open:

```text
http://localhost:8080
```

## Demo login

- email: `demo@scm.local`
- password: `admin123`

Spring Boot also has `spring.jpa.hibernate.ddl-auto=update`, so it can keep the tables aligned with the JPA entities after import.

Use `mvn` instead of `.\mvnw.cmd` in this folder. The Maven wrapper batch file can break because the project path contains `&`.

## Run immediately without MySQL

If you do not know your local MySQL password yet, run the app with the H2 profile:

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

This creates a small local database under `local-h2/` and seeds the same demo login.

## Docker database option

If you do not want to install MySQL directly, start the database from Docker:

```powershell
docker compose up -d mysql phpmyadmin
```

This maps MySQL to your PC on port `3307` and phpMyAdmin to:

```text
http://localhost:8081
```

For the Spring Boot app to use the Docker MySQL from your PC terminal:

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"
$env:MYSQL_PORT="3307"
mvn spring-boot:run
```

The Docker MySQL service imports `database.sql` automatically when its data folder is created fresh. If `mysql_data` already exists, delete that folder only when you intentionally want to recreate the database.

## Enable Google and GitHub login

OAuth login needs real app credentials from Google and GitHub. Without them, the app hides the social login buttons so users do not hit broken provider pages.

Create OAuth apps with these local callback URLs:

```text
Google: http://localhost:8080/login/oauth2/code/google
GitHub: http://localhost:8080/login/oauth2/code/github
```

Then start the app with:

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"
$env:APP_OAUTH_ENABLED="true"
$env:APP_OAUTH_GOOGLE_CLIENT_ID="your_google_client_id"
$env:APP_OAUTH_GOOGLE_CLIENT_SECRET="your_google_client_secret"
$env:APP_OAUTH_GITHUB_CLIENT_ID="your_github_client_id"
$env:APP_OAUTH_GITHUB_CLIENT_SECRET="your_github_client_secret"
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

For Google, set the authorized JavaScript origin to:

```text
http://localhost:8080
```
