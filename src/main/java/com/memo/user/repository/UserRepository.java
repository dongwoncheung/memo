package com.memo.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.memo.user.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer>{
	//UserEntity 는 null이거나 채워져있거나
	public UserEntity findByLoginId(String loginId);// 단건 조회
	
}
