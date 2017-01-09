wahwahApp.controller('APIKeysListController', ['$scope', '$http', function ($scope, $http)
{
	$scope.keys = [];

	refreshKeys();

	function refreshKeys()
	{
		$http.get(getUrl("/api/1.0/api-keys/")).success(function (data)
		{
			$scope.keys = data.keys;
		});
	}

	$scope.deleteKey = function (key)
	{
		$scope.showConfirmDeleteAlert = true;
		$scope.keyToDelete = key;
	}

	$scope.performDelete = function ()
	{
		$http.delete(getUrl("/api/1.0/api-keys/widget-" + $scope.keyToDelete.widgetId)).success(function ()
		{
			$scope.showConfirmDeleteAlert = false;
			refreshKeys();
		});
	}
}]);

wahwahApp.controller('APIKeysCreateController', ['$scope', '$http', '$location', '$flashMessage', function ($scope, $http, $location, $flashMessage)
{
	$scope.key = {};

	$scope.addKey = function ()
	{

		$http.post(getUrl('/api/1.0/api-keys/'), $scope.key).success(function (data)
		{
			$flashMessage.setMessage("APIKEY_CREATE");
			$location.path("/api-keys/widget-" + data.widgetId);
		})
	}

	$scope.cancel = function ()
	{
		$location.path("/api-keys");
	}
}]);

wahwahApp.controller('APIKeysEditController', ['$scope', '$http', '$location', '$routeParams', '$timeout', '$flashMessage', function ($scope, $http, $location, $routeParams, $timeout, $flashMessage)
{
	$scope.key = {};
	$scope.widgetId = $routeParams.widgetId;
	$scope.widgetName = "";

	var message = $flashMessage.getMessage();

	if (message == "APIKEY_CREATE")
	{
		$scope.showKeyUpdatedAlert = true;

		$timeout(function ()
		{
			$scope.showKeyUpdatedAlert = false;
		}, 10000);
	}

	refreshKey();

	function refreshKey()
	{
		$http.get(getUrl("/api/1.0/api-keys/widget-" + $scope.widgetId)).success(function (data)
		{
			$scope.key = data;
			$scope.widgetName = data.widgetName;
		});
	}

	$scope.updateKey = function ()
	{

		$http.put(getUrl('/api/1.0/api-keys/widget-' + $scope.widgetId + "/"), $scope.key).success(function (data)
		{
			refreshKey();

			$scope.showKeyUpdatedAlert = true;

			$timeout(function ()
			{
				$scope.showKeyUpdatedAlert = false;
			}, 10000);
		})
	}

	$scope.cancel = function ()
	{
		$location.path("/api-keys");
	}
}]);