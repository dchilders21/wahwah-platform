package com.wahwahnetworks.platform.data.entities;

import com.wahwahnetworks.platform.data.entities.enums.AuditActionTypeEnum;

import javax.persistence.*;

/**
 * Created by Justin on 1/1/2015.
 */

@Entity
@Table(name = "audit_action_types")
public class AuditActionType
{

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "name")
	private String name;

	@Column(name = "value")
	@Enumerated(EnumType.STRING)
	private AuditActionTypeEnum value;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public AuditActionTypeEnum getValue()
	{
		return value;
	}

	public void setValue(AuditActionTypeEnum value)
	{
		this.value = value;
	}
}
