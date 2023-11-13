package com.memo.post;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.memo.post.bo.PostBO;
import com.memo.post.domain.Post;

@RequestMapping("/post")
@Controller
public class PostController {
	
	@Autowired
	private PostBO postBO;

	@GetMapping("/post-list-view")
	public String postListView(
			@RequestParam(value ="prevId", required = false)Integer prevIdParam,
			@RequestParam(value ="nextId", required = false)Integer nextIdParam,
			Model model, 
			HttpSession session) {
		// 로그인 여부 조회
		Integer userId = (Integer)session.getAttribute("userId");// int로 하면 로그인 않된 사람이 가능할수있어서 integer로 받기
		if (userId == null) {
			// 비로그인이면 로그인 화면으로 이동
			return "redirect:/user/sign-in-view";
		}
		
		List<Post> postList = postBO.getPostListByUserId(userId, prevIdParam, nextIdParam);
		int nextId = 0;
		int prevId = 0;
		if(postList.isEmpty() == false) {
			// postList가 비어 있을때 오류를 방지하기 위함   []
			nextId = postList.get(postList.size() - 1).getId();// 가져온 list의 가장끝값(작은id)
			prevId = postList.get(0).getId();
			
			// 이전 방향의 끝인가?
						// prevId와 post 테이블의 가장 큰 id값과 같다면 이전 페이지 X
						if (postBO.isPrevLastPageByUserId(prevId, userId)) {
							prevId = 0;
						}
						
						// 다음 방향의 끝인가?
						// nextId와 post 테이블의 가장 작은 id값과 같다면 다음 페이지 X
						if (postBO.isNextLastPageByUserId(nextId, userId)) {
							nextId = 0;
						}
					}
		model.addAttribute("nextId", nextId);
		model.addAttribute("prevId", prevId);
		model.addAttribute("postList", postList);
		model.addAttribute("viewName", "post/postList");
		return "template/layout";
	}
	
	/**
	 * 글쓰기 화면
	 */

	
	@GetMapping("/post-create-view")
	public String postCreateView(Model model) {
		model.addAttribute("viewName", "post/postCreate");
		return "template/layout";
	}
	/**
	 * 게시글을 클릭하면 글을 더 자세히 보는 방식
	 * @param postId
	 * @param session
	 * @param model
	 * @return
	 */
	@GetMapping("/post-detail-view")
	public String postDetailView(
			@RequestParam("postId") int postId,
			HttpSession session,
			Model model) {
		//로그인 않된 사람이 들어올수도 있으니 변환 검사 실시
		int userId = (int)session.getAttribute("userId");// 변환 검사
		
		// DB Select
		// select where postId and userId -> 정확한 알고리즘 데이터
		Post post = postBO.getPostByPostIdUserId(postId, userId);
		
		model.addAttribute("post", post);
		model.addAttribute("viewName", "post/postDetail");
		return "template/layout";
	}
	
}

