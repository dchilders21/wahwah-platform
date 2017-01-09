package com.wahwahnetworks.platform.data.entities;

import javax.persistence.*;

/**
 * Created by jhaygood on 4/11/16.
 */

@Entity
@Table(name = "google_data_store_values")
public class GoogleDataStoreValue {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "google_data_store_id")
    private GoogleDataStore dataStore;

    @Column(name = "data_store_key")
    private String key;

    @Column(name = "data_store_value")
    private String value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GoogleDataStore getDataStore() {
        return dataStore;
    }

    public void setDataStore(GoogleDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
