package com.wahwahnetworks.platform.data.entities;

import javax.persistence.*;
import java.util.List;

/**
 * Created by jhaygood on 7/20/16.
 */

@Entity
@Table(name = "demand_source_placements")
public class DemandSourcePlacement {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "demand_source_id")
    private DemandSource demandSource;

    @Column(name = "placement_name")
    private String placementName;

    @ManyToMany
    @JoinTable(
            name = "demand_source_placements_sites",
            joinColumns = @JoinColumn(name = "demand_source_placement_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "site_id", referencedColumnName = "id")
    )
    private List<Site> sites;

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

    public String getPlacementName() {
        return placementName;
    }

    public void setPlacementName(String placementName) {
        this.placementName = placementName;
    }

    public List<Site> getSites() {
        return sites;
    }

    public void setSites(List<Site> sites) {
        this.sites = sites;
    }
}
