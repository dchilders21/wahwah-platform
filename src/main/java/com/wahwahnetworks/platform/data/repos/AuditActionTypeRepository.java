package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.AuditActionType;
import com.wahwahnetworks.platform.data.entities.enums.AuditActionTypeEnum;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Justin on 1/1/2015.
 */
public interface AuditActionTypeRepository extends CrudRepository<AuditActionType, Integer>
{
	AuditActionType findByValue(AuditActionTypeEnum value);
}
