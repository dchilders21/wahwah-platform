package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.AuditLogEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Justin on 1/1/2015.
 */
public interface AuditLogRepository extends CrudRepository<AuditLogEntry, Integer>
{
	Page<AuditLogEntry> findAll(Pageable pageable);
}
