package com.memo.user.bo;

import org.springframework.stereotype.Service;

import com.memo.user.entity.UserEntity;

@Service
public class UserBO {

	//input: loginid
	//output: userentity(null이거나 entity)
	public UserEntity getUserEntityByLoginId(String loginId) {
		return null; 
	}
}