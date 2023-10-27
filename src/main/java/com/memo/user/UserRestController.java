package com.memo.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RestController
public class UserRestController {
	
	@RequestMapping("/is-duplicated-id")
	public Map<String, Object> isDuplicatedId(
			@RequestParam("loginId") String loginId){
			
		//db조회
		// ajax의 구문 다시 홧인하고 map에 디버그 한번걸어보고 다시 해보기 -> 중복의 가능성을 확인하는것
		//응답값 만들고 return(jason으로 돌아간다)
		Map<String, Object>result = new HashMap<>();
		result.put("code", 200);
		result.put("isDuplicated", true);
		return result;
		
	}
}
