package com.memo.post.bo;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.memo.common.FileManagerService;
import com.memo.post.domain.Post;
import com.memo.post.mapper.PostMapper;

@Service
public class PostBO {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PostMapper postMapper;
	
	@Autowired
	private FileManagerService fileManager;

	// input:userId   output:List<Post>여러개행 최신순으로
	public List<Post> getPostListByUserId(int userId) {
		return postMapper.selectPostListByUserId(userId);
	}
	
	// input: postId, userId  output:Post
	public Post getPostByPostIdUserId(int postId, int userId) {
		return postMapper.selectPostByPostIdUserId(postId, userId);
	}
	
	// input:파라미터들   output:X 
	public void addPost(int userId, String userLoginId, 
			String subject, String content, 
			MultipartFile file) {
		
		String imagePath = null;
		
		// 이미지가 있으면 업로드 // server 컴퓨터에 폴더를 만들어 내서 저장하는 기능
		if (file != null) {
			imagePath = fileManager.saveFile(userLoginId, file);
		}
		
		postMapper.insertPost(userId, subject, content, imagePath);
	}
	
	// input:파라미터들     output:X
	public void updatePost(int userId, String userLoginId,
			int postId, String subject, String content, 
			MultipartFile file) {
		
		// 기존 글을 가져와본다.(1. 이미지 교체 시 삭제 위해   2. 업데이트 대상이 있는지 확인)
		Post post = postMapper.selectPostByPostIdUserId(postId, userId);
		if (post == null) {
			logger.error("[글 수정] post is null. postId:{}, userId:{}", postId, userId);
			return;
		}
		
		// 파일이 있다면
		// 1) 새 이미지를 업로드 한다
		// 2) 새 이미지 업로드 성공 시 기존 이미지 제거(기존 이미지가 있을 때)
		//post.getImagePath() // mpe에러 서버에서 꼭 수정을 해둬야된다
		String imagePath = null;
		if (file != null) {
			// 업로드
			imagePath = fileManager.saveFile(userLoginId, file);
			
			// 업로드 성공 시 기존 이미지 제거(있으면) 
			if (imagePath != null && post.getImagePath() != null) {
				// 업로드가 성공을 했고, 기존 이미지가 존재한다면 => 삭제
				// 이미지 제거
				fileManager.deleteFile(post.getImagePath());
			}
		}
		
		// DB 글 update
		postMapper.updatePostByPostIdUserId(postId, userId, subject, content, imagePath);
	}
	//in: postid / userid
	//out: x 성공된 행의 갯수 return 않함
	public void deletePostByPostIdUserId(int postId, int userId) {
		// 기존글 가져옴(이미지가있으면 삭제를 해야된다)
		Post post = postMapper.selectPostByPostIdUserId(postId, userId);
		if(post == null) {
			logger.info("[글 삭제] post가 null. postid:{}, userId:{}", postId, userId);// 와일드 카드 문법
			return;
		}
		// 기존 이미지가 존재하면 -> 삭제
		if(post.getImagePath() != null) {
			fileManager.deleteFile(post.getImagePath());
		}
		//DB삭제
		postMapper.deletePostByPostIdUserId(postId, userId);
		
	}
}

