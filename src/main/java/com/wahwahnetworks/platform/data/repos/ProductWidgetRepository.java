package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.ProductWidget;
import com.wahwahnetworks.platform.data.entities.enums.Environment;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by jhaygood on 5/25/16.
 */
public interface ProductWidgetRepository extends CrudRepository<ProductWidget,Integer> {
    ProductWidget findByEnvironmentAndProductId(Environment environment, int productId);
}
