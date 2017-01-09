/**
 * Created by Brian.Bober on 12/22/2014.
 */

wahwahApp.controller('ProductListController', ['$scope', '$http', '$location', function ($scope, $http, $location)
{


	$scope.xsrfToken = $scope.$parent.xsrfToken;

	$scope.user = {roles: []};

	$scope.loaded = false;

	$scope.showConfirmDeleteAlert = false;

	$scope.hasRole = function (roleName)
	{
		return window.hasRole($scope.user.roles, roleName);
	};

	$http.get(getUrl("/api/1.0/users/current/")).success(function (data)
	{
		$scope.user = data;
	});

	$scope.sites = [];

	function getSiteList()
	{
		$http.get(getUrl("/api/1.0/sites?page=0&size=100000")).success(function (data)
		{
			$scope.sites = data.sites;
		});
	}

	$scope.products = [];

	$scope.selectedProduct = null;

	$scope.productTypes = {TOOLBAR: 'Toolbar', STANDALONE_AD: 'Ad Only'};

	$scope.currentPageSize = 25;

	function refreshProducts()
	{
		loadProducts(0, $scope.currentPageSize);
	}

	function loadProducts(pageNumber, pageSize)
	{
		$http.get(getUrl("/api/1.0/products/?page=" + pageNumber + "&size=" + pageSize)).success(function (data)
		{
			$scope.products = data.products;

			$scope.page = {};
			$scope.page.current = data.page_current;
			$scope.page.size = data.page_size;
			$scope.page.total = data.page_totalcount;

			$scope.pages = [];

			for (var i = 0; i < $scope.page.total; i++)
			{
				var page = {};
				page.index = i;
				page.number = i + 1;
				$scope.pages.push(page);
			}

			$scope.loaded = true;

		});
	}

	$scope.openPage = function (pageIndex)
	{

		if ($scope.page.current == 0 && pageIndex == "previous")
		{
			return;
		}

		if ($scope.page.current == $scope.page.total - 1 && pageIndex == "next")
		{
			return;
		}

		if (pageIndex == "previous")
		{
			pageIndex = $scope.page.current - 1;
		}

		if (pageIndex == "next")
		{
			pageIndex = $scope.page.current + 1;
		}

		loadProducts(pageIndex, $scope.currentPageSize);
	}

	getSiteList();
	refreshProducts();
	// products won't be set yet here

	$scope.getSiteById = function (id)
	{
		for (var i = 0; i < $scope.sites.length; i++)
		{
			if ($scope.sites[i].id == id)
			{
				return $scope.sites[i];
			}
		}
		return {site_name: "", account_name: ""};
	}

	$scope.getProductType = function(product)
	{
		switch(product.type )
		{ // Need to make changes on $scope.currentTag because changeOptions hasn't been run yet
			case 'TOOLBAR':
				return "Toolbar";
				break;
			case 'STANDALONE_AD':
				if (product.standalone_ad.ad_format == "floater")
				{
					return "Ad-Only Floater";
				}
				else if (product.standalone_ad.ad_format == "banner")
				{
					return "Ad-Only In-Banner";
				}
				break;
		}
		return "Media Portal";
	}

	$scope.deleteProduct = function (product)
	{
		$scope.productToDelete = product;
		$scope.showConfirmDeleteAlert = true;
	}


	$scope.hideDeleteWarning = function ()
	{
		$scope.showConfirmDeleteAlert = false;
	}

	$scope.performDelete = function ()
	{
		$scope.showConfirmDeleteAlert = false;
		$http.delete(getUrl("/api/1.0/products/" + $scope.productToDelete.id)).success(function ()
		{
			refreshProducts();
		});
	}

	$scope.selectProduct = function (product)
	{
		//console.log('/product/ ' + product.id)
		//$location.path("/products/" + product.id);
		$scope.$apply()
		{
			if ($scope.selectedProduct == product)
				$scope.selectedProduct = null;
			else
				$scope.selectedProduct = product;
		}
	}


	$scope.startPublish = function (product)
	{
		$scope.$parent.productsToPublish = [product];
	};

	$scope.getTagDownloadUrl = function (product)
	{
		return getUrl("/api/1.0/sites/tags/txt/") + product.site_id + "?xsrf_token=" + $scope.xsrfToken;
	};

	$scope.editTag = function(product)
	{
		$location.path("/tagsettings/" + product.site_id + "/by-tag/" + product.id);
	};

}]);
