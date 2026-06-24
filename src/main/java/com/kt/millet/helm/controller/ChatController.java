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
    
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateKnowledge(@RequestBody Map<String, Object> request) {
//        try {
//
//    	        Map<String, Object> response = knowledgeService.createKnowledge(request);
//    	        return ResponseEntity.ok(response);
//    	    } catch (Exception e) {
//    	        e.printStackTrace();
//
//    	        Map<String, Object> error = new HashMap<>();
//    	        error.put("success", false);
//    	        error.put("message", e.getMessage());
//
//    	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//    	    }
    }
    

    @PostMapping("/stock")
    public ResponseEntity<Map<String, Object>> getInventoryChart(@RequestBody Map<String, String> request) {
        try {
            String from = request.get("from");
            String to = request.get("to");

            String query = String.format(
                    "조회기간은 %s부터 %s까지입니다. 이 기간의 월별 재고 수량을 JSON 형식으로만 반환해주세요. 형식은 {\"labels\":[\"2026-01\"],\"values\":[100]} 로 맞춰주세요.",
                    from, to
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(properties.getKey());
            
            Map<String, Object> body = new HashMap<>();
            body.put("inputs", new HashMap<>());
            body.put("query", query);
            body.put("response_mode", "blocking");
            body.put("conversation_id", "");
            body.put("user", "test1234");
            body.put("files", new Object[]{});

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> result = restTemplate.postForEntity(properties.getUrl(), entity, String.class);

            // TODO: 실제 응답 형식에 맞게 파싱해야 함
            // 임시 예시로 더미 데이터 반환
            Map<String, Object> response = new HashMap<>();
            response.put("labels", new String[]{from, to});
            response.put("values", new int[]{120, 95});
            response.put("raw", result.getBody());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
