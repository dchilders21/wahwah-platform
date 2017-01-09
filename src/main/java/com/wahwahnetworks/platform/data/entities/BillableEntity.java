package com.wahwahnetworks.platform.data.entities;

import com.wahwahnetworks.platform.data.entities.enums.BillableEntityType;

import javax.persistence.*;

/**
 * Created by jhaygood on 3/16/16.
 */

@Entity
@Table(name = "billable_entities")
public class BillableEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    private BillableEntityType entityType;

    @ManyToOne
    @JoinColumn(name = "network_id")
    private AccountNetwork network;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private AccountPublisher publisher;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "demand_source_placement_id")
    private DemandSourcePlacement placement;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BillableEntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(BillableEntityType entityType) {
        this.entityType = entityType;
    }

    public AccountNetwork getNetwork() {
        return network;
    }

    public void setNetwork(AccountNetwork network) {
        this.network = network;
    }

    public AccountPublisher getPublisher() {
        return publisher;
    }

    public void setPublisher(AccountPublisher publisher) {
        this.publisher = publisher;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public DemandSourcePlacement getPlacement() {
        return placement;
    }

    public void setPlacement(DemandSourcePlacement placement) {
        this.placement = placement;
    }
}
