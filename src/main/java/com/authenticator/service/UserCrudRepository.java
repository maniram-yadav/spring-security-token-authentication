package com.authenticator.service;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.authenticator.model.Users;


@Repository
public interface UserCrudRepository extends CrudRepository<Users, Integer> {
	public Users findByEmail(String email);
	public Users findByUserId(Integer userId);
	@Query("select u.tokens from Users u where u.tokens is not null")
	public List<String> getAllTokens();
}
