package com.wahwahnetworks.platform.controllers.api;

import com.wahwahnetworks.platform.annotations.HasUserRole;
import com.wahwahnetworks.platform.annotations.RestNoAuthentication;
import com.wahwahnetworks.platform.data.entities.enums.Environment;
import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import com.wahwahnetworks.platform.models.web.EnvironmentModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Brian.Bober on 8/25/2015.
 */

@RestController
@RequestMapping("/api/1.0/system/")
@Scope("request")
public class SystemController extends BaseAPIController
{

	@Autowired
	Environment environment;

	@RequestMapping(value = "/environment/", method = RequestMethod.GET, produces = "application/json")
	@HasUserRole(UserRoleType.USER)
	public EnvironmentModel getEnvironment()
	{
		EnvironmentModel environ = new EnvironmentModel();
		environ.setEnvironment(environment);
		return environ;
	}

	// Monitor this url for general uptime monitoring, Newrelic, etc. Not for user up-time
	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	@RestNoAuthentication
	public String monitor()
	{
		return "Pong!";
	}
}
