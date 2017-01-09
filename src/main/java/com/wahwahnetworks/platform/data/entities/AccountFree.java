package com.wahwahnetworks.platform.data.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * Created by jhaygood on 3/3/16.
 */

@Entity
@DiscriminatorValue("FREE")
@Table(name="account_free")
@PrimaryKeyJoinColumn(name = "account_id", referencedColumnName = "id")
public class AccountFree extends Account {
}
