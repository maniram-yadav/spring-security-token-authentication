package com.authenticator.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="users")
public class Users {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name="user_id")
	private Integer userId;
	
	private String name;
	private String email;
	private String password;
	@Column(name="token",length=1000)
	private String tokens;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="user_roles",joinColumns={@JoinColumn(name="userid")},inverseJoinColumns={@JoinColumn(name="roleid")})
	private List<Roles> roles;
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<Roles> getRoles() {
		return roles;
	}
	public void setRoles(List<Roles> roles) {
		this.roles = roles;
	}
	public String getTokens() {
		return tokens;
	}
	public void setTokens(String tokens) {
		this.tokens = tokens;
	}
	
}
