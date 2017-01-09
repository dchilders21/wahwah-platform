package com.wahwahnetworks.platform.data.entities;

import javax.persistence.*;

/**
 * Created by jhaygood on 1/28/16.
 */

@Entity
@Table(name = "demand_source_connections")
public class DemandSourceConnection {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "demand_source_id")
    private DemandSource demandSource;

    @ManyToOne
    @JoinColumn(name = "demand_source_connection_type_id")
    private DemandSourceConnectionType demandSourceConnectionType;

    @Column(name = "connection_details")
    private String connectionDetails;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DemandSource getDemandSource() {
        return demandSource;
    }

    public void setDemandSource(DemandSource demandSource) {
        this.demandSource = demandSource;
    }

    public DemandSourceConnectionType getDemandSourceConnectionType() {
        return demandSourceConnectionType;
    }

    public void setDemandSourceConnectionType(DemandSourceConnectionType demandSourceConnectionType) {
        this.demandSourceConnectionType = demandSourceConnectionType;
    }

    public String getConnectionDetails() {
        return connectionDetails;
    }

    public void setConnectionDetails(String connectionDetails) {
        this.connectionDetails = connectionDetails;
    }
}
