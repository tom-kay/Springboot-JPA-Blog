package com.cos.blog.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Member {
	private int id;
	private String password;
	private String email;
	private String username;
	
	@Builder
	public Member(int id, String password, String email, String username) {
		super();
		this.id = id;
		this.password = password;
		this.email = email;
		this.username = username;
	}
}
