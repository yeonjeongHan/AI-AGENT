package com.kt.millet.helm.service;

import java.util.HashMap;
import java.util.Map;

public interface ChatService {
	

    /**
    * 재고현황 리스트 
    * @param commandMap
    * @return
    * @throws Exception 
    */
	HashMap<String, Object> getStockList(Map<String, Object> commandMap) throws Exception;
	
	
	
}
