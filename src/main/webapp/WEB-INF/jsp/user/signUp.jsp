<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div class="d-flex justify-content-center">
	<div class="sign-up-box">
		<h1 class="mb-4">회원가입</h1>
		<form id="signUpForm" method="post" action="/user/sign-up">
			<table class="sign-up-table table table-bordered">
				<tr>
					<th>* 아이디(4자 이상)<br></th>
					<td>
						<%-- 인풋박스 옆에 중복확인을 붙이기 위해 div를 하나 더 만들고 d-flex --%>
						<div class="d-flex">
							<input type="text" id="loginId" name="loginId" class="form-control col-9" placeholder="아이디를 입력하세요.">
							<button type="button" id="loginIdCheckBtn" class="btn btn-success">중복확인</button><br>
						</div>
						
						<%-- 아이디 체크 결과 --%>
						<%-- d-none 클래스: display none (보이지 않게) --%>
						<div id="idCheckLength" class="small text-danger d-none">ID를 4자 이상 입력해주세요.</div>
						<div id="idCheckDuplicated" class="small text-danger d-none">이미 사용중인 ID입니다.</div>
						<div id="idCheckOk" class="small text-success d-none">사용 가능한 ID 입니다.</div>
					</td>
				</tr>
				<tr>
					<th>* 비밀번호</th>
					<td><input type="password" id="password" name="password" class="form-control" placeholder="비밀번호를 입력하세요."></td>
				</tr>
				<tr>
					<th>* 비밀번호 확인</th>
					<td><input type="password" id="confirmPassword" class="form-control" placeholder="비밀번호를 입력하세요."></td>
				</tr>
				<tr>
					<th>* 이름</th>
					<td><input type="text" id="name" name="name" class="form-control" placeholder="이름을 입력하세요."></td>
				</tr>
				<tr>
					<th>* 이메일</th>
					<td><input type="text" id="email" name="email" class="form-control" placeholder="이메일 주소를 입력하세요."></td>
				</tr>
			</table>
			<br>
		
			<button type="submit" id="signUpBtn" class="btn btn-primary float-right">회원가입</button>
		</form>
	</div>
</div>

<script>
$(document).ready(function(){
	//중복버튼 클릭
	$("#loginIdCheckBtn").on("click", function(e){
		//alert("중복확인");
		//경고 문구를 초기화 시켜줘야된다
		$("#idCheckLength").addClass('d-none');
		$("#idCheckDuplicated").addClass('d-none')
		$("#idCheckOk").addClass('d-none')
	let loginId = $("#loginId").val().trim();
	if(loginId.length < 4){
		$("#idCheckLength").removeClass('d-none');
		return;
	}
	
	//ajax - 중복확인
	$.ajax({
		//request
		
		url:"/user/is-duplicated-id"
		, data:{"loginId" : loginId}
		
		//response
		,success:function(data){
			//{"code":200, "isDuplicated":true} 중복이면 true
			if(data.isDuplicated){//중복
				$("#idCheckDuplicated").removeClass("d-none");
			}else{//중복 아님 사용 가능하다는 의미
				$("#idCheckOk").removeClass("d-none");
			}
		}
		,error:function(request, status, error){
			alert("중복확인에 실패했습니다");
		}
	});
	
	})
	//회원가입 submit(form태그 id를 사용)
	$("#signUpForm").on("submit", function(e){
		e.preventDefault();//submit기능을 막아준다
		
		//alert("클릭");
		//validation
		let loginId = $("#loginId").val;
		let password = $("#password").val();
		let confirmPassword = $("#confirmPassword").val();
		let name = $("#name").val();
		let email = $("#email").val();
		//아이디를 입력하세요 않나오니 다시 보기
		
		if (loginId == '') {
			alert("아이디를 입력하세요");//submit으로 했으니 
			return false;
		}
		if(!password|| !confirmPassword){
			alert("비밀번호를 입력하세요");
			return false;
		}
		
		if(password != confirmPassword){
			alert("비밀번호가 일치하지 않습니다");
			return false;
		}
		if(!name){
			alert("이름을 입력하세요");
			return false;
		}
		if(!email){
			alert("이메일을 입력하세요");
			return false;
		}
		
		//중복확인후 사용가능한지 확인하기 =>idCheckOk가 d-none있을때 alert을 띄운다
		if($("#idCheckOk").hasClass('d-none')){
			alert("아이디 중복확인을 다시해주세요.");
			return false;
		}
		//서버로 보내는 방식 2가지
		//1) 원하는 시점에서 submit을 시키고 싶을때 submit을 javascript으로 동작을 시킴
		//$(this)[0].submit();//화면 이동이 반드시 된다(jsp로 return or redirect으로 해줘야된다 -> 화면이 응답을 하는거다)/form태그중에 1번째
		//2) ajax - 응답값jason
		let url = $(this).attr("action"); // form태그의 action주소 가 나왔다
		//alert(url);
		let params = $(this).serialize();// form태그에 있는 name 속성과 값으로 파라미터 구성
		console.log(params);
		
		//ajax의 다른 방식 post의 method을 이용해서 해보기
		$.post(url, params) // request
		.done(function(data){// response
			//{"code":200, "result": "성공"}
		if(data.code == 200){//성공
			alert("가입을 환영합니다 로그인을 해주세요");
			location.href = "/user/sign-in-view";//로그인 화면으로 이동하는 주소를 입력을한다
		}else{//로직 실패
			alert(data.errorMessage);
		}
		});
		
		
	})
});

</script>