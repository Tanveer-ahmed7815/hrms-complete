package com.te.flinko.util;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.te.flinko.audit.BaseConfigController;
import com.te.flinko.entity.employee.EmployeeLoginInfo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtUtil extends BaseConfigController {

	private static final String COMPANY_CODE = "COMPANY_CODE";

	private static final String TREMINAL_ID = "terminalId";

	private static final String ROLES = "roles";

	private static final String COMPANY_ID = "companyId";

	private static final String FLINKO_TEAM = "FLINKO TEAM";

	@Value("${auth.app.jwt-secret}")
	private String secretKey;

	@Value("${auth.app.jwt-expiration-ms}")
	private int jwtExpirationMs;

	@Value("${auth.app.jwt-refresh-ms}")
	private int jwtRefreshMs;

	@Value("${auth.app.jwt-not-before-ms}")
	private int jwtNotBefore;

	private Map<String, String> jwtTocketIds = new HashMap<>();

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Integer extractCompanyId(String token) {
		return (Integer) extractAllClaims(token).get(COMPANY_ID);
	}

	public String extractTerminalId(String token) {
		return (String) extractAllClaims(token).get(TREMINAL_ID);
	}

	public String extractCompanyCode(String token) {
		return (String) extractAllClaims(token).get(COMPANY_CODE);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public String[] generateToken(EmployeeLoginInfo user, String deviceId) {
		return generateToken(new HashMap<>(), user, deviceId);
	}

	public String[] generateAccessToken(EmployeeLoginInfo user, String refreshToken, String deviceId) {
		String uuid = extractClaim(refreshToken, Claims::getId);
		String accessToken = Jwts.builder()
				.claim(ROLES,
						user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.claim(COMPANY_ID, user.getEmployeePersonalInfo().getCompanyInfo().getCompanyId())
				.claim(TREMINAL_ID, deviceId).setIssuer(FLINKO_TEAM).setSubject(user.getUsername())
				.setAudience(user.getEmployeePersonalInfo().getFirstName() + " "
						+ user.getEmployeePersonalInfo().getLastName())
				.claim(COMPANY_CODE, user.getEmployeePersonalInfo().getCompanyInfo().getCompanyCode())
				.setExpiration(new Date(Date.from(Instant.now()).getTime() + jwtExpirationMs))
				.setNotBefore(new Date(Date.from(Instant.now()).getTime() + jwtNotBefore))
				.setIssuedAt(Date.from(Instant.now())).setHeaderParam("typ", "JWT").setId(uuid)
				.signWith(getSignInKey(), SignatureAlgorithm.HS512).compact();
		return new String[] { accessToken, refreshToken };
	}

	public String[] generateToken(Map<String, Object> extraClaims, EmployeeLoginInfo user, String deviceId) {
		String uuid = UUID.randomUUID().toString();
		this.jwtTocketIds.put(user.getUsername(), uuid);
		String accessToken = Jwts.builder()
				.claim(ROLES,
						user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.claim(COMPANY_ID, user.getEmployeePersonalInfo().getCompanyInfo().getCompanyId())
				.claim(TREMINAL_ID, deviceId).setIssuer(FLINKO_TEAM).setSubject(user.getUsername())
				.claim(COMPANY_CODE, user.getEmployeePersonalInfo().getCompanyInfo().getCompanyCode())
				.setAudience(user.getEmployeePersonalInfo().getFirstName() + " "
						+ user.getEmployeePersonalInfo().getLastName())
				.setExpiration(new Date(Date.from(Instant.now()).getTime() + jwtExpirationMs))
				.setNotBefore(new Date(Date.from(Instant.now()).getTime() + jwtNotBefore))
				.setIssuedAt(Date.from(Instant.now())).setHeaderParam("typ", "JWT").setId(uuid)
				.signWith(getSignInKey(), SignatureAlgorithm.HS512).compact();

		String refreshToken = Jwts.builder().setIssuer(FLINKO_TEAM).setSubject(user.getUsername())
				.claim(COMPANY_ID, user.getEmployeePersonalInfo().getCompanyInfo().getCompanyId())
				.claim(TREMINAL_ID, deviceId)
				.claim(COMPANY_CODE, user.getEmployeePersonalInfo().getCompanyInfo().getCompanyCode())
				.setAudience(user.getEmployeePersonalInfo().getFirstName() + " "
						+ user.getEmployeePersonalInfo().getLastName())
				.setExpiration(new Date(Date.from(Instant.now()).getTime() + jwtRefreshMs))
				.setNotBefore(new Date(Date.from(Instant.now()).getTime() + jwtNotBefore))
				.setIssuedAt(Date.from(Instant.now())).setHeaderParam("typ", "JWT").setId(uuid)
				.signWith(getSignInKey(), SignatureAlgorithm.HS512).compact();

		return new String[] { accessToken, refreshToken };
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
	}

	private Key getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public boolean validateJwtToken(String authToken) {
		try {
			String username = extractClaim(authToken, Claims::getSubject);
			if (!extractTerminalId(authToken).equals(getTerminalId())) {
				log.error("Terminal Id Changed");
				throw new MalformedJwtException("Terminal Id Changed");
			}
			if (!extractClaim(authToken, Claims::getId).equals(this.jwtTocketIds.get(username))) {
				log.error("Tocken Mismatched");
				throw new MalformedJwtException("Tocken Mismatched");
			}
			if (Boolean.TRUE.equals(isTokenExpired(authToken)))
				throw new ExpiredJwtException(null, null, authToken);
			return true;
		} catch (MalformedJwtException e) {
			log.error("Invalid JWT token: {}", e.getMessage());
			throw e;
		} catch (ExpiredJwtException e) {
			log.error("JWT token is expired: {}", e.getMessage());
			throw e;
		} catch (UnsupportedJwtException e) {
			log.error("JWT token is unsupported: {}", e.getMessage());
			throw e;
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty: {}", e.getMessage());
			throw e;
		}
	}
}
