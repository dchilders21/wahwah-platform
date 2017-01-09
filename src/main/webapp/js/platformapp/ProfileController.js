/**
 * Created by Justin on 5/30/2014.
 */

wahwahApp.controller('ProfileController', ['$scope', '$http', '$timeout', '$user', function ($scope, $http, $timeout, $user)
{

	$scope.password = {};

	$scope.showProfileUpdatedAlert = false;
	$scope.showPasswordUpdatedAlert = false;
	$scope.showPasswordUpdatedFailureAlert = false;

	$user.getUserPromise().then(function (data)
	{
		$scope.user = {};
		$scope.user.firstName = data.firstName;
		$scope.user.lastName = data.lastName;
		$scope.user.emailAddress = data.emailAddress;
	});

	$scope.savePersonalInformation = function ()
	{
		$user.updateUserPromise($scope.user).then(function(data)
		{
			$scope.showProfileUpdatedAlert = true;

			$scope.user = {};
			$scope.user.firstName = data.firstName;
			$scope.user.lastName = data.lastName;
			$scope.user.emailAddress = data.emailAddress;

			$timeout(function ()
			{
				$scope.showProfileUpdatedAlert = false;
			}, 10000);
		});
	};

	$scope.updatePassword = function ()
	{

		$http.post(getUrl("/api/1.0/users/current/update-password"), $scope.password).success(function ()
		{
			$scope.showPasswordUpdatedAlert = true;

			$timeout(function ()
			{
				$scope.showPasswordUpdatedAlert = false;
			}, 10000);

		}).error(function ()
		{
			$scope.showPasswordUpdatedFailureAlert = true;

			$timeout(function ()
			{
				$scope.showPasswordUpdatedFailureAlert = false;
			}, 15000);
		})

	}

}]);