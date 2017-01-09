package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.AdUnit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by jhaygood on 4/9/15.
 * Updated by Brian.Bober 5/9/16
 */
public interface AdUnitRepository extends CrudRepository<AdUnit, Integer>
{
    Iterable<AdUnit> findAllByProductId(int id);

    @Query(value = "SELECT au.* FROM ad_units au INNER JOIN adconfig_inpage ac ON ac.backup_adunit_id = au.id INNER JOIN products prd ON prd.id = au.product_id INNER JOIN sites s ON s.id = prd.site_id INNER JOIN account_publishers p ON p.account_id = s.account_id INNER JOIN accounts a ON a.id = p.account_id WHERE a.parent_account_id <=> ?1",nativeQuery = true)
	List<AdUnit> findAllBackupAdUnitsByNetworkId(Integer accountNetworkId);

    @Query(value = "SELECT au.* FROM ad_units au INNER JOIN adconfig_inpage ac ON ac.video_adunit_id = au.id INNER JOIN products prd ON prd.id = au.product_id INNER JOIN sites s ON s.id = prd.site_id INNER JOIN account_publishers p ON p.account_id = s.account_id INNER JOIN accounts a ON a.id = p.account_id WHERE a.parent_account_id <=> ?1",nativeQuery = true)
    List<AdUnit> findAllVideoAdUnitsByNetworkId(Integer accountNetworkId);

	@Query(value = "SELECT au.* FROM ad_units au INNER JOIN adconfig_inpage ac ON ac.mobile_adunit_id = au.id INNER JOIN products prd ON prd.id = au.product_id INNER JOIN sites s ON s.id = prd.site_id INNER JOIN account_publishers p ON p.account_id = s.account_id INNER JOIN accounts a ON a.id = p.account_id WHERE a.parent_account_id <=> ?1",nativeQuery = true)
	List<AdUnit> findAllMobileAdUnitsByNetworkId(Integer accountNetworkId);



	@Query(value = "SELECT au.* FROM ad_units au INNER JOIN adconfig_inpage ac ON ac.combined_adunit_id = au.id INNER JOIN products prd ON prd.id = au.product_id INNER JOIN sites s ON s.id = prd.site_id INNER JOIN account_publishers p ON p.account_id = s.account_id WHERE p.account_id <=> ?1",nativeQuery = true)
	List<AdUnit> findAllFirstLookAdUnitsByPublisherId(Integer accountPublisherId);

	@Query(value = "SELECT au.* FROM ad_units au INNER JOIN adconfig_inpage ac ON ac.backup_adunit_id = au.id INNER JOIN products prd ON prd.id = au.product_id INNER JOIN sites s ON s.id = prd.site_id INNER JOIN account_publishers p ON p.account_id = s.account_id WHERE p.account_id <=> ?1",nativeQuery = true)
	List<AdUnit> findAllBackupAdUnitsByPublisherId(Integer accountPublisherId);

	@Query(value = "SELECT au.* FROM ad_units au INNER JOIN adconfig_inpage ac ON ac.video_adunit_id = au.id INNER JOIN products prd ON prd.id = au.product_id INNER JOIN sites s ON s.id = prd.site_id INNER JOIN account_publishers p ON p.account_id = s.account_id WHERE p.account_id <=> ?1",nativeQuery = true)
	List<AdUnit> findAllVideoAdUnitsByPublisherId(Integer accountPublisherId);

	@Query(value = "SELECT au.* FROM ad_units au INNER JOIN adconfig_inpage ac ON ac.mobile_adunit_id = au.id INNER JOIN products prd ON prd.id = au.product_id INNER JOIN sites s ON s.id = prd.site_id INNER JOIN account_publishers p ON p.account_id = s.account_id WHERE p.account_id <=> ?1",nativeQuery = true)
	List<AdUnit> findAllMobileAdUnitsByPublisherId(Integer accountPublisherId);



	@Query(value = "SELECT au.* FROM ad_units au INNER JOIN adconfig_inpage ac ON ac.combined_adunit_id = au.id INNER JOIN products prd ON prd.id = au.product_id INNER JOIN sites s ON s.id = prd.site_id WHERE s.id <=> ?1",nativeQuery = true)
	List<AdUnit> findAllFirstLookAdUnitsBySiteId(Integer siteId);

	@Query(value = "SELECT au.* FROM ad_units au INNER JOIN adconfig_inpage ac ON ac.backup_adunit_id = au.id INNER JOIN products prd ON prd.id = au.product_id INNER JOIN sites s ON s.id = prd.site_id WHERE s.id <=> ?1",nativeQuery = true)
	List<AdUnit> findAllBackupAdUnitsBySiteId(Integer siteId);

	@Query(value = "SELECT au.* FROM ad_units au INNER JOIN adconfig_inpage ac ON ac.video_adunit_id = au.id INNER JOIN products prd ON prd.id = au.product_id INNER JOIN sites s ON s.id = prd.site_id WHERE s.id <=> ?1",nativeQuery = true)
	List<AdUnit> findAllVideoAdUnitsBySiteId(Integer siteId);

	@Query(value = "SELECT au.* FROM ad_units au INNER JOIN adconfig_inpage ac ON ac.mobile_adunit_id = au.id INNER JOIN products prd ON prd.id = au.product_id INNER JOIN sites s ON s.id = prd.site_id WHERE s.id <=> ?1",nativeQuery = true)
	List<AdUnit> findAllMobileAdUnitsBySiteId(Integer siteId);



	@Query(value = "SELECT au.* FROM ad_units au INNER JOIN adconfig_inpage ac ON ac.combined_adunit_id = au.id INNER JOIN products prd ON prd.id = au.product_id WHERE prd.id <=> ?1",nativeQuery = true)
	List<AdUnit> findAllFirstLookAdUnitsByProductId(Integer productId);

	@Query(value = "SELECT au.* FROM ad_units au INNER JOIN adconfig_inpage ac ON ac.backup_adunit_id = au.id INNER JOIN products prd ON prd.id = au.product_id WHERE prd.id <=> ?1",nativeQuery = true)
	List<AdUnit> findAllBackupAdUnitsByProductId(Integer productId);

	@Query(value = "SELECT au.* FROM ad_units au INNER JOIN adconfig_inpage ac ON ac.video_adunit_id = au.id INNER JOIN products prd ON prd.id = au.product_id WHERE prd.id <=> ?1",nativeQuery = true)
	List<AdUnit> findAllVideoAdUnitsByProductId(Integer productId);

	@Query(value = "SELECT au.* FROM ad_units au INNER JOIN adconfig_inpage ac ON ac.mobile_adunit_id = au.id INNER JOIN products prd ON prd.id = au.product_id WHERE prd.id <=> ?1",nativeQuery = true)
	List<AdUnit> findAllMobileAdUnitsByProductId(Integer productId);

	List<AdUnit> findAllByAdServerUnitId(String externalId);
}
