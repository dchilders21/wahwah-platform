wahwahApp.controller('ProductVersionListController', ['$scope', '$http', function ($scope, $http)
{

	$scope.productVersions = [];
	$scope.listFilters = ["Released", "Released + Future", "All Versions"];
	$scope.currentListFilter = "Released";

	refreshProductVersions();

	function refreshProductVersions()
	{

		var url;

		if ($scope.currentListFilter == "Released")
		{
			url = "/api/1.0/product-versions/";
		}

		if ($scope.currentListFilter == "Released + Future")
		{
			url = "/api/1.0/product-versions/released-future";
		}

		$http.get(getUrl(url)).success(function (data)
		{
			$scope.productVersions = data.productVersions;

			for (var i = 0; i < $scope.productVersions.length; i++)
			{

				if ($scope.productVersions[i].lastPublishTime != null)
				{
					var lastPublishTime = moment($scope.productVersions[i].lastPublishTime);
					var lastPublishTimeFormatted = lastPublishTime.format("lll");

					$scope.productVersions[i].lastPublishTime = lastPublishTimeFormatted;
				}
			}
		});
	}

	$scope.$watch('currentListFilter', function ()
	{
		refreshProductVersions();
	});

	$scope.setDefault = function (branchName)
	{
		$http.put(getUrl("/api/1.0/product-versions/default"), branchName).success(function ()
		{
			refreshProductVersions();
		})
	}

}]);