package com.memo.post.domain;

import java.sql.Date;
import java.time.ZonedDateTime;

import lombok.Data;
import lombok.ToString;
@ToString
@Data// getter + setter
public class Post {

	private int id;
	private int userId;
	private String subject;
	private String content;
	private String imagePath;
	private ZonedDateTime createdAt;
	private ZonedDateTime updatedAt;
}
