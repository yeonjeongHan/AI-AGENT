package com.kt.millet.helm.test_dev;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
}
