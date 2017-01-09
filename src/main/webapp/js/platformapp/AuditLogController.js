wahwahApp.controller('AuditLogController', ['$scope', '$http', function ($scope, $http)
{

	$scope.auditLogEntries = [];

	$scope.currentPageSize = 25;

	refreshAuditLog();

	function refreshAuditLog()
	{
		loadAuditLogEntries(0, $scope.currentPageSize);
	}

	function loadAuditLogEntries(pageNumber, pageSize)
	{
		$http.get(getUrl("/api/1.0/audit-log/recent?page=" + pageNumber + "&size=" + pageSize)).success(function (data)
		{
			$scope.auditLogEntries = data.log_entries;

			for (var i = 0; i < $scope.auditLogEntries.length; i++)
			{
				var entryTime = moment($scope.auditLogEntries[i].entryTime);
				var entryTimeFormatted = entryTime.format("lll");

				$scope.auditLogEntries[i].entryTime = entryTimeFormatted;
			}

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

		loadAuditLogEntries(pageIndex, $scope.currentPageSize);
	}
}]);