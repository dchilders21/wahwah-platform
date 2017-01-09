package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.data.entities.WidgetAPIKey;
import com.wahwahnetworks.platform.data.repos.WidgetAPIKeyRepository;
import com.wahwahnetworks.platform.models.web.APIKeyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justin.Haygood on 8/28/2014.
 */

@Service
public class APIKeysService
{

	@Autowired
	private WidgetAPIKeyRepository widgetAPIKeyRepository;

	private APIKeyModel getModelFromEntity(WidgetAPIKey widgetAPIKey)
	{
		APIKeyModel keyModel = new APIKeyModel();
		keyModel.setWidgetId(widgetAPIKey.getWidgetId());
		keyModel.setWidgetName(widgetAPIKey.getWidgetName());
		keyModel.setApiKey(widgetAPIKey.getApiKey());

		return keyModel;
	}

	public APIKeyModel getForWidgetId(int widgetId)
	{
		WidgetAPIKey widgetAPIKey = widgetAPIKeyRepository.findByWidgetId(widgetId);
		return getModelFromEntity(widgetAPIKey);
	}

	public void deleteByWidgetId(int widgetId)
	{
		WidgetAPIKey widgetAPIKey = widgetAPIKeyRepository.findByWidgetId(widgetId);
		widgetAPIKeyRepository.delete(widgetAPIKey);
	}

	public APIKeyModel addKey(APIKeyModel keyModel)
	{

		WidgetAPIKey widgetAPIKey = new WidgetAPIKey();
		widgetAPIKey.setWidgetId(keyModel.getWidgetId());
		widgetAPIKey.setWidgetName(keyModel.getWidgetName());
		widgetAPIKey.setApiKey(keyModel.getApiKey());

		widgetAPIKeyRepository.save(widgetAPIKey);

		return getModelFromEntity(widgetAPIKey);

	}

	public APIKeyModel updateKey(int widgetId, APIKeyModel keyModel)
	{
		WidgetAPIKey widgetAPIKey = widgetAPIKeyRepository.findByWidgetId(widgetId);
		widgetAPIKey.setWidgetName(keyModel.getWidgetName());
		widgetAPIKey.setApiKey(keyModel.getApiKey());
		widgetAPIKeyRepository.save(widgetAPIKey);

		return getModelFromEntity(widgetAPIKey);
	}

	public List<APIKeyModel> getAll()
	{

		List<APIKeyModel> allModels = new ArrayList<>();
		Iterable<WidgetAPIKey> all = widgetAPIKeyRepository.findAll();

		for (WidgetAPIKey widgetAPIKey : all)
		{
			APIKeyModel keyModel = getModelFromEntity(widgetAPIKey);
			allModels.add(keyModel);
		}

		return allModels;
	}
}
