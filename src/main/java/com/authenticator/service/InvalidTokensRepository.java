package com.authenticator.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.authenticator.model.InvalidTokens;
import com.authenticator.model.Users;


@Repository
public interface InvalidTokensRepository extends CrudRepository<InvalidTokens, Integer> {
	public Users findByToken(String token);
}
