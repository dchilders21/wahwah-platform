package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.data.entities.Product;
import com.wahwahnetworks.platform.data.entities.Site;
import com.wahwahnetworks.platform.data.entities.User;
import com.wahwahnetworks.platform.data.repos.ProductRepository;
import com.wahwahnetworks.platform.data.repos.SiteRepository;
import com.wahwahnetworks.platform.data.repos.UserRepository;
import com.wahwahnetworks.platform.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhaygood on 1/14/16.
 */

@Service
public class SiteTagMigrationService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void migrateSiteAndTags(UserModel userModel, Site siteToMigrate, Site targetSite){

        User user = userRepository.findOne(userModel.getUserId());

        List<Product> products = new ArrayList<>();
        products.addAll(siteToMigrate.getProducts());

        for(Product product : products){
            product.setSite(targetSite);
            product.setLocked(true);
            product.setLockedUser(user);

            productRepository.save(product);

            siteToMigrate.getProducts().remove(product);
            targetSite.getProducts().add(product);
        }

        siteToMigrate.setArchived(true);
        siteToMigrate.setReplacedWith(targetSite);

        siteRepository.save(targetSite);
        siteRepository.save(siteToMigrate);
    }
}
