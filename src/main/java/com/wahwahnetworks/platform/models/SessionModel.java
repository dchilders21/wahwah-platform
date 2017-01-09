package com.wahwahnetworks.platform.models;

import com.wahwahnetworks.platform.data.entities.Account;
import com.wahwahnetworks.platform.data.repos.AccountRepository;
import com.wahwahnetworks.platform.models.impl.SessionModelImpl;
import com.wahwahnetworks.platform.services.OAuthService;
import com.wahwahnetworks.platform.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

/**
 * Created by Justin on 5/16/2014.
 */

@Component
@Scope("request")
public class SessionModel
{

	@Autowired
	private UserService userService;

	@Autowired
	private HttpSession httpSession;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private SessionModelImpl sessionModelImpl;

    @Autowired
    private OAuthService oAuthService;

	public UserModel getUser()
	{

		Integer userId = sessionModelImpl.getUserId();

		if (userId == 0)
		{ // We might have the user_id set in the session attribute still
			userId = (Integer) httpSession.getAttribute("user_id");

			if (userId == null)
			{
				return null;
			}

			sessionModelImpl.setUserId(userId);
		}

		return userService.getUserById(userId);
	}

	public Account getAccount()
	{
		UserModel userModel = getUser();

		if(userModel.getAccountId() != null){
			return accountRepository.findOne(userModel.getAccountId());
		}

		return null;
	}

	public UserModel getRealUser()
	{ // Impersonation/masquerading

		Integer userId = sessionModelImpl.getRealUserId();

		if (userId == 0)
		{ // We might have the user_id set in the session attribute still
			userId = (Integer) httpSession.getAttribute("real_user_id");

			if (userId == null)
			{
				return null;
			}

			sessionModelImpl.setRealUserId(userId);
		}

		return userService.getUserById(userId);
	}


	public void setUser(UserModel userModel)
	{
		int userId = userModel.getUserId();
		sessionModelImpl.setUserId(userId);
		httpSession.setAttribute("user_id", userId);
	}

	public void setRealUser(UserModel userModel)
	{
		int userId = userModel.getUserId();
		sessionModelImpl.setRealUserId(userId);
		httpSession.setAttribute("real_user_id", userId);

	}

	public String getXsrfToken()
	{
		return (String) httpSession.getAttribute("xsrf_token");
	}

	public void clearSession()
	{
		sessionModelImpl.setUserId(0);
		sessionModelImpl.setRealUserId(0);

		httpSession.removeAttribute("logged_in");
		httpSession.removeAttribute("real_user_id");
		httpSession.removeAttribute("user_id");
        httpSession.removeAttribute("platform_oauth_token");
	}

    public String getPlatformOAuthToken() throws Exception {

        String token = null;

        if(httpSession.getAttribute("platform_oauth_token") != null){
            token = (String)httpSession.getAttribute("platform_oauth_token");

            boolean isValidToken = oAuthService.isValidToken(token);

            if(!isValidToken) {
                token = null;
            }
        }

        if(token == null){
            token = oAuthService.getPlatformBearerTokenForUser(getUser());
            httpSession.setAttribute("platform_oauth_token",token);
        }

        return token;
    }

}
