package com.wahwahnetworks.platform.data.entities;

import com.wahwahnetworks.platform.data.entities.enums.ClientFeatureVariableType;

import javax.persistence.*;

/**
 * Created by jhaygood on 11/12/15.
 */

@Entity
@Table(name = "product_client_features")
public class ProductClientFeature {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "client_feature_id")
    private ClientFeature clientFeature;

    @Column(name = "value_string")
    private String variableValueString;

    @Column(name = "value_number")
    private Double variableValueNumber;

    @Column(name = "value_boolean")
    private Boolean variableValueBoolean;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ClientFeature getClientFeature() {
        return clientFeature;
    }

    public void setClientFeature(ClientFeature clientFeature) {
        this.clientFeature = clientFeature;
    }

    public String getVariableValueString() {

        if(clientFeature.getClientVariableType() == ClientFeatureVariableType.STRING && variableValueString == null){
            return "";
        }

        return variableValueString;
    }

    public void setVariableValueString(String variableValueString) {
        this.variableValueString = variableValueString;
    }

    public Double getVariableValueNumber() {

        if(clientFeature.getClientVariableType() == ClientFeatureVariableType.NUMBER && variableValueNumber == null){
            return 0.0;
        }

        return variableValueNumber;
    }

    public void setVariableValueNumber(Double variableValueNumber) {
        this.variableValueNumber = variableValueNumber;
    }

    public Boolean getVariableValueBoolean() {

        if(clientFeature.getClientVariableType() == ClientFeatureVariableType.BOOLEAN && variableValueBoolean == null){
            return false;
        }

        return variableValueBoolean;
    }

    public void setVariableValueBoolean(Boolean variableValueBoolean) {
        this.variableValueBoolean = variableValueBoolean;
    }
}
