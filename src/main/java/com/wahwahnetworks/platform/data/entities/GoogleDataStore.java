package com.wahwahnetworks.platform.data.entities;

import javax.persistence.*;

/**
 * Created by jhaygood on 4/11/16.
 */

@Entity
@Table(name = "google_data_store")
public class GoogleDataStore {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "google_value_id")
    private String googleValueId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGoogleValueId() {
        return googleValueId;
    }

    public void setGoogleValueId(String googleValueId) {
        this.googleValueId = googleValueId;
    }
}
