package com.wahwahnetworks.platform.data.entities;

import javax.persistence.*;

/**
 * Created by jhaygood on 4/20/16.
 */
@Entity
@Table(name = "demand_source_site_priorities")
public class DemandSourceSitePriority {
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
    @JoinColumn(name = "site_id")
    private Site site;

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

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
