# AI Chatbot Integration Guide

## Overview

The Student Management API now includes an AI-powered chatbot that can:
- Answer questions about student management
- Provide information about enrolled students
- Assist with student-related inquiries
- Maintain multi-turn conversations
- Access real-time student database information

## Architecture

### Components

1. **ChatbotController** - REST API endpoints for chat interactions
2. **ChatbotService** - Core chatbot logic and LLM integration
3. **StudentApiClientService** - Internal client for accessing student data
4. **OpenAiConfig** - OpenAI client configuration
5. **DTOs** - Data transfer objects for requests/responses

### Flow Diagram

```
User Request
    ↓
ChatbotController
    ↓
ChatbotService
    ├→ StudentApiClientService (fetch context)
    └→ OpenAIClient (process with LLM)
    ↓
ChatResponse
```

## Key Features

### 1. Stateful Conversations

Conversations maintain history using unique conversation IDs:

```java
// First message
POST /api/chatbot/chat-with-context
{
  "message": "What students do we have?",
  "conversationId": "conv-001"
}

// Follow-up (maintains context)
POST /api/chatbot/chat-with-context
{
  "message": "Tell me more about the first one",
  "conversationId": "conv-001"
}
```

The chatbot remembers previous messages in the conversation.

### 2. Context-Aware Responses

Two endpoints available:

- **`/api/chatbot/chat`** - General chat without database context
- **`/api/chatbot/chat-with-context`** - Chat with current student data

### 3. Message History

Stored in-memory per conversation:

```java
private final Map<String, List<ChatCompletionMessage>> conversationHistory
```

## Implementation Details

### ChatbotService

**System Prompt:**
```
You are a helpful student management assistant. You can help students 
inquire about their information, courses, and enrollment status. You have 
access to a student management system API. When users ask about students, 
use the information available to provide accurate responses. Always be 
professional and helpful.
```

**Model Configuration:**
- Model: `gpt-4o-mini` (latest GPT-4 mini)
- Max Tokens: 500
- Temperature: 0.7 (balanced creativity and consistency)

### StudentApiClientService

Provides internal methods:

```java
getStudentContext()          // Fetch all students for context
getStudentById(Long id)      // Get specific student
createStudent(Map)           // Create new student
updateStudent(Long id, Map)  // Update student
deleteStudent(Long id)       // Delete student
```

## Usage Examples

### Example 1: Simple Query

```bash
curl -X POST http://localhost:8080/api/chatbot/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "How do I enroll a student?"
  }'
```

**Response:**
```json
{
  "conversationId": "uuid-string",
  "userMessage": "How do I enroll a student?",
  "botResponse": "To enroll a student, you can use the POST /api/students endpoint with the following JSON body...",
  "timestamp": "2024-01-15T10:30:00",
  "success": true
}
```

### Example 2: Database Query with Context

```bash
curl -X POST http://localhost:8080/api/chatbot/chat-with-context \
  -H "Content-Type: application/json" \
  -d '{
    "message": "How many students are in the system?"
  }'
```

**Response:**
```json
{
  "conversationId": "uuid-string",
  "userMessage": "How many students are in the system?",
  "botResponse": "There are currently 5 students in the system, enrolled in various courses including Computer Science, Engineering, and Business Administration.",
  "timestamp": "2024-01-15T10:30:00",
  "success": true
}
```

### Example 3: Multi-turn Conversation

```bash
# Turn 1
curl -X POST http://localhost:8080/api/chatbot/chat-with-context \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Who studies Computer Science?",
    "conversationId": "my-conv-1"
  }'

# Response: Lists students in Computer Science

# Turn 2
curl -X POST http://localhost:8080/api/chatbot/chat-with-context \
  -H "Content-Type: application/json" \
  -d '{
    "message": "What's their average GPA?",
    "conversationId": "my-conv-1"
  }'

# Response: References previous context about CS students
```

## Error Handling

### Validation Errors

```json
{
  "success": false,
  "error": "Invalid request: Message cannot be blank"
}
```

### API Errors

```json
{
  "success": false,
  "error": "Error: 401 Unauthorized - Invalid API key"
}
```

### Network Errors

```json
{
  "success": false,
  "error": "Connection refused: Unable to reach student API"
}
```

## Performance Considerations

### In-Memory Storage

Conversation history is stored in-memory:
- **Pros**: Fast access, no database overhead
- **Cons**: Lost on server restart, memory-limited

### API Calls

Each context-aware request:
1. Fetches all students from database
2. Formats data for LLM prompt
3. Calls OpenAI API
4. Returns response

**Optimization Tips:**
- Cache student data if queried frequently
- Implement pagination for large student lists
- Use `/api/chatbot/chat` for non-database queries

## Security Considerations

1. **API Key Management**
   - Never commit API keys to repository
   - Use environment variables
   - Consider using Spring Vault for production

2. **Input Validation**
   - Messages are validated with `@NotBlank`
   - Invalid requests return 400 Bad Request

3. **Rate Limiting**
   - Implement rate limiting in production
   - Monitor OpenAI usage and costs

## Testing the Chatbot

### Unit Tests

```java
@Test
void testChatWithValidMessage() {
    ChatRequest request = new ChatRequest();
    request.setMessage("Test message");
    
    ChatResponse response = chatbotService.chat(request);
    
    assertTrue(response.isSuccess());
    assertNotNull(response.getBotResponse());
}
```

### Integration Tests

```java
@Test
void testChatEndpoint() throws Exception {
    ChatRequest request = new ChatRequest();
    request.setMessage("Hello");
    
    mockMvc.perform(post("/api/chatbot/chat")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
}
```

## Future Enhancements

### Short Term
- [ ] Add conversation history persistence to database
- [ ] Implement conversation pagination
- [ ] Add message feedback/rating
- [ ] Implement conversation search

### Medium Term
- [ ] Support for document uploads
- [ ] Custom fine-tuning for student management domain
- [ ] Multi-language support
- [ ] Voice input/output

### Long Term
- [ ] GraphQL API for chatbot
- [ ] WebSocket for real-time chat
- [ ] Mobile app integration
- [ ] Advanced NLP for complex queries

## Troubleshooting

### Issue: "Invalid API Key"
**Solution:**
1. Verify API key in environment variables
2. Check OpenAI account status and credits
3. Regenerate API key if necessary

### Issue: "Connection Refused" from StudentApiClientService
**Solution:**
1. Ensure student API is running on port 8080
2. Check firewall settings
3. Verify database is accessible

### Issue: Slow Responses
**Solution:**
1. Check OpenAI API status
2. Reduce max tokens if not needed
3. Cache frequently requested data

## Resources

- [OpenAI API Documentation](https://platform.openai.com/docs)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [GPT Best Practices](https://platform.openai.com/docs/guides/gpt-best-practices)