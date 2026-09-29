# Student Management REST API - Render Deployment Configuration

This file configures the application for deployment on Render.com

## Render.com Deployment Steps

### 1. Create a Web Service on Render

1. Go to [render.com](https://render.com)
2. Click "New +" → "Web Service"
3. Connect your GitHub repository: `myllm135795-eng/student-management-api`

### 2. Configure the Web Service

#### Build & Deploy Settings:

- **Name:** `student-management-api` (or your preferred name)
- **Environment:** `Docker`
- **Region:** Select closest to your location
- **Branch:** `main` (or `feature/ai-chatbot` for testing)

#### Auto-Deploy:
- Check "Auto-deploy new pushes" for continuous deployment

### 3. Set Environment Variables

In Render dashboard under "Environment":

```
OPENAI_API_KEY=sk-your-openai-api-key
```

### 4. Deploy

Click "Create Web Service" and Render will:
1. ✅ Build the Docker image
2. ✅ Run `docker build .` 
3. ✅ Deploy the container
4. ✅ Assign a public URL

---

## What Was Changed

### `application.properties`
- Changed `server.port=8080` to `server.port=${PORT:8080}`
- This allows the app to listen on Render's dynamically assigned PORT

### `Dockerfile`
- Uses `eclipse-temurin:21-jre-alpine` (lightweight Java 21)
- Copies the built JAR file
- Runs with `--server.port=${PORT:8080}`

### `.env.example`
- Template for environment variables
- Reference for local development setup

---

## Local Testing

Before deploying, test locally:

```bash
# Set environment variable
export OPENAI_API_KEY=sk-your-key

# Build
mvn clean package -DskipTests

# Run
java -jar target/student-management-api-0.0.1-SNAPSHOT.jar
```

Access: http://localhost:8080

---

## Troubleshooting

### "Port already in use"
- Change PORT environment variable: `export PORT=9000`
- Then run: `java -jar target/student-management-api-0.0.1-SNAPSHOT.jar`

### "OPENAI_API_KEY not set"
1. Check Render dashboard → Environment Variables
2. Verify the key is set correctly
3. Restart the service

### "Connection refused" from UI
- Update React UI to point to your Render URL
- In `student-management-ui`, set environment variable:
  ```
  REACT_APP_API_URL=https://your-app.onrender.com/api
  ```

---

## Docker Build & Test Locally

```bash
# Build Docker image
docker build -t student-management-api .

# Run locally
docker run -p 8080:8080 \
  -e OPENAI_API_KEY=sk-your-key \
  -e PORT=8080 \
  student-management-api
```

---

## Deployment URLs

After deployment on Render, your app will be available at:
```
https://your-app-name.onrender.com
```

API Endpoints:
- Students: `https://your-app-name.onrender.com/api/students`
- Chatbot: `https://your-app-name.onrender.com/api/chatbot/chat`
- H2 Console: `https://your-app-name.onrender.com/h2-console`

---

## Next Steps

1. Deploy backend to Render (this guide)
2. Deploy frontend (React UI) to Vercel/Render
3. Update React UI API URL to point to deployed backend
4. Test the full stack integration

---

For more info, see main [README.md](./README.md)
