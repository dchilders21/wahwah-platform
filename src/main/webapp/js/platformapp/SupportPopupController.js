wahwahApp.controller('SupportPopupController', ['$scope', '$http', function ($scope, $http)
{

	$scope.hasSentMail = false;

	$scope.subject = "";
	$scope.message_body = "";

	$scope.sendSupportMail = function ()
	{

		var supportMessage = {
			subject: $scope.subject,
			message_body: $scope.message_body
		};

		$http.post(getUrl("/api/1.0/support/"), supportMessage).success(function ()
		{
			$scope.hasSentMail = true;
		});
	}

	angular.element("#supportModal").on("hidden.bs.modal", function ()
	{

		$scope.subject = "";
		$scope.message_body = "";
		$scope.hasSentMail = false;


		$scope.apply();
	});

}]);