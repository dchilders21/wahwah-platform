package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.data.entities.*;
import com.wahwahnetworks.platform.data.entities.enums.AccountType;
import com.wahwahnetworks.platform.data.repos.ProductRepository;
import com.wahwahnetworks.platform.data.repos.ReportingProAccountRepository;
import com.wahwahnetworks.platform.data.repos.SiteRepository;
import com.wahwahnetworks.platform.exceptions.EntityNotPermittedException;
import com.wahwahnetworks.platform.models.UserModel;
import com.wahwahnetworks.platform.models.web.ReportingProAccountWebModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhaygood on 7/14/16.
 */

@Service
public class ReportingProAccountService {

    @Autowired
    private ReportingProAccountRepository reportingProAccountRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private SiteService siteService;

    @Autowired
    private PublisherService publisherService;

    @Transactional(readOnly = true)
    public ReportingProAccountWebModel getAccountById(int reportingProAccountId, UserModel userModel){
        if(userModel.getAccountType() != AccountType.ROOT && userModel.getAccountType() != AccountType.REPORTING_PRO){
            throw new EntityNotPermittedException("You do not have permission to access this account");
        }

        if(userModel.getAccountType() == AccountType.REPORTING_PRO){
            if(userModel.getAccountId() != reportingProAccountId){
                throw new EntityNotPermittedException("You do not have permission to access this account");
            }
        }

        AccountReportingPro reportingProAccount = reportingProAccountRepository.findOne(reportingProAccountId);
        return new ReportingProAccountWebModel(reportingProAccount);
    }

    @Transactional
    public void convertNetworkToReportingPro(AccountNetwork network, UserModel userModel) throws Exception {

        for(Account publisherAccounts : network.getChildAccounts()){
            AccountPublisher publisher = (AccountPublisher)publisherAccounts;

            for(Site site : publisher.getSites()){

                List<Product> productList = new ArrayList<>();
                productList.addAll(site.getProducts());

                for(Product product : productList){
                    productRepository.delete(product);
                }

                site.getProducts().clear();

                if(site.getMarketplaceSite() != null){
                    Site marketplaceSite = site.getMarketplaceSite();
                    site.setMarketplaceSite(null);
                    siteService.deleteSite(marketplaceSite,userModel,false);
                }

                siteRepository.save(site);
            }

            if(publisher.getMarketplacePublisher() != null){
                AccountPublisher marketplacePublisher = publisher.getMarketplacePublisher();
                publisher.setMarketplacePublisher(null);
                publisherService.deletePublisher(marketplacePublisher,userModel,false);
            }
        }

        entityManager.flush();

        jdbcTemplate.update("UPDATE accounts SET type = 'REPORTING_PRO' WHERE id = ?",network.getId());
        jdbcTemplate.update("DELETE FROM account_networks WHERE account_id = ?",network.getId());
        jdbcTemplate.update("INSERT INTO account_reporting_pro (account_id) VALUES(?)",network.getId());

        entityManager.clear();
    }
}
