package com.wahwahnetworks.platform.controllers.api;

import com.wahwahnetworks.platform.annotations.HasUserRole;
import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import com.wahwahnetworks.platform.models.web.AuditLogEntryListModel;
import com.wahwahnetworks.platform.services.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Justin on 1/26/2015.
 */

@RestController
@Scope("request")
@RequestMapping("/api/1.0/audit-log/")
public class AuditLogController extends BaseAPIController
{

	@Autowired
	private AuditService auditService;

	@RequestMapping(value = "/recent", method = RequestMethod.GET)
	@HasUserRole(UserRoleType.INTERNAL_USER)
	public AuditLogEntryListModel getRecentAuditLogEntries(@RequestParam(value = "page", defaultValue = "0") Integer pageNumber, @RequestParam(value = "size", defaultValue = "25") Integer pageSize)
	{
		Pageable pageable = new PageRequest(pageNumber, pageSize, Sort.Direction.DESC, "entryTime");
		return auditService.getRecentAuditEntries(pageable);
	}
}