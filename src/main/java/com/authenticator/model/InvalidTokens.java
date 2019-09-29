package com.authenticator.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="invalid_tokens")
public class InvalidTokens {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer invalidTokenId;
	@Column(name="token",length=1000)
	private String token;
	
	public Integer getInvalidTokenId() {
		return invalidTokenId;
	}
	public void setInvalidTokenId(Integer invalidTokenId) {
		this.invalidTokenId = invalidTokenId;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
