package com.wahwahnetworks.platform.data.entities;

import com.wahwahnetworks.platform.data.entities.enums.ProductType;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * Created by Justin on 2/6/2015.
 */

@Entity
@Table(name = "product_versions")
public class ProductVersion
{

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "git_branch")
	private String gitBranchName;

	@Column(name = "version_name")
	private String versionName;

	@Column(name = "product_type")
	@Enumerated(EnumType.STRING)
	private ProductType productType;

	@Column(name = "is_released")
	private Boolean isReleased;

	@Column(name = "is_obsolete")
	private Boolean isObsolete;

	@Column(name = "is_default")
	private Boolean isDefault;

	@Column(name = "last_published_time")
	private java.sql.Timestamp lastPublishTime;

	@Column(name = "last_commit_id")
	private String lastCommitId;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getGitBranchName()
	{
		return gitBranchName;
	}

	public void setGitBranchName(String gitBranchName)
	{
		this.gitBranchName = gitBranchName;
	}

	public String getVersionName()
	{
		return versionName;
	}

	public void setVersionName(String versionName)
	{
		this.versionName = versionName;
	}

	public ProductType getProductType()
	{
		return productType;
	}

	public void setProductType(ProductType productType)
	{
		this.productType = productType;
	}

	public Boolean getIsReleased()
	{
		return isReleased;
	}

	public void setIsReleased(Boolean isReleased)
	{
		this.isReleased = isReleased;
	}

	public Instant getLastPublishTime()
	{
		if (lastPublishTime == null)
		{
			return null;
		}

		return lastPublishTime.toInstant();
	}

	public void setLastPublishTime(Instant lastPublishTime)
	{
		if (lastPublishTime == null)
		{
			this.lastPublishTime = null;
			return;
		}
		this.lastPublishTime = Timestamp.from(lastPublishTime);
	}

	public String getLastCommitId()
	{
		return lastCommitId;
	}

	public void setLastCommitId(String lastCommitId)
	{
		this.lastCommitId = lastCommitId;
	}

	public Boolean getIsObsolete()
	{
		return isObsolete;
	}

	public void setIsObsolete(Boolean isObsolete)
	{
		this.isObsolete = isObsolete;
	}

	public Boolean getIsDefault()
	{
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault)
	{
		this.isDefault = isDefault;
	}
}
