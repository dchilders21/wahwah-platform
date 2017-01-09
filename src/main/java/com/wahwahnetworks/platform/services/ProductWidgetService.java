package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.data.entities.ProductWidget;
import com.wahwahnetworks.platform.data.entities.enums.Environment;
import com.wahwahnetworks.platform.data.repos.ProductWidgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jhaygood on 5/25/16.
 */

@Service
public class ProductWidgetService {

    @Autowired
    private ProductWidgetRepository productWidgetRepository;

    @Transactional
    public ProductWidget createWidget(Environment environment, int productId){
        ProductWidget productWidget = productWidgetRepository.findByEnvironmentAndProductId(environment,productId);

        if(productWidget == null) {
            productWidget = new ProductWidget();
            productWidget.setEnvironment(environment);
            productWidget.setProductId(productId);
        }

        productWidgetRepository.save(productWidget);

        return productWidget;
    }
}
