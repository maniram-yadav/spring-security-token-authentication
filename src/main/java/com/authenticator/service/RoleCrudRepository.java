package com.authenticator.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.authenticator.model.Roles;
import com.authenticator.model.Users;


@Repository
public interface RoleCrudRepository extends CrudRepository<Roles, Integer> {
	public Users findByRole(String role);
	public Users findByRoleId(Integer roleId);
}
