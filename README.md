# StudentHub — Java JDBC Full-Stack Application

A runnable student management application using Java 17, Maven, raw JDBC, MySQL, and a vanilla HTML/CSS/JavaScript frontend.

## Requirements
- JDK 17+
- Maven 3.9+
- MySQL 8+

## 1. Create the database

Start MySQL and run:

```bash
mysql -u root -p < database/schema.sql
```

Or open `database/schema.sql` in MySQL Workbench and execute it.

The database is named `studenthub`.

## 2. MySQL credentials

The default configuration is:

- host: localhost
- port: 3306
- database: studenthub
- user: root
- password: root

If your password differs, use environment variables instead of changing source code.

Windows PowerShell:

```powershell
$env:DB_USER="root"
$env:DB_PASSWORD="your_password"
```

macOS/Linux:

```bash
export DB_USER=root
export DB_PASSWORD=your_password
```

## 3. Run

From the project directory:

```bash
mvn clean compile
mvn exec:java
```

Open:

```text
http://localhost:8080
```

## Features
- Add student
- View/search students
- Update student
- Delete student
- Input validation
- PreparedStatement
- ResultSet
- executeQuery()
- executeUpdate()
- SQLException handling
- try-with-resources
- generated IDs
- REST-style HTTP endpoints

## Architecture

```text
HTML/CSS/JavaScript
        |
        | HTTP
        v
Java HTTP Server
        |
        v
StudentHandler
        |
        v
StudentDao
        |
        | JDBC
        v
MySQL
```

## JDBC interview explanation

> I built a full-stack Student Management System using Java, JDBC and MySQL. The frontend is vanilla HTML, CSS and JavaScript. The Java backend exposes REST-style endpoints using the built-in HTTP server. The DAO layer uses DriverManager to establish a Connection, PreparedStatement for parameterized SQL, ResultSet for SELECT results, executeQuery for reads, executeUpdate for INSERT/UPDATE/DELETE, and try-with-resources for cleanup.

## API

- GET `/api/students`
- GET `/api/students?search=java`
- GET `/api/students/{id}`
- POST `/api/students`
- PUT `/api/students/{id}`
- DELETE `/api/students/{id}`

## Important

This is a learning/demo project using raw JDBC. A production application would normally use a connection pool, authentication/authorization, structured JSON serialization, stronger validation, logging, and a mature web framework.
