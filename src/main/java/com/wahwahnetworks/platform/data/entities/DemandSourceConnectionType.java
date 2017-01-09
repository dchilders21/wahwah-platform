package com.wahwahnetworks.platform.data.entities;

import javax.persistence.*;

/**
 * Created by jhaygood on 1/27/16.
 */

@Entity
@Table(name = "demand_source_connection_types")
public class DemandSourceConnectionType {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "type_key")
    private String typeKey;

    @Column(name = "name")
    private String name;

    @Column(name = "connection_metadata")
    private String connectionMetaData;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeKey() {
        return typeKey;
    }

    public void setTypeKey(String typeKey) {
        this.typeKey = typeKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConnectionMetaData() {
        return connectionMetaData;
    }

    public void setConnectionMetaData(String connectionMetaData) {
        this.connectionMetaData = connectionMetaData;
    }
}
