package com.wahwahnetworks.platform.controllers.api;

import com.wahwahnetworks.platform.annotations.HasUserRole;
import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import com.wahwahnetworks.platform.models.web.APIKeyListModel;
import com.wahwahnetworks.platform.models.web.APIKeyModel;
import com.wahwahnetworks.platform.services.APIKeysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Justin.Haygood on 8/28/2014.
 */

@RestController
@Scope("request")
@RequestMapping("/api/1.0/api-keys")
public class APIKeysController extends BaseAPIController
{

	@Autowired
	private APIKeysService apiKeysService;

	@RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
	@HasUserRole(UserRoleType.INTERNAL_USER)
	public APIKeyListModel listKeys()
	{

		APIKeyListModel keyListModel = new APIKeyListModel(apiKeysService.getAll());
		return keyListModel;
	}

	@RequestMapping(value = "/", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@HasUserRole(UserRoleType.SUPER_USER)
	public APIKeyModel addKey(@RequestBody APIKeyModel apiKeyModel)
	{
		return apiKeysService.addKey(apiKeyModel);
	}

	@RequestMapping(value = "/widget-{widgetId}", method = RequestMethod.GET, produces = "application/json")
	@HasUserRole(UserRoleType.INTERNAL_USER)
	public APIKeyModel getKeyByWidgetId(@PathVariable int widgetId)
	{
		return apiKeysService.getForWidgetId(widgetId);
	}

	@RequestMapping(value = "/widget-{widgetId}", method = RequestMethod.PUT, produces = "application/json")
	@HasUserRole(UserRoleType.SUPER_USER)
	public APIKeyModel updateKey(@PathVariable int widgetId, @RequestBody APIKeyModel updateModel)
	{
		return apiKeysService.updateKey(widgetId, updateModel);
	}

	@RequestMapping(value = "/widget-{widgetId}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@HasUserRole(UserRoleType.SUPER_USER)
	public void deleteKey(@PathVariable int widgetId)
	{
		apiKeysService.deleteByWidgetId(widgetId);
	}
}
