package com.memo.post.bo;

import java.util.Collections;
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
	
	private static final int POST_MAX_SIZE = 3;// 상수를 설정
	
	@Autowired
	private PostMapper postMapper;
	
	@Autowired
	private FileManagerService fileManager;

	//최신순 게시글
		// input:userId   output:List<Post>여러개행 최신순으로
		public List<Post> getPostListByUserId(int userId, Integer prevId, Integer nextId) {
			// 게시글 번호 10 9 8| 7 6 5| 4 3 2 |1 
			//만약 4 3 2 페이지에 있을때 
			// 다음을 눌렀을때 -> 2보다 작은 3개 desc정렬 사용
			// 이전을 눌렀을때 -> 4보다 큰 3개 asc(5 6 7 ) => List reverse(7 6 5 )
			// 첫폐이지 이전, 다음없음 desc3개 
			
			String direction = null; // 방향
			Integer standardId = null;  // 기준이 되는 postid 번호
			if(prevId != null) {//이전
				direction = "prev";
				standardId = prevId;
				
				List<Post> postList = postMapper.selectPostListByUserId(userId, direction, standardId, POST_MAX_SIZE);
			
				//reverse 5 6 7 => 7 6 5 list에서 뒤집어 버린다
				Collections.reverse(postList); // 뒤집고 저장까지 해준다
			
			return postList;
		} else if (nextId != null) { // 다음
			direction = "next";
			standardId = nextId;
		}
		
		// 첫페이지 or 다음
		return postMapper.selectPostListByUserId(userId, direction, standardId, POST_MAX_SIZE);
	}
	
	// 이전 페이지의 마지막인가?
	public boolean isPrevLastPageByUserId(int prevId, int userId) {
		int postId = postMapper.selectPostIdByUserIdAndSort(userId, "DESC");
		return postId == prevId; // 같으면 끝 true, 아니면 false
	}
	
	// 다음 페이지의 마지막인가?
	public boolean isNextLastPageByUserId(int nextId, int userId) {
		int postId = postMapper.selectPostIdByUserIdAndSort(userId, "ASC");
		return postId == nextId; // 같으면 끝 true, 아니면 false
	}
	
	// 게시글 detail
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
		//이미지 업로드
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
		// 글을 삭제하는 method
		//in: postid(글번호) / userid(글쓴이 번호)
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
//package com.memo.post.bo;
//
//import java.util.Collections;
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.memo.common.FileManagerService;
//import com.memo.post.domain.Post;
//import com.memo.post.mapper.PostMapper;
//
//@Service
//public class PostBO {
//	private Logger logger = LoggerFactory.getLogger(this.getClass());
//	
//	private static final int POST_MAX_SIZE = 3; // 상수를 설정
//	
//	@Autowired
//	private PostMapper postMapper;
//	
//	@Autowired
//	private FileManagerService fileManager;
//
//	//최신순 게시글
//	// input:userId   output:List<Post>여러개행 최신순으로
//	public List<Post> getPostListByUserId(int userId, Integer prevId, Integer nextId) {
//		// 게시글 번호 10 9 8| 7 6 5| 4 3 2 |1 
//		//만약 4 3 2 페이지에 있을때 
//		// 다음을 눌렀을때 -> 2보다 작은 3개 desc정렬 사용
//		// 이전을 눌렀을때 -> 4보다 큰 3개 asc(5 6 7 ) => List reverse(7 6 5 )
//		// 첫폐이지 이전, 다음없음 desc3개 
//		
//		String direction = null; // 방향
//		Integer standardId = null;  // 기준이 되는 postid 번호
//		if(prevId != null) {//이전
//			direction = "prev";
//			standardId = prevId;
//			
//			List<Post> postList = postMapper.selectPostListByUserId(userId, direction, standardId, POST_MAX_SIZE);
//		
//			//reverse 5 6 7 => 7 6 5 list에서 뒤집어 버린다
//			Collections.reverse(postList); // 뒤집고 저장까지 해준다
//			
//			return postList;
//			
//		}else if(nextId != null) {//다음
//			direction = "next";
//			standardId = nextId;
//		}
//		
//		//첫페이지 이거나 다음이거나
//		return postMapper.selectPostListByUserId(userId, direction, standardId, POST_MAX_SIZE);
//		
//		// 이전 페이지의 마지막인가?
//		public boolean isPrevLastPageByUserId(int prevId, int userId) {
//			int postId = postMapper.selectPostIdByUserIdAndSort(userId, "DESC");
//			return postId == prevId; // 같으면 끝 true, 아니면 false
//		}
//		
//		// 다음 페이지의 마지막인가?
//		public boolean isNextLastPageByUserId(int nextId, int userId) {
//			int postId = postMapper.selectPostIdByUserIdAndSort(userId, "ASC");
//			return postId == nextId; // 같으면 끝 true, 아니면 false
//		}
//		
//
//	}
//	// 게시글 detail
//	// input: postId, userId  output:Post
//	public Post getPostByPostIdUserId(int postId, int userId) {
//		return postMapper.selectPostByPostIdUserId(postId, userId);
//	}
//	
//	// input:파라미터들   output:X 
//	public void addPost(int userId, String userLoginId, 
//			String subject, String content, 
//			MultipartFile file) {
//		
//		String imagePath = null;
//		
//		// 이미지가 있으면 업로드 // server 컴퓨터에 폴더를 만들어 내서 저장하는 기능
//		if (file != null) {
//			imagePath = fileManager.saveFile(userLoginId, file);
//		}
//		
//		postMapper.insertPost(userId, subject, content, imagePath);
//	}
//	//이미지 업로드
//	// input:파라미터들     output:X
//	public void updatePost(int userId, String userLoginId,
//			int postId, String subject, String content, 
//			MultipartFile file) {
//		
//		// 기존 글을 가져와본다.(1. 이미지 교체 시 삭제 위해   2. 업데이트 대상이 있는지 확인)
//		Post post = postMapper.selectPostByPostIdUserId(postId, userId);
//		if (post == null) {
//			logger.error("[글 수정] post is null. postId:{}, userId:{}", postId, userId);
//			return;
//		}
//		
//		// 파일이 있다면
//		// 1) 새 이미지를 업로드 한다
//		// 2) 새 이미지 업로드 성공 시 기존 이미지 제거(기존 이미지가 있을 때)
//		//post.getImagePath() // mpe에러 서버에서 꼭 수정을 해둬야된다
//		String imagePath = null;
//		if (file != null) {
//			// 업로드
//			imagePath = fileManager.saveFile(userLoginId, file);
//			
//			// 업로드 성공 시 기존 이미지 제거(있으면) 
//			if (imagePath != null && post.getImagePath() != null) {
//				// 업로드가 성공을 했고, 기존 이미지가 존재한다면 => 삭제
//				// 이미지 제거
//				fileManager.deleteFile(post.getImagePath());
//			}
//		}
//		
//		// DB 글 update
//		postMapper.updatePostByPostIdUserId(postId, userId, subject, content, imagePath);
//	}
//	// 글을 삭제하는 method
//	//in: postid(글번호) / userid(글쓴이 번호)
//	//out: x 성공된 행의 갯수 return 않함
//	public void deletePostByPostIdUserId(int postId, int userId) {
//		// 기존글 가져옴(이미지가있으면 삭제를 해야된다)
//		Post post = postMapper.selectPostByPostIdUserId(postId, userId);
//		if(post == null) {
//			logger.info("[글 삭제] post가 null. postid:{}, userId:{}", postId, userId);// 와일드 카드 문법
//			return;
//		}
//		// 기존 이미지가 존재하면 -> 삭제
//		if(post.getImagePath() != null) {
//			fileManager.deleteFile(post.getImagePath());
//		}
//		//DB삭제
//		postMapper.deletePostByPostIdUserId(postId, userId);
//		
//	}
//}

