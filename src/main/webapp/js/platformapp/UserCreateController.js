wahwahApp.controller('UserCreateController', ['$scope', '$http', '$location', '$flashMessage', '$user', function ($scope, $http, $location, $flashMessage, $user)
{

	$scope.editUser = {};

	$scope.newUser = {};

	$scope.roles = [];

	$scope.roles["SOFTWARE_ENGINEER"] = ["DEVELOPER", "SUPER_USER","INTERNAL_USER", "ADVERTISING_ADMIN", "PUBLISHER_ADMIN", "PUBLISHER_USER", "PLAYER_ADMIN", "ANALYTICS", "TOOLBAR_PUBLISHER", "USER", "NETWORK_ADMIN"];
	$scope.roles["PUBLISHER_ANALYTICS"] = ["USER", "ANALYTICS"];
	$scope.roles["INTERNAL_USER"] = ["INTERNAL_USER", "ADVERTISING_ADMIN", "PUBLISHER_ADMIN", "PUBLISHER_USER", "PLAYER_ADMIN", "ANALYTICS", "TOOLBAR_PUBLISHER", "USER", "NETWORK_ADMIN"];
	$scope.roles["NETWORK_ANALYTICS"] = ["USER", "ANALYTICS"];
	$scope.roles["ANALYTICS"] = ["USER", "ANALYTICS"];

	$scope.userTypes = ["ANALYTICS",/*"PUBLISHER_ANALYTICS", "NETWORK_ANALYTICS",*/ "INTERNAL_USER", "SOFTWARE_ENGINEER"];

	$scope.editUser.selectedUserType =  "ANALYTICS";

	$scope.user = {roles: []};

	$scope.publisherQuery = getUrl("/api/1.0/accounts/query/%QUERY");

	$scope.hasRole = function (roleName)
	{
		return window.hasRole($scope.user.roles, roleName);
	};

	$scope.publisherNameInvalid = false;

	$scope.showProfileUpdatedAlert = false;
	$scope.showRolesUpdatedAlert = false;
	$scope.showPublisherNameInvalidAlert = false;

	$user.getUserPromise().then(function(user){
        $scope.user = user;
    });

	$scope.displayUser = function (userType)
	{
		userType = userType.toLowerCase();
		var tmpArray = userType.split("_");
		userType = "";
		for (var i = 0; i < tmpArray.length; i++)
		{
			var sub = tmpArray[i];
			userType += sub.charAt(0).toUpperCase() + sub.slice(1);
			if (i < tmpArray.length - 1 )
				userType += " ";
		}
		return userType;
	};

	$scope.onPublisherFilter = function (data)
	{
		var publishers = [];

		var accounts = data.publishers;

		for (var i = 0; i < accounts.length; i++)
		{
			var account = accounts[i];
			if (account.account_type == "NETWORK" || (account.account_type == "PUBLISHER" && account.redpanda_publisher_creator_id == null && account.parent_account_id == null))
			{
				publishers.push({value: account.name});
			}
		}

		return publishers;
	};

	$scope.roleSelections = {	};

	$scope.shouldShowUserType = function(type)
	{
		switch(type)
		{
			case "PUBLISHER_ANALYTICS":
			case "NETWORK_ANALYTICS":
			case "INTERNAL_USER":
				if (!$scope.hasRole("DEVELOPER") && !$scope.hasRole("SUPER_USER") && !$scope.hasRole("INTERNAL_USER"))
					return false;
				break;
			case "SOFTWARE_ENGINEER":
				if (!$scope.hasRole("DEVELOPER"))
					return false;
				break;
		}
		return true;
	}

	$scope.shouldShowPublisherField = function()
	{
        if($scope.user.accountType != "ROOT"){
            return false;
        }

		switch($scope.editUser.selectedUserType)
		{
			case "SOFTWARE_ENGINEER":
			case "INTERNAL_USER":
				return false;
				break;
		}
		return true;
	};

	$scope.onClickUserType = function()
	{

	};

	$scope.applyRolesFromUserType = function ()
	{
		$scope.roleSelection = $scope.roles[$scope.editUser.selectedUserType];
		$scope.editUser.roles = $scope.roleSelection;
	}

	$scope.showError = false;

	$scope.createUser = function ()
	{
		$scope.applyRolesFromUserType();

		$scope.showError = false;
		$scope.errMsg = "";

		$http.post(getUrl('/api/1.0/users/'), $scope.editUser).success(function (data)
		{
			$flashMessage.setMessage("USER_CREATED");
			$location.path("/users/" + data.userId);
		}).error(function(e)
		{
			$scope.showError = true;
			if (e.message.toLowerCase().indexOf("duplicate email") >=0)
			{
				$scope.errMsg = "Duplicate email! Email " + $scope.editUser.emailAddress + " already exists.";
				return;
			}
			$scope.errMsg = "Unknown error";
		});
	}

}]);
