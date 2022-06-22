package com.devsuperior.dscatalog.dto;

import com.devsuperior.dscatalog.services.validation.UserInsertValid;

@UserInsertValid
public class UserDTOInsert extends UserDTO {
	private static final long serialVersionUID = 1L;
	
	private String password;
	
	public UserDTOInsert() {
		super();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}	
}
