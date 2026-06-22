package com.kt.millet.helm.test_dev;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final AbcLabProperties properties;

    public ChatController(AbcLabProperties properties) {
        this.properties = properties;
    }

    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, Object> request) {
        String message = request.get("message") != null ? request.get("message").toString() : "";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(properties.getKey());

        Map<String, Object> body = new HashMap<>();
        body.put("inputs", new HashMap<>());
        body.put("query", message);
        body.put("response_mode", "blocking");
        body.put("conversation_id", request.getOrDefault("conversation_id", ""));
        body.put("user", request.getOrDefault("user", "test1234"));
        body.put("auto_generate_name", false);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> result = restTemplate.postForEntity(properties.getUrl(), entity, Map.class);

        Map<String, Object> apiResponse = result.getBody();

        Map<String, Object> response = new HashMap<>();
        response.put("reply", apiResponse != null ? apiResponse.get("answer") : null);
        response.put("raw", apiResponse);

        return response;
    }
    
    @PostMapping("/knowledge/update")
    public ResponseEntity<Map<String, Object>> updateKnowledge(@RequestBody Map<String, Object> request) {
        try {
            String datasetId = request.get("dataset_id") != null ? request.get("dataset_id").toString() : "";
            String documentId = request.get("document_id") != null ? request.get("document_id").toString() : "";
            String name = request.get("name") != null ? request.get("name").toString() : "";
            String text = request.get("text") != null ? request.get("text").toString() : "";

            if (datasetId.isEmpty() || documentId.isEmpty() || text.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "dataset_id, document_id, text는 필수입니다.");
                return ResponseEntity.badRequest().body(error);
            }

            String url = properties.getAddUrl()
                    + "/datasets/" + datasetId
                    + "/documents/" + documentId
                    + "/update_by_text";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(properties.getAddKey());

            Map<String, Object> body = new HashMap<>();
            if (!name.isEmpty()) {
                body.put("name", name);
            }
            body.put("text", text);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> result = restTemplate.postForEntity(url, entity, Map.class);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result.getBody());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
