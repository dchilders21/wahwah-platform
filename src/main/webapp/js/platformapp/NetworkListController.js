wahwahApp.controller('NetworkListController', ['$scope', '$http', '$location', '$timeout', function ($scope, $http, $location, $timeout)
{

	$scope.xsrfToken = $scope.$parent.xsrfToken;

	$scope.user = {roles: []};

	$scope.showConfirmDeleteAlert = false;

	$scope.showCog = false;

	$scope.hasRole = function (roleName)
	{
		return window.hasRole($scope.user.roles, roleName);
	};

	$scope.getNetworkUniversalTagDownloadUrl = function(networkId)
	{
		return getUrl("/api/1.0/networks/universaltag/txt/" + networkId) + "?xsrf_token=" + $scope.xsrfToken;
	}

	$scope.getNetworkAllTagDownloadUrl = function (networkId)
	{
		// Get ALL sites, not just page 1
		return getUrl("/api/1.0/networks/tags/txt/") + networkId + "?xsrf_token=" + $scope.xsrfToken;
	}

	$http.get(getUrl("/api/1.0/users/current/")).success(function (data)
	{
		$scope.user = data;
	});

	$scope.users = [];

	$scope.currentPageSize = 25;


	$scope.errorMsg = "";
	$scope.showError = false;

	$scope.refreshNetworks = function()
	{
		loadNetworks(0, $scope.currentPageSize);
	};

	function loadNetworks(pageNumber, pageSize)
	{
		$http.get(getUrl("/api/1.0/networks/?page=" + pageNumber + "&size=" + pageSize)).success(function (data)
		{
			$scope.networks = data.networks;
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

		loadNetworks(pageIndex, $scope.currentPageSize);
	};

	$scope.refreshNetworks();

	$scope.deleteNetwork = function (pub)
	{
		$scope.showConfirmDeleteAlert = true;
		$scope.networkToDelete = pub;
	};

	$scope.hideDeleteWarning = function ()
	{
		$scope.showConfirmDeleteAlert = false;
	};

	$scope.performDelete = function ()
	{
		$scope.showCog = true;

		$http.delete(getUrl("/api/1.0/networks/" + $scope.networkToDelete.id)).success(function ()
		{
			$scope.showConfirmDeleteAlert = false;
			$scope.showCog = false;
			$scope.refreshNetworks();
			$scope.networkToDelete = null;
		}).error(function()
		{
			$scope.showConfirmDeleteAlert = false;
			$scope.showError = true;
			$scope.errorMsg = "Unable to delete " + $scope.networkToDelete.name;
			$scope.networkToDelete = null;
			$scope.refreshNetworks();
			$timeout(function(){
				$scope.showError = false;
			},15000);
		});
	};

	$scope.selectNetwork = function (network)
	{
		if(network.is_single_publisher){
			$location.path("publisher-" + network.single_publisher_id + "/sites/");
		} else {
			$location.path("/network-" + network.id + "/publishers/");
		}
	}

}]);
