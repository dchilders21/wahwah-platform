package com.wahwahnetworks.platform.data.entities;

import com.wahwahnetworks.platform.data.entities.enums.ClientFeatureVariableType;

import javax.persistence.*;

/**
 * Created by jhaygood on 11/10/15.
 */

@Entity
@Table(name = "client_features")
public class ClientFeature {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "variable_name")
    private String clientVariableName;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "variable_label")
    private String clientVariableLabel;

    @Column(name = "variable_type")
    @Enumerated(EnumType.STRING)
    private ClientFeatureVariableType clientVariableType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClientVariableName() {
        return clientVariableName;
    }

    public void setClientVariableName(String clientVariableName) {
        this.clientVariableName = clientVariableName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ClientFeatureVariableType getClientVariableType() {
        return clientVariableType;
    }

    public void setClientVariableType(ClientFeatureVariableType clientVariableType) {
        this.clientVariableType = clientVariableType;
    }

    public String getClientVariableLabel() {
        return clientVariableLabel;
    }

    public void setClientVariableLabel(String clientVariableLabel) {
        this.clientVariableLabel = clientVariableLabel;
    }
}
