package com.kt.millet.helm.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kt.millet.helm.service.ChatService;

@Service("ChatServiceImpl")
public class ChatServiceImpl implements ChatService {

	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatServiceImpl.class);
	
	
    /**
    * 재고 현황 리스트 조회
    * @param commandMap
    * @return
    * @throws Exception 
    * @throws EgovBizException
    */
	@Override
	public HashMap<String, Object> getStockList(Map<String, Object> commandMap) throws Exception {
		
		 String name = request.get("name") != null ? request.get("name").toString() : "";
		    String description = request.get("description") != null ? request.get("description").toString() : "";
		    String permission = request.get("permission") != null ? request.get("permission").toString() : "";

		    String searchMethod = request.get("search_method") != null ? request.get("search_method").toString() : "hybrid_search";
		    boolean rerankingEnable = request.get("reranking_enable") != null
		            && Boolean.parseBoolean(request.get("reranking_enable").toString());
		    String rerankingProviderName = request.get("reranking_provider_name") != null
		            ? request.get("reranking_provider_name").toString() : "";
		    String rerankingModelName = request.get("reranking_model_name") != null
		            ? request.get("reranking_model_name").toString() : "";
		    int topK = request.get("top_k") != null ? Integer.parseInt(request.get("top_k").toString()) : 15;
		    boolean scoreThresholdEnabled = request.get("score_threshold_enabled") != null
		            && Boolean.parseBoolean(request.get("score_threshold_enabled").toString());
		    double scoreThreshold = request.get("score_threshold") != null
		            ? Double.parseDouble(request.get("score_threshold").toString()) : 0.7;

		    HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_JSON);
		    headers.setBearerAuth(properties.getKey());

		    Map<String, Object> rerankingModel = new HashMap<>();
		    rerankingModel.put("reranking_provider_name", rerankingProviderName);
		    rerankingModel.put("reranking_model_name", rerankingModelName);

		    Map<String, Object> retrievalModel = new HashMap<>();
		    retrievalModel.put("search_method", searchMethod);
		    retrievalModel.put("reranking_enable", rerankingEnable);
		    retrievalModel.put("reranking_model", rerankingModel);
		    retrievalModel.put("top_k", topK);
		    retrievalModel.put("score_threshold_enabled", scoreThresholdEnabled);
		    retrievalModel.put("score_threshold", scoreThreshold);

		    Map<String, Object> body = new HashMap<>();
		    body.put("name", name);
		    body.put("description", description);
		    body.put("permission", permission);
		    body.put("retrieval_model", retrievalModel);

		    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

		    ResponseEntity<Map> result = restTemplate.postForEntity(properties.getUrl(), entity, Map.class);

		    Map<String, Object> apiResponse = result.getBody();

		    Map<String, Object> response = new HashMap<>();
		    response.put("reply", apiResponse != null ? apiResponse.get("data") : null);
		    response.put("raw", apiResponse);

		    return response;	
		
	}
	
	
	
}
