package com.wahwahnetworks.platform.data.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * Created by jhaygood on 7/13/16.
 */

@Entity
@DiscriminatorValue("REPORTING_PRO")
@Table(name="account_reporting_pro")
@PrimaryKeyJoinColumn(name = "account_id", referencedColumnName = "id")
public class AccountReportingPro extends Account {
}
