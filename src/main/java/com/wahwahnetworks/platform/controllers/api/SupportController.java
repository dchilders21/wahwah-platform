package com.wahwahnetworks.platform.controllers.api;

import com.wahwahnetworks.platform.models.SessionModel;
import com.wahwahnetworks.platform.models.SupportMessage;
import com.wahwahnetworks.platform.services.SupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Created by jhaygood on 2/23/15.
 */

@RestController
@Scope("request")
@RequestMapping("/api/1.0/support")
public class SupportController extends BaseAPIController
{

	@Autowired
	private SupportService supportService;

	@Autowired
	private SessionModel sessionModel;

	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseStatus(value = HttpStatus.CREATED)
	public void sendSupportMessage(@RequestBody SupportMessage supportMessage)
	{
		supportService.sendSupportMessage(sessionModel.getUser(), supportMessage);
	}
}
