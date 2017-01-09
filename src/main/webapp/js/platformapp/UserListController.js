wahwahApp.controller('UserListController', ['$scope', '$http', function ($scope, $http)
{

	$scope.user = {roles: []};

	$scope.showConfirmDeleteAlert = false;

	$scope.hasRole = function (roleName)
	{
		return window.hasRole($scope.user.roles, roleName);
	};

	$http.get(getUrl("/api/1.0/users/current/")).success(function (data)
	{
		$scope.user = data;
	});

	$scope.users = [];

	$scope.currentPageSize = 25;

	$scope.userTypes = {
        "RED_PANDA_USER":"Red Panda Employee",
        "MARKETPLACE_PUBLISHER_USER":"Red Panda Marketplace",
        "FREE_USER":"Free",
        "NETWORK_USER":"Network",
        "NETWORK_PUBLISHER_USER":"Network Publisher"
    };

	function refreshUsers()
	{
		loadUsers(0, $scope.currentPageSize);
	}

	function loadUsers(pageNumber, pageSize)
	{
		$http.get(getUrl("/api/1.0/users/?page=" + pageNumber + "&size=" + pageSize)).success(function (data)
		{

			$scope.users = data.users;

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

			for (var i = 0; i < $scope.users.length; i++)
			{
				var publisherName = $scope.users[i].publisherName;
				if (publisherName == null)
				{
					$scope.users[i].publisherName = "N/A";
				}

				var roleListArray = [];

				if (roleListArray.length == 0 && $scope.users[i].roles.indexOf("DEVELOPER") >= 0)
				{
					roleListArray.push("Software Engineer");
				}

				if (roleListArray.length == 0 && $scope.users[i].roles.indexOf("SUPER_USER") >= 0)
				{
					roleListArray.push("Super User");
				}

				if (roleListArray.length == 0 && $scope.users[i].roles.indexOf("INTERNAL_USER") >= 0)
				{
					roleListArray.push("Internal User");
				}

				if (roleListArray.length == 0 && $scope.users[i].roles.indexOf("PUBLISHER_ADMIN") >= 0)
				{
					roleListArray.push("Publisher Administrator");
				}

				if (roleListArray.length == 0 && $scope.users[i].roles.indexOf("PUBLISHER_USER") >= 0)
				{
					roleListArray.push("Publisher User");
				}

				if (roleListArray.length == 0 && $scope.users[i].roles.indexOf("USER") >= 0)
				{
					roleListArray.push("User");
				}

				if ($scope.users[i].roles.indexOf("DEVELOPER") == -1 && $scope.users[i].roles.indexOf("TOOLBAR_PUBLISHER") >= 0)
				{
					roleListArray.push("Toolbar Publisher");
				}

				$scope.users[i].roleList = roleListArray.join(",");

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

		loadUsers(pageIndex, $scope.currentPageSize);
	}

	refreshUsers();

	$scope.checkInternal = function (user)
	{
		for (var i = 0; i < user.roleList.length; i++)
		{
			var role = user.roles[i];
			if (role == "INTERNAL_USER")
				return true;
		}
		return false;
	}

	$scope.showDelete = function (user)
	{
		if ($scope.hasRole('PUBLISHER_ADMIN') && !$scope.checkInternal(user))
		{
			if ($scope.user.emailAddress != user.emailAddress) // Can't delete self
				return true;
		}
		else if ($scope.hasRole('SUPER_USER') && $scope.checkInternal(user))
		{
			if ($scope.user.emailAddress != user.emailAddress) // Can't delete self
				return true;
		}
		else
			return false;
	}

	$scope.deleteUser = function (user)
	{
		$scope.showConfirmDeleteAlert = true;
		$scope.userToDelete = user;
	}

	$scope.hideDeleteWarning = function ()
	{
		$scope.showConfirmDeleteAlert = false;
	}

	$scope.performDelete = function ()
	{
		$http.delete(getUrl("/api/1.0/users/" + $scope.userToDelete.userId)).success(function ()
		{
			$scope.showConfirmDeleteAlert = false;
			refreshUsers();
		});
	}

}]);
