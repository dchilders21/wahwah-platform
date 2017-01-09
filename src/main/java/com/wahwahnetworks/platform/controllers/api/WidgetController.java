package com.wahwahnetworks.platform.controllers.api;

import com.wahwahnetworks.platform.annotations.RestNoAuthentication;
import com.wahwahnetworks.platform.data.entities.ProductWidget;
import com.wahwahnetworks.platform.data.entities.enums.Environment;
import com.wahwahnetworks.platform.models.web.ProductWidgetModel;
import com.wahwahnetworks.platform.services.ProductWidgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jhaygood on 5/25/16.
 */

@RestController
@RestNoAuthentication
@RequestMapping("/api/1.0/widget/")
@Scope("request")
public class WidgetController extends BaseAPIController {

    @Autowired
    private ProductWidgetService widgetService;

    @RequestMapping(value = "/{environment}/{productId}", method = RequestMethod.GET)
    public ProductWidgetModel getWidget(@PathVariable("environment") Environment environment, @PathVariable("productId") int productId){
        ProductWidget widget = widgetService.createWidget(environment,productId);

        ProductWidgetModel widgetModel = new ProductWidgetModel(widget.getId(),widget.getEnvironment(),widget.getProductId());
        return widgetModel;
    }
}
