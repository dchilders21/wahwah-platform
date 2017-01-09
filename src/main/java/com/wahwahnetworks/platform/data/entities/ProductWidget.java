package com.wahwahnetworks.platform.data.entities;

import com.wahwahnetworks.platform.data.entities.enums.Environment;

import javax.persistence.*;

/**
 * Created by jhaygood on 5/25/16.
 */

@Entity
@Table(name = "product_widgets")
public class ProductWidget {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "environment")
    @Enumerated(value = EnumType.STRING)
    private Environment environment;

    @Column(name = "product_id") // This isn't a foreign key relationship, since it might have products that are not for this environment
    private int productId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
