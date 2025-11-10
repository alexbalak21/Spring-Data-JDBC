# 🚀 Spring Boot Docker Deployment Guide

This guide walks you through building a Spring Boot application, containerizing it with Docker, pushing it to Docker Hub, and deploying it on [Render](https://render.com).

## 📦 1. Build Your Spring Boot App (Maven)

```bash
./mvnw clean package
```

This generates a `.jar` file in the `target/` directory, for example:

```
target/app-0.0.1-SNAPSHOT.jar
```

## 🐳 2. Create a Dockerfile

Create a `Dockerfile` in your project root with the following content:

```dockerfile
FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/app-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

> **Note:** Replace `app-0.0.1-SNAPSHOT.jar` with your actual JAR filename if different.

## 🔨 3. Build the Docker Image

```bash
docker build -t yourusername/app:latest .
```

Example:

```bash
docker build -t alexbalak/app:latest .
```

## 🔐 4. Log in to Docker Hub

```bash
docker login
```

Enter your Docker Hub credentials when prompted.

## 📤 5. Push the Image to Docker Hub

```bash
docker push alexbalak/app:latest
```

## 🌐 6. Deploy on Render

1. Go to [Render Dashboard](https://dashboard.render.com)
2. Click **"New" → "Web Service"**
3. Choose **"Deploy an existing image from a Docker registry"**
4. Fill in the form:

   | Field | Value |
   |------------------|---------------------------------------|
   | Service Name | `spring-app` (or any name) |
   | Environment | `Docker` |
   | Docker Image | `docker.io/alexbalak/app:latest` |
   | Port | `8080` (or your app's port) |
   | Start Command | Leave blank (uses Dockerfile ENTRYPOINT) |

5. Click **"Create Web Service"**

## 🔁 Updating Your App

After making changes:

```bash
mvn clean package
docker build -t alexbalak/app:latest .
docker push alexbalak/app:latest
```

Render will redeploy automatically if auto-deploy is enabled, or you can trigger it manually.

## ✅ Optional Enhancements

- Add environment variables in Render settings
- Connect to a database (PostgreSQL, MySQL, etc.)
- Use GitHub Actions to automate builds and pushes