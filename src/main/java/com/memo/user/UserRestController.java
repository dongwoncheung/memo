package com.memo.user;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.memo.common.EncryptUtils;
import com.memo.user.bo.UserBO;
import com.memo.user.entity.UserEntity;

@RequestMapping("/user")
@RestController
public class UserRestController {
	@Autowired
	private UserBO userBO;
	
	/**
	 * 로그인 id 중복확인 api
	 * @param loginId
	 * @return
	 */
	@RequestMapping("/is-duplicated-id")
	public Map<String, Object> isDuplicatedId(
			@RequestParam("loginId") String loginId){
			
		//db조회
		UserEntity user = userBO.getUserEntityByLoginId(loginId);
		// ajax의 구문 다시 홧인하고 map에 디버그 한번걸어보고 다시 해보기 -> 중복의 가능성을 확인하는것
		//응답값 만들고 return(jason으로 돌아간다)
		Map<String, Object>result = new HashMap<>();
		result.put("code", 200);
		if(user == null) {
			//중복아님
			result.put("isDuplicated", false);
		}else {
			// 중복인 상태
			result.put("isDuplicated", true);
		}
		return result;
		
	}
	/**
	 * 회원가입 API
	 * @param loginId
	 * @param password
	 * @param name
	 * @param email
	 * @return
	 */
	//회원가입
	@PostMapping("/sign-up")
	public Map<String, Object> signUp(
			@RequestParam("loginId") String loginId,
			@RequestParam("password") String password,
			@RequestParam("name") String name,
			@RequestParam("email") String email){
		
		//password의 암호화(해싱) 보안이 제일 취약한 방식이다(mb5알고리즘) 다른 알고리즘도 한번 찾아보기 
		String hashedPassword = EncryptUtils.md5(password);
		
		//DBinsert
		Integer id = userBO.addUser(loginId, hashedPassword, name, email);
		//응답값(성공한 여부) 
		Map<String, Object>result = new HashMap<>();
		if(id == null) {
			result.put("code", 500);
			result.put("errorMessage", "회원가입하는데 실패");
			
		}else {
			result.put("code", 200);
			result.put("result", "성공");
		}
		result.put("code", 200);
		result.put("result", "성공");
		return result; // jason으로 return을 내릴거다
	}
	/**
	 * 로그인 API
	 * @param loginId
	 * @param password
	 * @param request
	 * @return
	 */
	@PostMapping("/sign-in")
	public Map<String, Object>signIn(
			@RequestParam("loginId")String loginId,
			@RequestParam("password")String password,
			HttpServletRequest request){ // jason의 id을 꺼내기위해서 추가(HttpServletRequest request)
		//비밀번호를 hashing하기
		String hashedPassword = EncryptUtils.md5(password);
		//db조회(loginId, 해싱된 비밀번호) -> null or 있음 ????
		UserEntity user = userBO.getUserEntityByLoginIdPassword(loginId, hashedPassword);
		
		//응답값
		Map<String, Object> result = new HashMap<>();
		if(user != null) {
			//로그인 처리
			
			HttpSession session = request.getSession();
			session.setAttribute("userId", user.getId());
			session.setAttribute("userName", user.getName());
			session.setAttribute("loginId", user.getLoginId());
			result.put("code", 200);
			result.put("result", "성공");
			 
			
		}else {
			//로그인 불가
			result.put("code", 500);
			result.put("errorMessage", "존재하지않는 사용자입니다.");
		}
		return result;
		
		
		
	}
}
