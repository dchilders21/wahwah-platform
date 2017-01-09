/**
 * Created by Brian.Bober on 6/12/2015.
 */

wahwahApp.controller('ProductBulkUpdateController', ['$scope', '$http', 'ngstomp', '$user', function ($scope, $http, ngstomp, $user)
{


	$scope.user = {roles: []};

	$scope.tagsToUpdate = [];

	$scope.hasConfirmedPublish = false;

	$scope.productVersions = [];

// Not sure why the functions are required to change the values
	$scope.changeToggleTo = function(ver)
	{
		$scope.toggleTo = ver;
	}

	$scope.changeToggleFrom = function(ver)
	{
		$scope.toggleFrom = ver;
	}

	$scope.toggle = function()
	{
		for (var idx in $scope.choose.products)
		{
			var product = $scope.choose.products[idx];
			if ($scope.choose.newVersion[product.id].versionName == $scope.toggleFrom.versionName)
			{
				$scope.choose.newVersion[product.id] = $scope.toggleTo;
				$scope.choose.checked[product.id] = true;
			}
		}
	};


	$http.get(getUrl("/api/1.0/product-versions/without-checkout")).success(function (data)
	{
		$scope.productVersions = data.productVersions;

		for (var i = 0; i < $scope.productVersions.length; i++)
		{
			if ($scope.productVersions[i].isDefault)
				$scope.toggleTo = $scope.toggleFrom = $scope.productVersions[i];
		}
	});


	$scope.sites = [];

	function getSiteList()
	{
		$http.get(getUrl("/api/1.0/sites?page=0&size=100000")).success(function (data)
		{
			$scope.sites = data;
		});
	}

	getSiteList();

	$scope.getWidgetId = function(product)
	{
		var widget = (product.toolbar != null)? product.toolbar : product.standalone_ad;
		var widgetId = widget.widget_id;
		if (widgetId <= 0)
			return "Unpublished";
		return widgetId;
	};

	$scope.getSiteNameById = function (id)
	{
		if (typeof($scope.sites.sites) == "undefined")
			return;
		for (var i = 0; i < $scope.sites.sites.length; i++)
		{
			if ($scope.sites.sites[i].id == id)
			{
				return $scope.sites.sites[i].site_name;
			}
		}
		return "";
	};

	$scope.getVersionById = function (id)
	{
		for (var i = 0; i < $scope.productVersions.length; i++)
		{
			if ($scope.productVersions[i].id == id)
				return $scope.productVersions[i];
		}
		return {versionName: "unknown"}; // Shouldn't happen
	};

	$user.getUserPromise().then(function (data)
	{
		$scope.user = data;
		$scope.start();
	});

	$scope.hasRole = function (roleName)
	{
		return $user.hasRole(roleName);
	};

	$scope.choose = function ()
	{

		$scope.state = 'CHOOSE';

		$scope.choose.products = [];

		$scope.choose.checked = [];

		$scope.choose.newVersion = [];


		var pageNumber = 0;
		var pageSize = 1000000; // Todo: 1 million for now to avoid paging. May need to update later to page if # of tags gets too long
		$http.get(getUrl("/api/1.0/products/?page=" + pageNumber + "&size=" + pageSize)).success(function (data)
		{
			$scope.choose.products = data.products;
			for (var i = 0; i < $scope.choose.products.length; i++)
			{
				var product = $scope.choose.products[i];
				$scope.choose.checked[product.id] = false;
				$scope.choose.newVersion[product.id] = product.product_version_id;
			}
		}).error(function (data)
		{
			$scope.fatalErrorMsg = "Unabled to retrieve any tags!";
		});


		$scope.choose.submit = function ()
		{
			$scope.fatalErrorMsg = "";
			for (var i = 0; i < $scope.choose.products.length; i++)
			{
				var product = $scope.choose.products[i];
				if ($scope.choose.checked[product.id])
				{
					var _newVersion = $scope.choose.newVersion[product.id].id;
					var _oldVersion = product.product_version_id.id;
					$scope.tagsToUpdate[$scope.tagsToUpdate.length] = {
						name: product.product_name,
						id: product.id,
						widget_id: $scope.getWidgetId(product),
						status: 'Pending',
						oldVersion: _oldVersion,
						newVersion: _newVersion
					};
				}
			}

			if ($scope.tagsToUpdate.length == 0)
			{
				$scope.fatalErrorMsg = "None chosen.";
			}
			else
			{
				$scope.state = 'CONFIRM';
			}
		}


	};

	$scope.update = function (tags)
	{

		$scope.state = 'PUBLISHING';

		$scope.update.anyFailed = false;

		$scope.update.tagsToUpdate = tags;

		$scope.update.updateVersion = function (idx)
		{

			if (idx >= $scope.update.tagsToUpdate.length)
			{
				$scope.update.publishAll();
				return;
			}

			var tag = $scope.update.currentTag = $scope.update.tagsToUpdate[idx]; // From tagsToUpdate

			tag.status = "Update";

			$http.get(getUrl('/api/1.0/products/' + tag.id), "").success(function (data)
			{
				data.product_version_id = $scope.getVersionById(tag.newVersion);
				// Now submit
				$http.put(getUrl('/api/1.0/products/' + tag.id), data).success(function (data)
				{
					$scope.update.updateVersion(++idx);
				}).error(function ()
				{
					tag.failed = true;
					tag.status = "Error";
					tag.errorMsg = "Failed version update: Could not update version";
					$scope.update.anyFailed = true;
					$scope.update.updateVersion(++idx);
				});
			}).error(function ()
			{
				tag.failed = true;
				tag.status = "Error";
				tag.errorMsg = "Failed version update: Could not retrieve tag info";
				$scope.update.anyFailed = true;
				$scope.update.updateVersion(++idx);
			});
		}

		$scope.update.stompClientConnectionId = createUUID();


		var url = getUrl("/stomp/bulkpublish");
		url = url.replace("http://","ws://");

		$scope.update.stompClient = ngstomp(url);


		$scope.update.stompClient.connect("guest", "guest", function ()
		{
			$scope.update.stompClient.subscribe("/topic/" + $scope.update.stompClientConnectionId, function (message)
			{

				var statusMessage = JSON.parse(message.body);

				for (var i = 0; i < $scope.update.tagsToUpdate.length; i++)
				{

					if ($scope.update.tagsToUpdate[i].id == statusMessage.fileName)
					{

						if (statusMessage.status == "UPLOAD")
						{
							$scope.update.tagsToUpdate[i].status = "Upload";
						}

						if (statusMessage.status == "DONE")
						{
							$scope.update.fileUploadCounter++;
							$scope.update.tagsToUpdate[i].status = "Complete";

						}

						if (statusMessage.status == "ERROR")
						{
							$scope.update.fileUploadCounter++;
							$scope.update.tagsToUpdate[i].failed = true;
							$scope.update.tagsToUpdate[i].status = "Error";
							$scope.update.tagsToUpdate[i].errorMsg = "Upload Failed";
							$scope.update.anyFailed = true;
						}

						if ($scope.update.fileUploadCounter == $scope.update.tagsToUpdate.length)
						{
							$scope.state = "DONE";
						}

					}
				}
			}, function ()
			{
			}, '/');
		});

		$scope.update.publishAll = function()
		{
			var tagsToUpdateIdArray = [];

			for (var i = 0; i < $scope.update.tagsToUpdate.length; i++)
			{
				tagsToUpdateIdArray[i] = $scope.update.tagsToUpdate[i].id;
				var bulkPublishModel = {
					productIds: tagsToUpdateIdArray,
					stompConnectionId: $scope.update.stompClientConnectionId
				}
			}
			$http.post(getUrl('/api/1.0/products/bulkpublish'), bulkPublishModel ).success(function ()
			{


			});

		}

		$scope.update.fileUploadCounter = 1;
		$scope.update.updateVersion(0);
	}


	$scope.uploadAll = function ()
	{
		$scope.update($scope.tagsToUpdate);
	}

	$scope.checkSupported = function ()
	{
		if ($scope.product.type == "TOOLBAR" || $scope.product.type == "STANDALONE_AD" || $scope.product.type == "MINI_BAR" || $scope.product.type == "CUSTOM")
		{
			$scope.state = 'START';
			$scope.isSupportedProductType = true;
		}
		else
		{
			$scope.showUnsupportedMessage = true;
		}

	}


	$scope.start = function ()
	{
		if (!$scope.hasRole('SUPER_USER'))
		{
			$scope.fatalErrorMsg = "Unauthorized. Please contact administrator to get necessary permissions.";
		}
		else
		{
			$scope.choose();
		}
	}


}]);