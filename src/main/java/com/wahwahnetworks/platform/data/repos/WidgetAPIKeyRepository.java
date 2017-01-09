package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.WidgetAPIKey;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Justin.Haygood on 8/28/2014.
 */
public interface WidgetAPIKeyRepository extends CrudRepository<WidgetAPIKey, Integer>
{
	WidgetAPIKey findByWidgetId(int widgetId);
}
