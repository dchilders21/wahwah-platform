package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.data.entities.OAuthApplication;
import com.wahwahnetworks.platform.data.entities.OAuthGrant;
import com.wahwahnetworks.platform.data.entities.OAuthToken;
import com.wahwahnetworks.platform.data.entities.User;
import com.wahwahnetworks.platform.data.repos.OAuthApplicationRepository;
import com.wahwahnetworks.platform.data.repos.OAuthGrantRepository;
import com.wahwahnetworks.platform.data.repos.OAuthTokenRepository;
import com.wahwahnetworks.platform.data.repos.UserRepository;
import com.wahwahnetworks.platform.exceptions.EntityNotPermittedException;
import com.wahwahnetworks.platform.lib.AESUtil;
import com.wahwahnetworks.platform.lib.HMacUtil;
import com.wahwahnetworks.platform.models.UserModel;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by jhaygood on 3/2/15.
 */

@Service
public class OAuthService
{

	@Autowired
	private OAuthApplicationRepository oauthApplicationRepository;

	@Autowired
	private OAuthGrantRepository oAuthGrantRepository;

	@Autowired
	private OAuthTokenRepository oAuthTokenRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	private final String HMAC_KEY = "chatoyant"; // Meriam-Webster WOTD on 2015-03-01

    private final String PLATFORM_APP_CLIENT_ID = "13244748-D80B-11E5-A1BB-F93F60F10656";

	@Transactional(readOnly = true)
	public OAuthApplication getApplicationForClientId(String clientId) {
		return oauthApplicationRepository.findByClientId(clientId);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String getPlatformBearerTokenForUser(UserModel userModel) throws Exception {
        OAuthApplication application = getApplicationForClientId(PLATFORM_APP_CLIENT_ID);


        if(application == null){
            String currentTime = Instant.now().toString();
            String clientSecret = HMacUtil.hmac(PLATFORM_APP_CLIENT_ID + ":" + currentTime, HMAC_KEY);

            application = new OAuthApplication();
            application.setClientId(PLATFORM_APP_CLIENT_ID);
            application.setClientSecret(clientSecret);
            application.setApplicationName("Wahwah Platform");
            application.setRequiresUserConsent(false);

            oauthApplicationRepository.save(application);
        }

        Set<String> scopes = new HashSet<>();
        scopes.add("platform");

        String authCode = createAuthorizationCode(application,userModel, scopes);
        String token = getBearerTokenFromAuthorizationCode(authCode,application.getClientId(),application.getClientSecret());

        return token;
    }

	@Transactional
	public OAuthApplication createApplication(String name)
	{

		String clientId = UUID.randomUUID().toString();
		String currentTime = Instant.now().toString();
		String clientSecret = HMacUtil.hmac(clientId + ":" + currentTime, HMAC_KEY);

		OAuthApplication application = new OAuthApplication();
		application.setClientId(clientId);
		application.setClientSecret(clientSecret);
		application.setApplicationName(name);

		oauthApplicationRepository.save(application);

		return application;
	}

	@Transactional
	public String createAuthorizationCode(OAuthApplication application, UserModel userModel, Set<String> scopes) throws Exception
	{
		Instant expirationTime = Instant.now().plusSeconds(60 * 10); // Expires in 10 minutes
		User user = userRepository.findOne(userModel.getUserId());

		OAuthGrant oAuthGrant = oAuthGrantRepository.findByApplicationAndUser(application, user);

		String authorizationCode;

		if (oAuthGrant == null)
		{
			oAuthGrant = new OAuthGrant();
			oAuthGrant.setApplication(application);
			oAuthGrant.setUser(user);
			oAuthGrantRepository.save(oAuthGrant);
		}

		authorizationCode = UUID.randomUUID().toString();

		OAuthToken oAuthToken = new OAuthToken();
		oAuthToken.setGrant(oAuthGrant);
		oAuthToken.setAuthorizationCode(authorizationCode);
		oAuthToken.setExpirationTime(expirationTime);
		oAuthToken.setOauthScopes(scopes);

		oAuthTokenRepository.save(oAuthToken);

		String authorizationSecret = HMacUtil.hmac(authorizationCode, HMAC_KEY);

		SecureRandom secureRandom = new SecureRandom();
		byte[] ivData = secureRandom.generateSeed(16);
		String ivString = Base64.encodeBase64String(ivData);
		String keyString = authorizationSecret + ":" + application.getClientSecret();
		String token = oAuthGrant.getId() + ":" + authorizationCode + ":" + expirationTime.getEpochSecond();

		String base64EncodedToken = AESUtil.encrypt(keyString, ivData, token);

		String tokenMac = HMacUtil.hmac(base64EncodedToken, HMAC_KEY);

		String authorizationToken = base64EncodedToken + "." + tokenMac + "." + oAuthToken.getId() + "." + ivString;
		return authorizationToken;
	}

	@Transactional(readOnly = true)
	public OAuthToken getTokenFromAuthorizationCode(String authorizationToken)
	{
		try
		{
			int separatorIndex = authorizationToken.lastIndexOf(".");

			String messageContentsWithMac = authorizationToken.substring(0, separatorIndex);
			String ivString = authorizationToken.substring(separatorIndex + 1);

			separatorIndex = messageContentsWithMac.lastIndexOf(".");
			String messageContents = messageContentsWithMac.substring(0, separatorIndex);
			int tokenId = Integer.parseInt(messageContentsWithMac.substring(separatorIndex + 1));

			separatorIndex = messageContents.lastIndexOf(".");
			String base64EncodedToken = messageContents.substring(0, separatorIndex);
			String tokenMac = messageContents.substring(separatorIndex + 1);

			// Verify Message
			if (HMacUtil.hmac(base64EncodedToken, HMAC_KEY).equals(tokenMac))
			{
				OAuthToken oAuthToken = oAuthTokenRepository.findOne(tokenId);

				if (oAuthToken != null)
				{
					String authorizationCode = oAuthToken.getAuthorizationCode();
					String authorizationSecret = HMacUtil.hmac(authorizationCode, HMAC_KEY);
					String keyString = authorizationSecret + ":" + oAuthToken.getGrant().getApplication().getClientSecret();
					byte[] ivData = Base64.decodeBase64(ivString);

					String token = AESUtil.decrypt(keyString, ivData, base64EncodedToken);

					int tokenIdInToken = Integer.parseInt(token.substring(0, token.indexOf(":")));
					String authorizationCodeInToken = token.substring(token.indexOf(":") + 1, token.lastIndexOf(":"));
					Integer epochExpiresTime = Integer.parseInt(token.substring(token.lastIndexOf(":") + 1));
					Instant expiresTime = Instant.ofEpochSecond(epochExpiresTime);

					boolean isExpired = Instant.now().isAfter(expiresTime);

					// Verify token was issued for same grant requested -- and it's not expired
					if (tokenIdInToken == tokenIdInToken && authorizationCodeInToken.equals(authorizationCode) && !isExpired)
					{
						return oAuthToken;
					}
				}
			}
		}
		catch (Exception ex)
		{
			return null;
		}

		return null;
	}

	@Transactional(readOnly = true)
	public String getBearerTokenFromAuthorizationCode(String code, String clientId, String clientSecret) throws Exception
	{
		OAuthToken oAuthToken = getTokenFromAuthorizationCode(code);

		if (oAuthToken == null)
		{
			throw new EntityNotPermittedException("Invalid or Expired Authentication code");
		}

		if (!oAuthToken.getGrant().getApplication().getClientId().equals(clientId) || !oAuthToken.getGrant().getApplication().getClientSecret().equals(clientSecret))
		{
			throw new EntityNotPermittedException("Invalid Client Credentials");
		}

		OAuthApplication application = oAuthToken.getGrant().getApplication();
		String authorizationCode = oAuthToken.getAuthorizationCode();
		String authorizationSecret = HMacUtil.hmac(authorizationCode, HMAC_KEY);
		Instant expirationTime = Instant.now().plusSeconds(60 * 60);

		// Update expiration token time
		oAuthToken.setExpirationTime(expirationTime);
		oAuthTokenRepository.save(oAuthToken);

		SecureRandom secureRandom = new SecureRandom();
		byte[] ivData = secureRandom.generateSeed(16);
		String ivString = Base64.encodeBase64String(ivData);
		String keyString = authorizationSecret + ":" + application.getClientSecret();
		String token = "BearerToken." + oAuthToken.getId() + ":" + authorizationCode + ":" + expirationTime.getEpochSecond();

		String base64EncodedToken = AESUtil.encrypt(keyString, ivData, token);

		String tokenMac = HMacUtil.hmac(base64EncodedToken, HMAC_KEY);

		String bearerToken = base64EncodedToken + "." + tokenMac + "." + oAuthToken.getId() + "." + ivString;
		return bearerToken;
	}

	@Transactional(readOnly = true)
	private OAuthToken getTokenFromBearerToken(String bearerToken)
	{
		try
		{
			int separatorIndex = bearerToken.lastIndexOf(".");

			String messageContentsWithMac = bearerToken.substring(0, separatorIndex);
			String ivString = bearerToken.substring(separatorIndex + 1);

			separatorIndex = messageContentsWithMac.lastIndexOf(".");
			String messageContents = messageContentsWithMac.substring(0, separatorIndex);
			int tokenId = Integer.parseInt(messageContentsWithMac.substring(separatorIndex + 1));

			separatorIndex = messageContents.lastIndexOf(".");
			String base64EncodedToken = messageContents.substring(0, separatorIndex);
			String tokenMac = messageContents.substring(separatorIndex + 1);

			// Verify Message
			if (HMacUtil.hmac(base64EncodedToken, HMAC_KEY).equals(tokenMac))
			{
				OAuthToken oAuthToken = oAuthTokenRepository.findOne(tokenId);

				if (oAuthToken != null)
				{
					String authorizationCode = oAuthToken.getAuthorizationCode();
					String authorizationSecret = HMacUtil.hmac(authorizationCode, HMAC_KEY);
					String keyString = authorizationSecret + ":" + oAuthToken.getGrant().getApplication().getClientSecret();
					byte[] ivData = Base64.decodeBase64(ivString);

					String token = AESUtil.decrypt(keyString, ivData, base64EncodedToken);

					if (token.startsWith("BearerToken."))
					{

						int tokenIdInToken = Integer.parseInt(token.substring(token.indexOf(".") + 1, token.indexOf(":")));
						String authorizationCodeInToken = token.substring(token.indexOf(":") + 1, token.lastIndexOf(":"));
						Integer epochExpiresTime = Integer.parseInt(token.substring(token.lastIndexOf(":") + 1));
						Instant expiresTime = Instant.ofEpochSecond(epochExpiresTime);

						boolean isExpired = Instant.now().isAfter(expiresTime);

						// Verify token was issued for same grant requested -- and it's not expired
						if (tokenIdInToken == tokenId && authorizationCodeInToken.equals(authorizationCode) && !isExpired)
						{
							return oAuthToken;
						}
					}
				}
			}
		}
		catch (Exception ex)
		{
			return null;
		}

		return null;
	}

	@Transactional(readOnly = true)
	public Set<String> getScopesFromBearerToken(String bearerToken)
	{
		OAuthToken oAuthToken = getTokenFromBearerToken(bearerToken);

		if (oAuthToken != null)
		{
			return oAuthToken.getOauthScopes();
		}

		return new HashSet<>();
	}

	@Transactional(readOnly = true)
	public UserModel getUserFromBearerToken(String bearerToken)
	{
		OAuthToken oAuthToken = getTokenFromBearerToken(bearerToken);

		if (oAuthToken != null)
		{
			UserModel userModel = userService.getUserById(oAuthToken.getGrant().getUser().getId());
			return userModel;
		}

		return null;
	}

	@Transactional(readOnly = true)
	public boolean isValidToken(String token){
		OAuthToken authToken = getTokenFromBearerToken(token);

		if(authToken != null){
			Instant expirationTime = authToken.getExpirationTime();
			Instant now = Instant.now();

			if(now.isAfter(expirationTime)){
				return false;
			}

			return true;
		}

		return false;
	}
}
