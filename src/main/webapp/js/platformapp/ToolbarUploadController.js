wahwahApp.controller('ToolbarUploadController', ['$scope', '$http', '$location', 'ngstomp', function ($scope, $http, $location, ngstomp)
{

	$scope.credentials = {};

	$http.get(getUrl("/api/1.0/system/environment/")).success(function (data)
	{
		for (destination in $scope.destinations)
		{
			if ($scope.destinations[destination].folder == data.environmentAlias)
			{
				$scope.selectedDestination = $scope.destinations[destination];
			}
		}

	});

	var url = getUrl("/stomp/toolbar-upload");
	url = url.replace("http://","ws://");

	$scope.stompClient = ngstomp(url);

	$scope.stompClientConnectionId = createUUID();

	$scope.stompClient.connect("guest", "guest", function ()
	{
		$scope.stompClient.subscribe("/topic/" + $scope.stompClientConnectionId, function (message)
		{

			var statusMessage = JSON.parse(message.body);

			for (var i = 0; i < $scope.filesToUpload.length; i++)
			{

				if ($scope.filesToUpload[i].name == statusMessage.fileName)
				{

					if (statusMessage.status == "UPLOAD")
					{
						$scope.filesToUpload[i].status = "Upload";
					}

					if (statusMessage.status == "DONE")
					{
						$scope.filesToUpload[i].status = "Complete";

						$scope.fileUploadCounter++;
						$scope.fileUploadPercentage = Math.ceil(($scope.fileUploadCounter / $scope.filesToUpload.length) * 100);
						$scope.fileUploadProgressStyle.width = $scope.fileUploadPercentage + "%";

						if ($scope.fileUploadCounter == $scope.filesToUpload.length)
						{
							$scope.currentStep = "upload-done";
						}
					}

				}
			}
		}, function ()
		{
		}, '/');
	});


	$scope.didGitCredentialsWork = true;
	$scope.didEdgeCastCredentialsWork = true;
	$scope.hasTestedCredentials = false;
	$scope.gitBranches = ["Loading"];
	$scope.hasLoadedGitBranches = false;
	$scope.selectedGitBranch = null;
	$scope.destinations = [{folder: "prod", name: "Production"}, {
		folder: "qa",
		name: "Quality Assurance Testing"
	}, {folder: "dev", name: "Development Testing"}];
	$scope.selectedDestination = null;
	$scope.filesToUpload = [];
	$scope.fileUploadCounter = 0;
	$scope.fileUploadPercentage = 0;
	$scope.fileUploadProgressStyle = {width: "0%"};

	$scope.currentStep = "test-credentials";

	testCurrentCredentials();

	function testCurrentCredentials()
	{
		$http.get(getUrl("/api/1.0/toolbar-upload/test-credentials/current")).success(function (data)
		{

			$scope.hasTestedCredentials = true;

			$scope.didGitCredentialsWork = data.didGitCredentialsWork;
			$scope.didEdgeCastCredentialsWork = data.didEdgeCastCredentialsWork;

			if (data.didEdgeCastCredentialsWork && data.didEdgeCastCredentialsWork)
			{
				$scope.currentStep = "list-git-branches";
				$scope.loadGitBranches();
			}

		}).error(function (data, status)
		{
			if (status == 403)
			{
				$location.path("/overview");
			}
		});
	}


	$scope.testCredentials = function ()
	{

		$scope.hasTestedCredentials = false;

		$http.post(getUrl("/api/1.0/toolbar-upload/test-credentials/"), $scope.credentials).success(function (data)
		{

			$scope.hasTestedCredentials = true;

			$scope.didGitCredentialsWork = data.didGitCredentialsWork;
			$scope.didEdgeCastCredentialsWork = data.didEdgeCastCredentialsWork;
		});
	}

	$scope.saveCredentials = function ()
	{

		$scope.hasTestedCredentials = false;

		$http.post(getUrl("/api/1.0/toolbar-upload/save-credentials/"), $scope.credentials).success(function (data)
		{
			$scope.hasTestedCredentials = true;

			$scope.didGitCredentialsWork = data.didGitCredentialsWork;
			$scope.didEdgeCastCredentialsWork = data.didEdgeCastCredentialsWork;

			if (data.didEdgeCastCredentialsWork && data.didEdgeCastCredentialsWork)
			{
				$scope.currentStep = "list-git-branches";
				$scope.loadGitBranches();
			}
		});
	}

	$scope.loadGitBranches = function ()
	{
		$http.get(getUrl("/api/1.0/toolbar-upload/list-git-branches/")).success(function (data)
		{
			$scope.gitBranches = data.gitBranches;
			$scope.hasLoadedGitBranches = true;
		});
	}

	$scope.forceUploadAll = function ()
	{
		$scope.currentStep = "upload-monitor";

		$scope.loadFileList(true);
	}

	$scope.selectGitBranch = function ()
	{
		$scope.currentStep = "choose-destination";
	}

	$scope.selectDestination = function ()
	{
		$scope.currentStep = "upload-monitor";

		$scope.loadFileList(false);
	}

	$scope.loadFileList = function (all)
	{
		var listFilesUrl = "/api/1.0/toolbar-upload/list-files/";
		if (all)
			listFilesUrl = "/api/1.0/toolbar-upload/list-files-all/"

		$http.get(getUrl(listFilesUrl + $scope.selectedGitBranch)).success(function (data)
		{

			$scope.commitIdToUpload = data.commitId;
			$scope.previousCommitIdForDiff = data.lastCommitId;
			$scope.filesToUpload = [];

			for (var i = 0; i < data.files.length; i++)
			{
				var file = {
					name: data.files[i],
					status: "Pending"
				};

				$scope.filesToUpload.push(file);
			}

			if (data.files.length == 0)
			{
				$scope.currentStep = "upload-done";
			}
			else
			{
				$scope.uploadFiles();
			}
		});
	}

	$scope.uploadFiles = function ()
	{
		var uploadData = {
			gitBranchName: $scope.selectedGitBranch,
			destinationFolder: $scope.selectedDestination.folder,
			stompConnectionId: $scope.stompClientConnectionId,
			commitId: $scope.commitIdToUpload,
			lastCommitId: $scope.previousCommitIdForDiff
		};

		$http.post(getUrl("/api/1.0/toolbar-upload/upload-files/"), uploadData);
	}

}]);
