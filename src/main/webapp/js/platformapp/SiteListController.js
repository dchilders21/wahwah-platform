wahwahApp.controller('SiteListController', ['$scope', '$http', '$user', '$location', '$timeout', '$routeParams', function ($scope, $http, $user, $location, $timeout, $routeParams)
{

	$scope.xsrfToken = $scope.$parent.xsrfToken;

	$scope.parentNetwork = null;

	$scope.options = {};
	$scope.options.seeAll = false;

	$scope.user = {roles: []};

	$scope.publisherId = (typeof($routeParams.publisherId) != "undefined") ? $routeParams.publisherId : null;
	$scope.publisherName = null;
	$scope.publisher = null;

	$scope.sites = [];

	$scope.showCog = false;

	$scope.showError = false;
	$scope.errorMsg = "";

	$scope.hasRole = function (roleName)
	{
		return $user.hasRole(roleName);
	};

	$scope.getUniversalSiteTagDownloadUrl = function(siteId)
	{
		return getUrl("/api/1.0/sites/universaltag/txt/" + siteId) + "?xsrf_token=" + $scope.xsrfToken;
	};

	$user.getUserPromise().then(function(user){
        $scope.user = user;

        if (user.accountType != "ROOT" && user.accountType != "NETWORK" && user.accountType != "REPORTING_PRO"){
            $scope.publisherId = $scope.user.accountId;
        }

        if ($scope.publisherId != null){
            $scope.getPublisherName();
        } else {
            $scope.refreshSiteList();
        }
    });

	$http.get(getUrl("/api/1.0/users/current/")).success(function (data)
	{

	});

	$scope.currentPageSize = 25;

	$scope.refreshSiteList = function ()
	{
		var page = 0;
		if (typeof($scope.page) != "undefined")
			page = $scope.page.current;
		loadSites(page, $scope.currentPageSize, $scope.options.seeAll);
	}

	function loadSites(pageNumber, pageSize, seeAll)
	{
		if (typeof(seeAll) == "undefined")
			seeAll = false;
		var publisherQuery = "";
		if ($scope.publisherId != null)
			publisherQuery = "/publisher-" + $scope.publisherId;
		var seeAllStr = ($scope.parentNetwork != null)?"":"&seeAll="+seeAll;
		$http.get(getUrl("/api/1.0/sites" + publisherQuery + "?page=" + pageNumber + "&size=" + pageSize + seeAllStr)).success(function (data)
		{
			$scope.sites = data.sites;

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

		loadSites(pageIndex, $scope.currentPageSize);
	}


	$scope.deleteSite = function (site)
	{
		$scope.showConfirmDeleteAlert = true;
		$scope.siteToDelete = site;
	}

	$scope.hideDeleteWarning = function ()
	{
		$scope.showConfirmDeleteAlert = false;
		$scope.siteToDelete = null;
	}

	$scope.performDelete = function ()
	{
		$scope.showCog = true;

		$http.delete(getUrl("/api/1.0/sites/" + $scope.siteToDelete.id)).success(function ()
		{
			$scope.showCog = false;
			$scope.showConfirmDeleteAlert = false;
			$scope.siteToDelete = null;
			$scope.refreshSiteList();
		}).error(function()
		{
			$scope.showConfirmDeleteAlert = false;
			$scope.showError = true;
			$scope.errorMsg = "Unable to delete " + $scope.siteToDelete.site_name;
			$scope.siteToDelete = null;
			$timeout(function(){
				$scope.showError = false;
			},15000);
		});
	}

	$scope.selectSite = function (site)
	{
		$location.path("/sites/" + site.id);
	};

	$scope.getPublisherName = function ()
	{

		$http.get(getUrl("/api/1.0/publishers/" + $scope.publisherId), $scope.publisherId).success(function (data)
		{
			$scope.publisher = data;
			$scope.publisherName = data.name;
			if (data.parent_account_id != null)
			{
				if(data.parent_account_type == "NETWORK"){
					$http.get(getUrl("/api/1.0/networks/" + data.parent_account_id)).success(function (data)
					{
						$scope.parentNetwork = data;
						$scope.refreshSiteList();
					});
				}

				if(data.parent_account_type == "REPORTING_PRO"){
					$http.get(getUrl("/api/1.0/reporting-pro-accounts/" + data.parent_account_id)).success(function (data)
					{
						$scope.parentNetwork = data;
						$scope.refreshSiteList();
					});
				}

			}
			else
			{
				$scope.refreshSiteList(); // Have to know if INTERNAL_USER first
			}
		});
	}

	$scope.getTagDownloadUrl = function (site)
	{
		return getUrl("/api/1.0/sites/tags/txt/") + site.id + "?xsrf_token=" + $scope.xsrfToken;
	}



		$scope.getPublisherUniversalPublisherTagDownloadUrl = function(publisherId)
		{
			return getUrl("/api/1.0/publishers/universaltag/txt/" + publisherId) + "?xsrf_token=" + $scope.xsrfToken;
		}

		$scope.getPublisherAllTagDownloadUrl = function (publisherId)
		{
			// Get ALL sites, not just page 1
			return getUrl("/api/1.0/publishers/tags/txt/") + publisherId + "?xsrf_token=" + $scope.xsrfToken;
		}


	$scope.changeSeeAll = function()
	{
		if (typeof($scope.page) == "undefined")
			return;
		$scope.refreshSiteList();
	}


}]);