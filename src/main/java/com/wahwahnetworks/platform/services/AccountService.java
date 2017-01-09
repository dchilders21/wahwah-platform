package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.data.entities.Account;
import com.wahwahnetworks.platform.data.entities.AccountFree;
import com.wahwahnetworks.platform.data.entities.AccountNetwork;
import com.wahwahnetworks.platform.data.entities.AccountPublisher;
import com.wahwahnetworks.platform.data.entities.enums.AccountType;
import com.wahwahnetworks.platform.data.repos.AccountRepository;
import com.wahwahnetworks.platform.exceptions.EntityNotPermittedException;
import com.wahwahnetworks.platform.models.UserModel;
import com.wahwahnetworks.platform.models.web.AccountWebListModel;
import com.wahwahnetworks.platform.models.web.AccountWebModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Brian.Bober 1/20/2016
 */

@Service
public class AccountService
{
	@Autowired
	AccountRepository accountRepository;

	@Autowired
	PublisherService publisherService;

	@Autowired
	NetworkService networkService;

    @Autowired
    ReportingProAccountService reportingProAccountService;

    @Autowired
    private MailSender mailSender;

    @Transactional(readOnly = true)
	public Account findByName(String accountName)
	{
		return accountRepository.findByName(accountName);
	}

	@Transactional(readOnly = true)
	public Account findById(Integer accountId)
	{
		return accountRepository.findOne(accountId);
	}

    @Transactional(readOnly = true)
    public AccountWebModel getById(int accountId){
        Account account = accountRepository.findOne(accountId);
        return new AccountWebModel(account);
    }

	@Transactional(readOnly = true)
	public List<AccountWebModel> query(String query, UserModel userModel)
	{
		Iterable<Account> accounts = accountRepository.findByNameStartsWith(query);

		List<AccountWebModel> accountWebModelList = new ArrayList<>();

		for (Account account : accounts)
		{
			AccountWebModel accountModel = null;

			if (account instanceof AccountPublisher)
            {
                boolean returnPublisher = false;

                if(userModel.getAccountType() == AccountType.ROOT){
                    returnPublisher = true;
                }

                if(userModel.getAccountType() == AccountType.NETWORK){
                    if(account.getParentAccount() != null && account.getParentAccount().getId() == userModel.getAccountId()){
                        returnPublisher = true;
                    }
                }

                if(userModel.getAccountType() == AccountType.PUBLISHER){
                    if(account.getId() == userModel.getAccountId()){
                        returnPublisher = true;
                    }
                }

                if(returnPublisher){
                    accountModel = publisherService.getPublisherById(account.getId(), userModel);
                }

            }
            else if (account instanceof AccountNetwork)
            {
                boolean returnNetwork = false;

                if(userModel.getAccountType() == AccountType.ROOT){
                    returnNetwork = true;
                }

                if(userModel.getAccountType() == AccountType.NETWORK){
                    if(account.getId() == userModel.getAccountId()){
                        returnNetwork = true;
                    }
                }

                if(returnNetwork){
                    accountModel = networkService.getNetworkModelById(account.getId(), userModel);
                }

            }

			if (accountModel != null)
            {
                accountWebModelList.add(accountModel);
            }
		}

		return accountWebModelList;
	}

    @Transactional
	public void sendUpgradeRequest(UserModel userModel){
        StringBuilder upgradeRequestMessage = new StringBuilder();
        upgradeRequestMessage.append("Red Panda Platform Team,\r\n\r\n");
        upgradeRequestMessage.append("Upgrade Request From: ").append(userModel.getFirstName()).append(" ").append(userModel.getLastName()).append(" at ").append(userModel.getAccountName()).append("\r\n");
        upgradeRequestMessage.append("Contact Email: ").append(userModel.getEmailAddress());

        SimpleMailMessage requestMessage = new SimpleMailMessage();
        requestMessage.setFrom("hello@redpandaplatform.com");
        requestMessage.setTo("hello@redpandaplatform.com");
		requestMessage.setReplyTo(userModel.getEmailAddress());
        requestMessage.setSubject("Free Account Upgrade Request");
        requestMessage.setText(upgradeRequestMessage.toString());

        mailSender.send(requestMessage);
	}

    @Transactional(readOnly = true)
	public AccountWebListModel listAccountsToUpgrade(UserModel userModel, Pageable pageable){

        List<Class> accountTypesToUpgrade = Arrays.asList(AccountFree.class);

		Account parentAccount = null;

        if(userModel.getAccountId() != null){
            throw new EntityNotPermittedException("Only ROOT users can list upgradable accounts");
        }

		Page<Account> accounts = accountRepository.findByParentAccountIsNullAndAccountTypeIn(accountTypesToUpgrade,pageable);

        AccountWebListModel accountWebListModel = new AccountWebListModel(accounts,parentAccount);
        return accountWebListModel;
	}

    @Transactional
    public void upgradeAccount(UserModel userModel, int accountId, AccountType targetAccountType) throws Exception {

        Account account = accountRepository.findOne(accountId);

        if(account.getAccountType() == AccountType.FREE && targetAccountType == AccountType.NETWORK){
            AccountFree freeAccount = (AccountFree)account;
            networkService.convertFreeAccountToNetwork(freeAccount,userModel);
        }

        if(account.getAccountType() == AccountType.NETWORK && targetAccountType == AccountType.REPORTING_PRO){
            AccountNetwork networkAccount = (AccountNetwork)account;
            reportingProAccountService.convertNetworkToReportingPro(networkAccount,userModel);
        }
    }
}
