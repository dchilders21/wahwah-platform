package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.data.entities.Account;
import com.wahwahnetworks.platform.data.entities.AuditActionType;
import com.wahwahnetworks.platform.data.entities.AuditLogEntry;
import com.wahwahnetworks.platform.data.entities.User;
import com.wahwahnetworks.platform.data.entities.enums.AuditActionTypeEnum;
import com.wahwahnetworks.platform.data.repos.AuditActionTypeRepository;
import com.wahwahnetworks.platform.data.repos.AuditLogRepository;
import com.wahwahnetworks.platform.data.repos.UserRepository;
import com.wahwahnetworks.platform.models.UserModel;
import com.wahwahnetworks.platform.models.web.AuditLogEntryListModel;
import com.wahwahnetworks.platform.models.web.AuditLogEntryModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

/**
 * Created by Justin on 1/1/2015.
 */

@Service
public class AuditService
{

	@Autowired
	HttpSession httpSession;

	@Autowired
	private AuditActionTypeRepository actionTypeRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuditLogRepository auditLogRepository;

	@Autowired
	private AccountService accountService;

	@Transactional
	public void addAuditEntry(UserModel userToLog, AuditActionTypeEnum actionTypeEnum, String description, String comment)
	{
		User user = userRepository.findOne(userToLog.getUserId());
		addAuditEntry(user, actionTypeEnum, description, comment);
	}

	@Transactional
	public void addAuditEntry(User userToLog, AuditActionTypeEnum actionTypeEnum, String description, String comment)
	{

		AuditActionType auditActionType = actionTypeRepository.findByValue(actionTypeEnum);

		if (auditActionType == null)
		{
			throw new RuntimeException("Missing AuditActionType in database: please add it");
		}

		AuditLogEntry auditLogEntry = new AuditLogEntry();
		auditLogEntry.setUser(userToLog);
		auditLogEntry.setAccount(userToLog.getAccount());
		auditLogEntry.setActionType(auditActionType);
		auditLogEntry.setDescription(description);
		auditLogEntry.setCommentMessage(comment);
		auditLogEntry.setEntryTime(Instant.now());

		auditLogRepository.save(auditLogEntry);
	}

	@Transactional
	public void addAuditEntry(AuditActionTypeEnum actionTypeEnum, String description, String comment)
	{
		Boolean isLoggedIn;
		try
		{
			isLoggedIn = (Boolean) httpSession.getAttribute("logged_in");
		}
		catch(Exception e)
		{
			return;
		}

		if (isLoggedIn != null && isLoggedIn == true)
		{
			Integer userId = (Integer) httpSession.getAttribute("real_user_id");
			User user = userRepository.findOne(userId);
			addAuditEntry(user, actionTypeEnum, description, comment);
		}
		else
		{
			throw new RuntimeException("User must be logged in to use this method.");
		}
	}

	@Transactional
	public AuditLogEntryListModel getRecentAuditEntries(Pageable pageable)
	{

		Page<AuditLogEntry> auditLogEntryList = auditLogRepository.findAll(pageable);

		ArrayList<AuditLogEntryModel> auditLogEntryModels = new ArrayList<>();

		for (AuditLogEntry auditLogEntry : auditLogEntryList)
		{
			auditLogEntryModels.add(getAuditLogModelFromAuditLogEntry(auditLogEntry));
		}

		AuditLogEntryListModel listModel = new AuditLogEntryListModel(auditLogEntryModels, auditLogEntryList);
		return listModel;
	}

	@Transactional
	private AuditLogEntryModel getAuditLogModelFromAuditLogEntry(AuditLogEntry auditLogEntry)
	{
		AuditLogEntryModel auditLogEntryModel = new AuditLogEntryModel();
		auditLogEntryModel.setActionType(auditLogEntry.getActionType().getName());

		OffsetDateTime entryDateTime = OffsetDateTime.ofInstant(auditLogEntry.getEntryTime(), ZoneId.of("UTC"));

		auditLogEntryModel.setEntryTime(entryDateTime);
		auditLogEntryModel.setDescription(getWebEnhancedDescription(auditLogEntry));
		auditLogEntryModel.setComment(auditLogEntry.getCommentMessage());
		auditLogEntryModel.setUserName(auditLogEntry.getUserName());
		auditLogEntryModel.setAccountName(auditLogEntry.getAccountName());

		String userEmailAddress = auditLogEntry.getUserEmailAddress();
		User user = userRepository.findByEmailAddress(userEmailAddress);

		if (user != null)
		{
			auditLogEntryModel.setUserId(user.getId());
		}

		Account account = accountService.findByName(auditLogEntry.getAccountName());

		if (account != null)
		{
			auditLogEntryModel.setAccountId(account.getId());
		}

		return auditLogEntryModel;
	}

	@Transactional
	private String getWebEnhancedDescription(AuditLogEntry auditLogEntry)
	{
		return auditLogEntry.getDescription();
	}

}
