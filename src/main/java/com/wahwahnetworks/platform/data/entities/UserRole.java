package com.wahwahnetworks.platform.data.entities;

import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;

import javax.persistence.*;

/**
 * Created by Justin on 5/17/2014.
 */

@Entity
@Table(name = "user_roles")
public class UserRole
{

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(optional = false, cascade = CascadeType.MERGE)
	@JoinColumn(name = "user_id", nullable = false, updatable = false)
	private User user;

	@Column(name = "role_name")
	@Enumerated(EnumType.STRING)
	private UserRoleType roleType;

	protected UserRole()
	{

	}

	public UserRole(User user, UserRoleType userRoleType)
	{
		this.user = user;
		this.roleType = userRoleType;
	}

	public User getUser()
	{
		return user;
	}

	public UserRoleType getUserRoleType()
	{
		return roleType;
	}
}
