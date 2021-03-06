package com.wahwahnetworks.platform.data.entities;

import javax.persistence.*;

/**
 * Created by jhaygood on 4/20/16.
 */

@Entity
@Table(name = "demand_source_publisher_priorities")
public class DemandSourcePublisherPriority {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "network_id")
    private AccountNetwork network;

    @ManyToOne
    @JoinColumn(name = "demand_source_id")
    private DemandSource demandSource;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private AccountPublisher publisher;

    @Column(name = "priority")
    private int priority;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AccountNetwork getNetwork() {
        return network;
    }

    public void setNetwork(AccountNetwork network) {
        this.network = network;
    }

    public DemandSource getDemandSource() {
        return demandSource;
    }

    public void setDemandSource(DemandSource demandSource) {
        this.demandSource = demandSource;
    }

    public AccountPublisher getPublisher() {
        return publisher;
    }

    public void setPublisher(AccountPublisher publisher) {
        this.publisher = publisher;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
