package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.data.entities.*;
import com.wahwahnetworks.platform.data.repos.*;
import com.wahwahnetworks.platform.models.web.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian.Bober on 5/26/2015.
 */
@Service
public class SearchService
{

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	SiteRepository siteRepository;

	@Autowired
	AccountPublisherRepository publisherRepository;

	@Autowired
	AccountNetworkRepository networkRepository;

	@Autowired
	AdUnitRepository adUnitRepository;

	@Autowired
	PublisherService publisherService;


	@Transactional
	public SearchResultsModel search(String searchTmp)
	{
		SearchResultsModel results = new SearchResultsModel();
		String searchTerm = StringUtils.strip(StringUtils.trim(searchTmp));
		if (StringUtils.isNumeric(searchTerm) && searchTerm.length() <= 9)
		{
			int num = Integer.parseInt(searchTerm);
			Site site = siteRepository.findById(num);
			AccountNetwork network = networkRepository.findOne(num);
			AccountPublisher publisher = publisherRepository.findOne(num);
			Product product1 = productRepository.findById(num);
			Product product2 = productRepository.findByWidgetId(num);
			List<AdUnit> units = adUnitRepository.findAllByAdServerUnitId(new String("" + num));

			if (network != null)
			{
				List<NetworkWebModel> networks = new ArrayList<>();
				networks.add(new NetworkWebModel(network));
				results.setNetworks(networks);
			}

			if (publisher != null)
			{
				List<PublisherWebModel> publishers = new ArrayList<>();
				publishers.add(new PublisherWebModel(publisher, publisherService.getParentRedPandaPublisherCreator(publisher),0));
				results.setPublishers(publishers);
			}

			if(site != null && site.getReplacedWith() != null){
				site = site.getReplacedWith();
			}

			if(site != null && site.isArchived()){
				site = null;
			}

			if (site != null)
			{
				List<SiteModel> sites = new ArrayList<>();
				sites.add(new SiteModel(site));
				results.setSites(sites);
			}

			if (product1 != null || product2 != null || units.size() != 0)
			{
				List<ProductModel> products = new ArrayList<ProductModel>();
				if (product1 != null)
					products.add(new ProductModel(product1,null));
				if (product2 != null)
					products.add(new ProductModel(product2,null));
				if (units.size() != 0)
				{
					for (int i = 0; i < units.size(); i++)
					{
						Product productUnit = units.get(i).getProduct();
						products.add(new ProductModel(productUnit,null));
					}
				}
				results.setProducts(products);
			}

			if ( searchTerm.length() < 4  &&// Also include name matches for longer numbers, even if it finds a mathching id
					(results.getNetworks().size() > 0
					|| results.getPublishers().size() > 0
					|| results.getSites().size() > 0
					|| results.getProducts().size() > 0
					))
			{
				return results;
			}

		}

		// Either numeric search found nothing, or not numeric
		Iterable<Site> sitesEntity = siteRepository.findBySiteNameContainingOrderBySiteNameAsc(searchTerm);
		Iterable<AccountPublisher> publishersEntity = publisherRepository.findByNameContainingOrderByNameAsc(searchTerm);
		Iterable<AccountNetwork> networksEntity = networkRepository.findByNameContainingOrderByNameAsc(searchTerm);
		Iterable<Product> productsEntity = productRepository.findByNameContainingOrderByNameAsc(searchTerm);

		if (sitesEntity != null)
		{
			for (Site site : sitesEntity)
			{
				boolean shouldAddSite = true;
				Site siteToAdd = site;

				while(siteToAdd.getReplacedWith() != null){
					siteToAdd = siteToAdd.getReplacedWith();
				}

				if(siteToAdd.isArchived()){
					shouldAddSite = false;
				}

				final Site siteToCheck = siteToAdd;

				if(results.getSites().stream().filter(s -> s.getId() == siteToCheck.getId()).count() > 0){
					shouldAddSite = false;
				}

				if(shouldAddSite) {
					results.getSites().add(new SiteModel(siteToAdd));
				}
			}
		}
		if (productsEntity != null)
		{
			for (Product product : productsEntity)
			{
				results.getProducts().add(new ProductModel(product,null));
			}
		}
		if (publishersEntity != null)
		{
			for (AccountPublisher publisher : publishersEntity)
			{
				results.getPublishers().add(new PublisherWebModel(publisher, publisherService.getParentRedPandaPublisherCreator(publisher),0));
			}
		}
		if (networksEntity != null)
		{
			for (AccountNetwork network : networksEntity)
			{
				results.getNetworks().add(new NetworkWebModel(network));
			}
		}

		return results;
	}
}