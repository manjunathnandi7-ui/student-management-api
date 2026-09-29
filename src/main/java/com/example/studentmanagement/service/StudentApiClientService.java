package com.example.studentmanagement.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StudentApiClientService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String API_BASE_URL = "http://localhost:8080/api/students";
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Get all students and format as context for the chatbot
     */
    public String getStudentContext() {
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(API_BASE_URL, List.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> students = response.getBody();
                return formatStudentContext(students);
            }
            return "No students found in the system.";
        } catch (Exception e) {
            log.error("Error fetching student context", e);
            return "Unable to fetch student data at this moment.";
        }
    }

    /**
     * Get a specific student by ID
     */
    public Map<String, Object> getStudentById(Long id) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    API_BASE_URL + "/" + id, 
                    Map.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Error fetching student with ID: {}", id, e);
            return null;
        }
    }

    /**
     * Create a new student
     */
    public Map<String, Object> createStudent(Map<String, Object> studentData) {
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    API_BASE_URL,
                    studentData,
                    Map.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Error creating student", e);
            return null;
        }
    }

    /**
     * Update an existing student
     */
    public Map<String, Object> updateStudent(Long id, Map<String, Object> studentData) {
        try {
            restTemplate.put(
                    API_BASE_URL + "/" + id,
                    studentData
            );
            return getStudentById(id);
        } catch (Exception e) {
            log.error("Error updating student with ID: {}", id, e);
            return null;
        }
    }

    /**
     * Delete a student
     */
    public boolean deleteStudent(Long id) {
        try {
            restTemplate.delete(API_BASE_URL + "/" + id);
            return true;
        } catch (Exception e) {
            log.error("Error deleting student with ID: {}", id, e);
            return false;
        }
    }

    /**
     * Format student data for LLM context
     */
    private String formatStudentContext(List<Map<String, Object>> students) {
        StringBuilder context = new StringBuilder();
        context.append("Total Students: ").append(students.size()).append("\n\n");
        
        for (Map<String, Object> student : students) {
            context.append(String.format(
                    "- ID: %s, Name: %s, Email: %s, Course: %s\n",
                    student.get("id"),
                    student.get("name"),
                    student.get("email"),
                    student.get("course")
            ));
        }
        
        return context.toString();
    }
}