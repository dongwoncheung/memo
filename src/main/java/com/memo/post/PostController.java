package com.memo.post;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.memo.post.domain.Post;

@RequestMapping("/post")
@Controller
public class PostController {
	
	@Autowired
	private com.memo.post.bo.postBO postBO;
	@GetMapping("/post/post-list-view")//반드시 로그인이 되야되는 페이지
	public String PostListView(Model model,
			HttpSession session) {
		Integer userId = (Integer)session.getAttribute("userId");// int로 하면 로그인 않된 사람이 로그인 가능하다
		if(userId == null) {
			//비로그인이면 로그인 페이지로 이동
			return"redirect:/user/sign-in-view";
		}
		
		List<Post>postList = postBO.getPostListByUserId(userId);
		
		model.addAttribute("postList", postList);
		model.addAttribute("viewName", "post/postList");
		return "template/layout";
	}
}
