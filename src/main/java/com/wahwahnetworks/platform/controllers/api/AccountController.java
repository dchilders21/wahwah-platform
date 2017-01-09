package com.wahwahnetworks.platform.controllers.api;

import com.wahwahnetworks.platform.annotations.HasUserRole;
import com.wahwahnetworks.platform.data.entities.enums.AccountType;
import com.wahwahnetworks.platform.data.entities.enums.UserRoleType;
import com.wahwahnetworks.platform.models.SessionModel;
import com.wahwahnetworks.platform.models.web.AccountWebListModel;
import com.wahwahnetworks.platform.models.web.AccountWebModel;
import com.wahwahnetworks.platform.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Brian.Bober on 1/20/2016.
 */

@RestController
@Scope("request")
@RequestMapping("/api/1.0/accounts")
public class AccountController extends BaseAPIController
{
	@Autowired
	private AccountService accountService;

	@Autowired
	private SessionModel sessionModel;

	@RequestMapping(value = "/query/{query}", method = RequestMethod.GET, produces = "application/json")
    @HasUserRole(UserRoleType.NETWORK_ADMIN)
	public AccountWebListModel search(@PathVariable String query)
	{
		List<AccountWebModel> accountWebModelList = accountService.query(query, sessionModel.getUser());
		return new AccountWebListModel(accountWebModelList);
	}

    @RequestMapping(value = "{accountId}", method = RequestMethod.GET)
    @HasUserRole(UserRoleType.INTERNAL_USER)
    public AccountWebModel getAccountById(@PathVariable int accountId){
        return accountService.getById(accountId);
    }

	@RequestMapping(value = "request-upgrade", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @HasUserRole(UserRoleType.USER)
	public void sendUpgradeRequest(){
        accountService.sendUpgradeRequest(sessionModel.getUser());
	}

    @RequestMapping(value = "perform-upgrade/reporting-pro", method = RequestMethod.POST)
    @HasUserRole(UserRoleType.INTERNAL_USER)
    public AccountWebModel convertToReportingPro(@RequestBody int accountId) throws Exception {
        accountService.upgradeAccount(sessionModel.getUser(),accountId, AccountType.REPORTING_PRO);
        return accountService.getById(accountId);
    }

    @RequestMapping(value = "perform-upgrade", method = RequestMethod.POST)
    @HasUserRole(UserRoleType.INTERNAL_USER)
    public AccountWebModel convertToNetwork(@RequestBody int accountId) throws Exception {
        accountService.upgradeAccount(sessionModel.getUser(),accountId, AccountType.NETWORK);
        return accountService.getById(accountId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "can-upgrade")
    @HasUserRole(UserRoleType.INTERNAL_USER)
	public AccountWebListModel listUpgradeCapableAccounts(@RequestParam(value = "page", defaultValue = "0") Integer pageNumber, @RequestParam(value = "size", defaultValue = "25") Integer pageSize){

        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(new Sort.Order(Sort.Direction.ASC, "name")));

        return accountService.listAccountsToUpgrade(sessionModel.getUser(),pageable);
	}
}
