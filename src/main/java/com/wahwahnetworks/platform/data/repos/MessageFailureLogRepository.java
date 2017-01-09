package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.AuditLogEntry;
import com.wahwahnetworks.platform.data.entities.MessageFailureLogEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Brian.Bober on 4/26/2016.
 */
public interface MessageFailureLogRepository extends CrudRepository<MessageFailureLogEntry, Integer>
{
	MessageFailureLogEntry findByUid(String uid);
}
