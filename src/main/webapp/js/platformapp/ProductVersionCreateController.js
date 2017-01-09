wahwahApp.controller('ProductVersionCreateController', ['$scope', '$http', '$location', function ($scope, $http, $location)
{

	$scope.gitBranches = [];
	$scope.hasLoadedGitBranches = false;
	$scope.productVersion = {};

	$scope.loadUnclaimedGitBranches = function ()
	{
		$http.get(getUrl("/api/1.0/product-versions/git-branches/unclaimed")).success(function (data)
		{
			$scope.gitBranches = data.gitBranches;
			$scope.hasLoadedGitBranches = true;
		});
	}

	$scope.loadUnclaimedGitBranches();

	$scope.addProductVersion = function ()
	{
		var productVersion = {
			branchName: $scope.productVersion.gitBranchName,
			versionName: $scope.productVersion.versionName
		};

		$http.post(getUrl("/api/1.0/product-versions/"), productVersion).success(function (data)
		{
			$location.path("/product-versions/" + data.id);
		});
	}

}]);