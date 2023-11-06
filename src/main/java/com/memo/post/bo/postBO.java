package com.memo.post.bo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.memo.common.FileManagerService;
import com.memo.post.domain.Post;
import com.memo.post.mapper.PostMapper;

@Service
public class PostBO {
	@Autowired
	private PostMapper postMapper;
	@Autowired
	private FileManagerService fileManager;
	//in: user id 
	//out: List<Post> 여러개행 최신순으로
	public List<Post> getPostListByUserId(int userId){
		return postMapper.selectPostListByUserId(userId);
	} 
	// input:파라미터들   output:X
		public void addPost(int userId, String userLoginId,String subject, String content, 
				MultipartFile file) {
			
			String imagePath = null;
			
			// 이미지가 있으면 업로드 // server 컴퓨터에 폴더를 만들어 내서 저장하는 기능
			if(file != null) {
				imagePath = fileManager.saveFile(userLoginId, file);
			}
			
			
			postMapper.insertPost(userId, subject, content, imagePath);
		}
		//in: postId, userId
		//out: X
		public Post getPostByPostIdUserId(int postId, int userId) {
			return postMapper.selectPostByPostIdUserId(postId, userId);
		}
}
