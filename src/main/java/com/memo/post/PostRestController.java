package com.memo.post;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.memo.post.bo.PostBO;

@RequestMapping("/post")
@RestController
public class PostRestController {

	@Autowired
	private PostBO postBO;
	
	/**
	 * 글쓰기 API
	 * @param subject
	 * @param content
	 * @param file
	 * @param session
	 * @return
	 */
	@PostMapping("/create")
	public Map<String, Object> create(
			@RequestParam("subject") String subject,
			@RequestParam("content") String content,
			@RequestParam(value = "file", required = false) MultipartFile file,
			HttpSession session) {// 쿠키랑 세션의 차이를 좀 알아보기
		
		// session에 들어있는 유저 id 꺼낸다.
		int userId = (int)session.getAttribute("userId");
		String userLoginId = (String)session.getAttribute("userLoginId");
		
		// DB insert
		postBO.addPost(userId, userLoginId, subject, content, file);
		
		// 응답값
		Map<String, Object> result = new HashMap<>();
		result.put("code", 200);
		result.put("result", "성공");
		return result;
	}
	/**
	 * 글수정 API
	 * @param postId
	 * @param subject
	 * @param content
	 * @param file
	 * @param session
	 * @return
	 */
	@PutMapping("/update")
	public Map<String, Object> update(
			@RequestParam("postId") int postId,
			@RequestParam("subject") String subject,
			@RequestParam("content") String content,
			@RequestParam(value = "file", required = false) MultipartFile file,
			HttpSession session) {
		
		int userId = (int)session.getAttribute("userId"); // 일괄처리 비일괄인경우 integer로 해야된다
		String userLoginId = (String)session.getAttribute("userLoginId");
		
		// db update
		postBO.updatePost(userId, userLoginId, postId, subject, content, file);
		
		Map<String, Object> result = new HashMap<>();
		result.put("code", 200);
		result.put("result", "성공");
		return result;// 응답값 jason으로 리턴
	}
	
	@DeleteMapping("/delete")
	public Map<String, Object>delete(
			@RequestParam("postId") int postId,
			HttpSession session){
		
		int userId = (int)session.getAttribute("userId"); // 이부분에 디버깅 한번 해서 삭제 하고 postid가 넘어오고 userid가 넘어오는지 확인후 결과처리 확인
		
		//DB삭제
		postBO.deletePostByPostIdUserId(postId, userId);
		//응답값
		Map<String, Object>result = new HashMap<>();
		result.put("code", 200);
		result.put("result", "성공");
		return result;
		//사진이 들어가있는 폴더가 없어졌는지를 확인
	}
}

