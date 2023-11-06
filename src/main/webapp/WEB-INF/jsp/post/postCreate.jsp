<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div class="d-flex justify-content-center"> <!-- 수평기준 가운데 정렬 -->
	<div class="w-50">
		<h1>글 쓰기</h1>
			<input type="text" id="subject" class="form-control" placeholder="제목을 입력하세요">
			<textarea id="content"class="form-control" rows="10" placehodler="내용을 입력하세요"></textarea>
	<div class="d-flex justify-content-end my-4">
		<input type="file" id="file" accept=".jpg, .jpeg, .png, .gif">
	</div>	
	<div class="d-flex justify-content-between">
		<button type="button" id="postListBtn" class="btn btn-dark">목록</button>
		
		<div>
			<button type="button" id="claerBtn" class="btn btn-secondary">모두 지우기</button>
			<button type="button" id="saveBtn" class="btn btn-primary">저장</button>
		</div>
	</div>
	</div>
	
</div>

<script>
$(document).ready(function(){
	//목록 버튼 클릭 -> 글목록으로 이동
	$("#postListBtn").on("click", function(){
		location.href="/post/post-list-view";
	});
	//모두지우기 버튼
	$("#claerBtn").on("click", function(){
		$("#subject").val("");
		$("#content").val("");
	});
	//글 저장 버튼
	$("#saveBtn").on("click", function(){
		let subject = $("#subject").val().trim();
		let content = $("#content").val().trim();
		let fileName = $("#file").val(); //C:\fakepath\다운로드.jpg
		
		//alert(file);
		
	//validation check
	
	if(!subject){
		alert("제목을 입력하세요");
		return;
	}
	if(!content){
		alert("내용을 입력하세요");
		return;
	}
	//파일이 업로드 된경우에만 확장자 체크
	if(fileName){
		//alert("파일이 있다");
		//C:\fakepath\다운로드.jpg
		// 확장자만 뽑은후 소문자로 변경한다
		let ext = fileName.split(".").pop().toLowerCase();
		//alert(ext);
		
		if($.inArray(ext, ['jpg','jpeg','png','gif']) == -1){// 이배열 안에 ext가 없을때를 의미한다
			alert("이미지 파일만 업로드 할수있습니다");
			$("#file").val(""); // 파일을 비운다
			return;
		}
	}

	//ajax
	
	//request param구성
	// 이미지를 업로드를 할떄는 반드시 form태그가 있어야된다
	let formData = new FormData();
		formData.append("subject", subject); // key는 form 태그의 name 속성과 같고 Request parameter명이 된다.
		formData.append("content", content);
		formData.append("file", $("#file")[0].files[0]); 
	
	$.ajax({
		//request
		type:"post"
		,url:"/post/create"
		,data:formData
		//파일을 업로드 하는 방식
		, enctype:"multipart/form-data" // 파일업로드를 위한 필수 설정 -> form과 label태그로 이미지 업로드 할때 반드시 넣어줘야되는 함수
		,processData:false // 파일업로드를 위한 필수 설정 
		,contentType:false // 파일업로드를 위한 필수 설정	 -> 지금 내가 보내는 타입이 string이 아니다는것을 알릴려주는 설정
		
		//response
		, success:function(data){
			if(data.result == "성공"){
				alert("매모가저장되었습니다")
				location.href="/post/post-list-view";
			}else{
				//로직 실패
				alert(data.errorMessage);
			}
		}
		,error:function(request, error, status){
			alert("글을 저장하는데 실패했습니다");
		}
	})
	
	});
});
</script>