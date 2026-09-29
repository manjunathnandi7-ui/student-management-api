# Student Management REST API with AI Chatbot

This project is a Java 21 Spring Boot application that exposes CRUD endpoints for managing students using an H2 in-memory database, now enhanced with an **AI-powered chatbot** using OpenAI's API.

## Features
- Student entity with fields: id, name, email, course
- Layered architecture: controller, service, repository
- H2 database configuration
- Validation support
- CRUD REST endpoints
- **AI Chatbot Integration** with OpenAI GPT-4
- **Multi-turn Conversations** with conversation history
- **Context-aware Responses** using current student database data
- **Conversation Management** with unique conversation IDs

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- OpenAI API Key (get from [OpenAI Platform](https://platform.openai.com))

## Setup

### 1. Clone and Install

```bash
git clone <repository-url>
cd student-management-api
mvn clean install
```

### 2. Configure OpenAI API Key

Set your OpenAI API key as an environment variable:

```bash
# Linux/Mac
export OPENAI_API_KEY=sk-your-actual-api-key-here

# Windows
set OPENAI_API_KEY=sk-your-actual-api-key-here
```

Or update `src/main/resources/application.properties`:

```properties
openai.api-key=sk-your-actual-api-key-here
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Student Management (Original)

- `GET /api/students` - Get all students
- `GET /api/students/{id}` - Get a specific student
- `POST /api/students` - Create a new student
- `PUT /api/students/{id}` - Update a student
- `DELETE /api/students/{id}` - Delete a student

### Chatbot Endpoints (New)

#### 1. Simple Chat

**POST** `/api/chatbot/chat`

Send a message to the chatbot without student database context.

```json
{
  "message": "Tell me about managing student records",
  "conversationId": "optional-unique-id"
}
```

**Response:**

```json
{
  "conversationId": "550e8400-e29b-41d4-a716-446655440000",
  "userMessage": "Tell me about managing student records",
  "botResponse": "I can help you manage student records...",
  "timestamp": "2024-01-15T10:30:00",
  "success": true,
  "error": null
}
```

#### 2. Context-Aware Chat

**POST** `/api/chatbot/chat-with-context`

Send a message to the chatbot with current student database context. The chatbot will have access to all student data.

```json
{
  "message": "How many students are enrolled in Computer Science?",
  "conversationId": "optional-unique-id"
}
```

**Response:**

```json
{
  "conversationId": "550e8400-e29b-41d4-a716-446655440000",
  "userMessage": "How many students are enrolled in Computer Science?",
  "botResponse": "Based on the current database, there are 3 students enrolled in Computer Science...",
  "timestamp": "2024-01-15T10:30:00",
  "success": true,
  "error": null
}
```

#### 3. Clear Conversation

**POST** `/api/chatbot/clear/{conversationId}`

Clear the conversation history for a specific conversation ID.

```bash
curl -X POST http://localhost:8080/api/chatbot/clear/550e8400-e29b-41d4-a716-446655440000
```

**Response:**

```json
"Conversation cleared successfully"
```

#### 4. Check Conversation Status

**GET** `/api/chatbot/exists/{conversationId}`

Check if a conversation with a given ID exists.

```bash
curl http://localhost:8080/api/chatbot/exists/550e8400-e29b-41d4-a716-446655440000
```

**Response:**

```json
true
```

#### 5. Health Check

**GET** `/api/chatbot/health`

```bash
curl http://localhost:8080/api/chatbot/health
```

**Response:**

```json
"Chatbot service is running"
```

## Example Usage

### Create Students

```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice Johnson",
    "email": "alice@example.com",
    "course": "Computer Science"
  }'
```

### Chat with Student Context

```bash
curl -X POST http://localhost:8080/api/chatbot/chat-with-context \
  -H "Content-Type: application/json" \
  -d '{
    "message": "List all students and their courses"
  }'
```

### Multi-turn Conversation

```bash
# First turn
curl -X POST http://localhost:8080/api/chatbot/chat-with-context \
  -H "Content-Type: application/json" \
  -d '{
    "message": "What courses are available?",
    "conversationId": "conv-123"
  }'

# Second turn (using same conversationId)
curl -X POST http://localhost:8080/api/chatbot/chat-with-context \
  -H "Content-Type: application/json" \
  -d '{
    "message": "How many students are in Computer Science?",
    "conversationId": "conv-123"
  }'
```

## Database Console

Open the H2 Console at: `http://localhost:8080/h2-console`

Settings:
- JDBC URL: `jdbc:h2:mem:studentdb`
- Username: `sa`
- Password: (leave blank)

## Configuration

### application.properties

Key configurations:

```properties
# OpenAI API Key
openai.api-key=sk-your-api-key

# H2 Database
spring.datasource.url=jdbc:h2:mem:studentdb

# Logging Level
logging.level.com.example.studentmanagement=DEBUG
```

## Project Structure

```
src/main/java/com/example/studentmanagement/
├── config/
│   ├── OpenAiConfig.java        # OpenAI client configuration
│   └── RestTemplateConfig.java   # REST client configuration
├── controller/
│   ├── ChatbotController.java    # Chatbot REST endpoints
│   └── StudentController.java    # Student CRUD endpoints
├── service/
│   ├── ChatbotService.java       # Chatbot logic & LLM integration
│   └── StudentApiClientService.java  # Internal API client
├── dto/
│   ├── ChatRequest.java          # Chatbot request DTO
│   ├── ChatResponse.java         # Chatbot response DTO
│   └── StudentDto.java           # Student DTO
├── model/
│   └── Student.java              # Student entity
└── repository/
    └── StudentRepository.java     # Student data access
```

## Dependencies

- Spring Boot 3.3.4
- Spring Data JPA
- H2 Database
- OpenAI Java SDK
- Jackson (JSON processing)
- Lombok (reducing boilerplate)
- Validation API

## API Response Status Codes

- `200 OK` - Successful request
- `400 Bad Request` - Invalid request format
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error or OpenAI API error

## Troubleshooting

### OpenAI API Key Error

If you get an "invalid API key" error:
1. Verify your API key is correct
2. Check that the environment variable is set: `echo $OPENAI_API_KEY`
3. Ensure your OpenAI account has sufficient credits

### Connection Refused Errors

If you get connection errors when chatting with context:
1. Ensure the student API is running on port 8080
2. Check that no firewall is blocking localhost communication

### Rate Limiting

OpenAI has rate limits. If you hit them:
1. Wait before making additional requests
2. Check your OpenAI dashboard for usage
3. Consider upgrading your OpenAI plan

## License

MIT License - see LICENSE file for details

## Contributing

Contributions are welcome! Please feel free to submit pull requests.

## Future Enhancements

- [ ] PostgreSQL database support
- [ ] JWT authentication for API
- [ ] Student enrollment API endpoints
- [ ] Grade management
- [ ] Advanced LLM features (embeddings, semantic search)
- [ ] Chat history persistence
- [ ] Web UI for chatbot
- [ ] WebSocket support for real-time chat
- [ ] Multi-language support