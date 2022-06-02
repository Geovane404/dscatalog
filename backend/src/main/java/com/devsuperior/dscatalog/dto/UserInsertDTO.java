package com.devsuperior.dscatalog.dto;

public class UserInsertDTO extends UserDTO {
	private static final long serialVersionUID = 1L;

	private String Password;

	public UserInsertDTO() {
		super();
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}
}
