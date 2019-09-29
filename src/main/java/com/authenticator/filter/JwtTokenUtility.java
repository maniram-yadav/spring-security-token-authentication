package com.authenticator.filter;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.authenticator.model.Roles;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtility {

	Logger LOGGER=LoggerFactory.getLogger(JwtTokenUtility.class);
	
	@Value("${security.jwt.token.key.value}")
	private String secretKey;

	@Value("${security.jwt.token.expiry.milliseconds}")
	private long expiryTime;

	@PostConstruct
	protected void init() {
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	public String createToken(String userName, Set<SimpleGrantedAuthority> authorities) {
		Claims claims = Jwts.claims().setSubject(userName);
		List<SimpleGrantedAuthority> authorities2= authorities.stream()
				.filter(Objects::nonNull).collect(Collectors.toList());
		claims.put("authority",authorities2);
		Date expiration = new Date(System.currentTimeMillis() + expiryTime);
		return Jwts.builder().setClaims(claims).setIssuedAt(new Date()).setExpiration(expiration)
				.signWith(SignatureAlgorithm.HS512, secretKey).compact();
	}

	private Claims getJwtClaims(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
	}

	public String getUserName(String token) {
		validateToken(token);
		Claims claims = this.getJwtClaims(token);
		return claims.getSubject();
	}
	public boolean isExpiredToken(String token){
		Claims claims = this.getJwtClaims(token);
		Date expiredDate=claims.getExpiration();
		int cmp= new Date().compareTo(expiredDate);
		return (cmp==-1)?false:true;
	}
	public List<SimpleGrantedAuthority> getAuthority(String token) {
		Claims claims = this.getJwtClaims(token);
		LOGGER.debug(""+claims.get("authority"));
		String auhorities=claims.get("authority").toString();
		ObjectMapper mapper=new ObjectMapper();
	    mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
	    return (List<SimpleGrantedAuthority>) mapper.convertValue(auhorities, new TypeReference<List<SimpleGrantedAuthority>>(){}) ;
	}

	public String resolveToken(HttpServletRequest req) {
		String bearerToken = req.getHeader("token");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			if(isExpiredToken(token)){
				new RuntimeException("Token have been expired");
			}
			return true;
		} catch (Exception e) {
			LOGGER.error("Exception : ",e);
			throw new RuntimeException(e);
		}
	}
}
