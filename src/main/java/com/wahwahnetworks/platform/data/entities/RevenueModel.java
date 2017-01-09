package com.wahwahnetworks.platform.data.entities;

import com.wahwahnetworks.platform.data.entities.enums.ProductFormat;
import com.wahwahnetworks.platform.data.entities.enums.PublisherRevenueModelType;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Created by justin on 4/24/15.
 */

@Entity
@Table(name = "publisher_revenue_model")
public class RevenueModel
{
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "publisher_id")
	private AccountPublisher publisher;

	@ManyToOne
	@JoinColumn(name = "billable_entity_id")
	private BillableEntity billableEntity;

	@Column(name = "revenue_model_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private PublisherRevenueModelType revenueModelType;

    @Column(name = "format")
    @Enumerated(EnumType.STRING)
    private ProductFormat format;

	@Column(name = "revenue_share_percent")
	private Integer revenueSharePercentage;

	@Column(name = "us_desktop_gcpm_cents")
	private Integer guaranteedCPMForUSDesktopImpressions;

	@Column(name = "gcpm_cents")
	private Integer guaranteedCPMForOtherImpressions;

	@Column(name = "minimum_impressions")
	private Long minimumImpressions;

	@Column(name = "minimum_revenue_cents")
	private Integer minimumRevenueInCents;

	@Column(name = "start_date")
	private LocalDate startDate;

	@Column(name = "end_date")
	private LocalDate endDate;

	protected RevenueModel(){
	}

	public RevenueModel(BillableEntity billableEntity, PublisherRevenueModelType revenueModelType){
		setBillableEntity(billableEntity);
		setRevenueModelType(revenueModelType);
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public AccountPublisher getPublisher()
	{
		return publisher;
	}

	public void setPublisher(AccountPublisher publisher)
	{
		this.publisher = publisher;
	}

	public PublisherRevenueModelType getRevenueModelType()
	{
		return revenueModelType;
	}

	public void setRevenueModelType(PublisherRevenueModelType revenueModelType)
	{
		this.revenueModelType = revenueModelType;
	}

	public Integer getRevenueSharePercentage()
	{
		return revenueSharePercentage;
	}

	public void setRevenueSharePercentage(Integer revenueSharePercentage)
	{
		this.revenueSharePercentage = revenueSharePercentage;
	}

	public Integer getGuaranteedCPMForUSDesktopImpressions()
	{
		return guaranteedCPMForUSDesktopImpressions;
	}

	public void setGuaranteedCPMForUSDesktopImpressions(Integer guaranteedCPMForUSDesktopImpressions)
	{
		this.guaranteedCPMForUSDesktopImpressions = guaranteedCPMForUSDesktopImpressions;
	}

	public Integer getGuaranteedCPMForOtherImpressions()
	{
		return guaranteedCPMForOtherImpressions;
	}

	public void setGuaranteedCPMForOtherImpressions(Integer guaranteedCPMForOtherImpressions)
	{
		this.guaranteedCPMForOtherImpressions = guaranteedCPMForOtherImpressions;
	}

	public Long getMinimumImpressions()
	{
		return minimumImpressions;
	}

	public void setMinimumImpressions(Long minimumImpressions)
	{
		this.minimumImpressions = minimumImpressions;
	}

	public Integer getMinimumRevenueInCents()
	{
		return minimumRevenueInCents;
	}

	public void setMinimumRevenueInCents(Integer minimumRevenueInCents)
	{
		this.minimumRevenueInCents = minimumRevenueInCents;
	}

    public BillableEntity getBillableEntity() {
        return billableEntity;
    }

    public void setBillableEntity(BillableEntity billableEntity) {
        this.billableEntity = billableEntity;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public ProductFormat getFormat() {
        return format;
    }

    public void setFormat(ProductFormat format) {
        this.format = format;
    }
}
