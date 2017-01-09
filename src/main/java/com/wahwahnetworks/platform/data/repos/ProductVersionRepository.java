package com.wahwahnetworks.platform.data.repos;

import com.wahwahnetworks.platform.data.entities.ProductVersion;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Justin on 2/6/2015.
 */
public interface ProductVersionRepository extends CrudRepository<ProductVersion, Integer>
{
	ProductVersion findByGitBranchName(String gitBranchName);

	List<ProductVersion> findByIsObsoleteFalseAndIsReleasedTrue();

	List<ProductVersion> findByIsObsoleteFalse();

	ProductVersion findByIsDefaultTrue();
}
