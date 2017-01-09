wahwahApp.controller('OverviewController', ['$scope', '$http', '$location', '$user', function ($scope, $http, $location, $user)
{

	$scope.isNaN = isNaN;

	var previousStartDate = null;
	var previousEndDate = null;

	$scope.hasRole = function(roleName){
		return $user.hasRole(roleName);
	};

	$scope.isDataLoading = true;
	$scope.noDataForPublisher = false;
	$scope.loadingFailed = false;
	$scope.noDemandSourcesForPublisher = false;

	$scope.publisherReportPredicate = "site_name";
	$scope.publisherReportReverse = false;

	$scope.advertiserReportPredicate = "advertiser_name";
	$scope.advertiserReportReverse = false;

	$scope.impressionTypeFilters = [];
	$scope.reportTypes = [];

	$scope.publisherId = null;
	$scope.publishers = [];

	$scope.showPublisherMenu = false;
	$scope.showAdvertiserMenu = false;
    $scope.showReportTypeMenu = false;

	$scope.summaryMode = "standard";
    $scope.showRequestFields = true;
    $scope.graphs = [];

    $scope.hasGraph = function(graphName){
        return $scope.graphs.indexOf(graphName) >= 0;
    };

    var onInitialLoad = true;

	function reloadDataFromSearch()
	{
		var searchParams = $location.search();

		if (searchParams.show)
		{
			for (var i = 0; i < $scope.impressionTypeFilters.length; i++)
			{
				if ($scope.impressionTypeFilters[i].value == searchParams.show)
				{
					$scope.currentImpressionTypeFilter = $scope.impressionTypeFilters[i];
				}
			}
		}

        if(searchParams.report_type)
        {
            for(var i = 0; i < $scope.reportTypes.length; i++)
            {
                if($scope.reportTypes[i].value == searchParams.report_type)
                {
                    $scope.currentReportType = $scope.reportTypes[i];
                }
            }
        }

		if (searchParams.start_date)
		{
			$scope.dates.startDate = moment(searchParams.start_date).format('LL');
		}

		if (searchParams.end_date)
		{
			$scope.dates.endDate = moment(searchParams.end_date).format('LL');
		}

		if (searchParams.publisher_id)
		{
			$scope.setPublisher(searchParams.publisher_id,false);
		}

		if (searchParams.site_id)
		{
			$scope.setSite(searchParams.site_id,false);
		}

		if (searchParams.advertiser_id)
		{
			$scope.setAdvertiser(searchParams.advertiser_id,false);
		}

        if(onInitialLoad){
            onInitialLoad = false;
            fetchDashboardData();
        }
	}

	$scope.$on('$locationChangeSuccess', function ()
	{
		reloadDataFromSearch();
	});

	function setTableExportUrl()
	{

		$scope.tableExportUrl = "";

		var startDate = moment(new Date($scope.dates.startDate)).format("YYYY-MM-DD");
		var endDate = moment(new Date($scope.dates.endDate)).format("YYYY-MM-DD");

		if ($scope.user.accountType == "ROOT" || $scope.user.accountType == "NETWORK" || $scope.user.accountType == "REPORTING_PRO")
		{
			if ($scope.currentReportTab != null && $scope.currentReportTab.title == "Publishers")
			{
				$scope.tableExportUrl = "/analytics/api/1.0/dashboard/network/export/publisher.csv?start_date=" + startDate + "&end_date=" + endDate + "&show=" + $scope.currentImpressionTypeFilter.value;
			}

			if ($scope.currentReportTab != null && $scope.currentReportTab.title == "Advertisers" && $scope.currentReportType.value == "network")
			{
				$scope.tableExportUrl = "/analytics/api/1.0/dashboard/network/export/advertiser.csv?start_date=" + startDate + "&end_date=" + endDate + "&show=" + $scope.currentImpressionTypeFilter.value;
			}

			if ($scope.publisherId != null)
			{
				$scope.tableExportUrl += ("&publisher_id=" + $scope.publisherId);
			}

			if ($scope.siteId != null){
				$scope.tableExportUrl += ("&site_id=" + $scope.siteId);
			}
		}

		if($scope.user.accountType == "PUBLISHER")
		{
			if ($scope.currentReportTab != null && $scope.currentReportTab.title == "Sites")
			{
				$scope.tableExportUrl = "/analytics/api/1.0/dashboard/publisher/export/publisher.csv?start_date=" + startDate + "&end_date=" + endDate + "&show=" + $scope.currentImpressionTypeFilter.value;
			}
		}
	}

    function setEnabledMenus(){

        $scope.showPublisherMenu = false;
        $scope.showAdvertiserMenu = false;
        $scope.showReportTypeMenu = false;

        $scope.summaryMode = "standard";
        $scope.showRequestFields = true;

        if($scope.user.accountType == "NETWORK" || $scope.user.accountType == "ROOT" || $scope.user.accountType == "REPORTING_PRO")
        {
            if($scope.currentReportType.value == "network"){
                $scope.showPublisherMenu = true;
                $scope.showAdvertiserMenu = true;
                $scope.showReportTypeMenu = true;
            }

            if($scope.currentReportType.value == "demand_source"){
                $scope.showAdvertiserMenu = true;
                $scope.showReportTypeMenu = true;
            }
        }

        if($scope.user.accountType == "ROOT"){
            $scope.graphs = ['RequestVsImpressions','RevenueVsNetRevenueVsECPM'];
        }

        if($scope.user.accountType == "NETWORK"){
            if($scope.user.singlePublisherNetworkUser || $scope.currentReportType.value == "demand_source"){
                $scope.graphs = ['RequestVsImpressions','RevenueVsECPM'];
            } else {
                $scope.graphs = ['RequestVsImpressions','RevenueVsNetRevenueVsECPM'];
            }
        }

        if($scope.user.accountType == "REPORTING_PRO"){

            if($scope.currentReportType.value == "network"){
                $scope.graphs = ['Impressions','RevenueVsNetRevenueVsECPM'];
                $scope.showRequestFields = false;
                $scope.summaryMode = "reporting_pro";
            }

            if($scope.currentReportType.value == "demand_source"){
                $scope.graphs = ['RequestVsImpressions','RevenueVsECPM'];
            }

        }

        if($scope.user.accountType == "PUBLISHER")
        {
            $scope.graphs = ['RequestVsImpressions','RevenueVsECPM'];

            if($scope.currentReportType == "publisher"){
                $scope.showPublisherMenu = true;
            }
        }

        if($scope.user.accountType == "FREE")
        {
            $scope.graphs = ['RequestVsImpressions','RevenueVsECPM'];

            if($scope.currentReportType.value == "demand_source"){
                $scope.showPublisherMenu = true;
                $scope.showAdvertiserMenu = true;
            }
        }
    }

	$scope.setPublisher = function(publisherId,doReloadData,doUnsetSite){
		if(typeof doReloadData == "undefined") doReloadData = true;
		if(typeof doUnsetSite == "undefined") doUnsetSite = true;

		if($scope.publisherId != publisherId || (doUnsetSite && $scope.siteId != null)) {
			$scope.publisherId = publisherId;
			$location.search("publisher_id",publisherId);

			if(doUnsetSite){
				$scope.setSite(null,false,false);
			}

			if(doReloadData){
				fetchDashboardData();
			}
		}

	};

	$scope.setSite = function(siteId,doReloadData,doUnsetPublisher){
		if(typeof doReloadData == "undefined") doReloadData = true;
		if(typeof doUnsetPublisher == "undefined") doUnsetPublisher = true;

		if($scope.siteId != siteId){
			$scope.siteId = siteId;
			$location.search("site_id",siteId);

			if(doUnsetPublisher){
				$scope.setPublisher(null,false,false);
			}

			if(doReloadData){
				fetchDashboardData();
			}
		}
	};

    $scope.setAdvertiser = function(advertiserId,doReloadData){

        if(typeof doReloadData == "undefined") doReloadData = true;

        if($scope.advertiserId != advertiserId){
            $scope.advertiserId = advertiserId;
            $location.search("advertiser_id",advertiserId);

            if(doReloadData) {
                fetchDashboardData();
            }
        }
    };

	$scope.selectDrillDownItem = function (item)
	{
		if ($scope.user.accountType == "ROOT" || $scope.user.accountType == "NETWORK" || $scope.user.accountType == "REPORTING_PRO")
		{
			$scope.setPublisher(item.id);
		}
	};

    $scope.selectAdvertiserDrillDownItem = function (item)
    {
        if ($scope.user.accountType == "ROOT" || $scope.user.accountType == "NETWORK" || $scope.user.accountType == "REPORTING_PRO")
        {
            $scope.setAdvertiser(item.id);
        }
    };

	$scope.selectImpressionTypeFilter = function (impressionTypeFilter)
	{
		$scope.currentImpressionTypeFilter = impressionTypeFilter;
		$location.search("show", impressionTypeFilter.value);
		fetchDashboardData();
	};

    $scope.selectReportType = function(reportType)
    {
        $scope.currentReportType = reportType;
        $location.search("report_type", reportType.value);
        setEnabledMenus();
        fetchDashboardData();
    };

	$user.getUserPromise().then(function (data)
	{
		$scope.user = data;

		if ($scope.hasRole('ANALYTICS'))
		{
            if($scope.user.accountType == "ROOT" || $scope.user.accountType == "NETWORK"){
                $scope.impressionTypeFilters = [
                    {name: 'US Desktop', value: 'us_desktop'},
                    {name: 'Mobile & Non-US', value: 'mobile_and_non_us'},
                    {name: 'All', value: 'all'}
                ];
            }

            if($scope.user.accountType == "FREE" || $scope.user.accountType == "REPORTING_PRO"){
				$scope.impressionTypeFilters = [
					{name: 'All', value: 'all'}
				];
			}

            if($scope.user.accountType == "NETWORK" || $scope.user.accountType == "ROOT" || $scope.user.accountType == "REPORTING_PRO"){
                $scope.reportTypes = [
                    {name: 'Network Performance', value: 'network'},
                    {name: 'Demand Source Performance', value: 'demand_source'}
                ];
            }

            if($scope.user.accountType == "FREE"){
                $scope.reportTypes = [
                    {name: 'Demand Source Performance', value: 'demand_source'}
                ];
            }

            if($scope.user.accountType == "PUBLISHER"){
                $scope.reportTypes = [
                    {name: 'Publisher Performance', value: 'publisher'}
                ]
            }

            var defaultReportType = angular.element("body").data("defaultdashboard");

            for(var i = 0; i < $scope.reportTypes.length; i++){
                if($scope.reportTypes[i].value == defaultReportType){
                    $scope.currentReportType = $scope.reportTypes[i];
                }
            }

            if ($scope.user.accountType == "PUBLISHER" || $scope.user.accountType == "NETWORK"){
				$scope.currentImpressionTypeFilter = {name: 'US Desktop', value: 'us_desktop'};
			}


			if ($scope.user.accountType == "FREE" || $scope.user.accountType == "REPORTING_PRO" || $scope.user.accountType == "ROOT"){
				$scope.currentImpressionTypeFilter = {name: 'All', value: 'all'};
			}

			$scope.dates = {};
			$scope.dates.endDate = moment().subtract(1, 'days').format('LL');
			$scope.dates.startDate = moment().startOf('month').format('LL');
			$scope.dates.todayDate = moment().format('LL');

			if (moment().date() == 1)
			{
				$scope.dates.startDate = moment().subtract(1, 'months').startOf('month').format('LL');
			}

			$scope.currentDrillDownName = $scope.user.accountName;
			$scope.currentAdvertiserDrillDownName = "";

            if ($scope.user.accountType == "PUBLISHER")
            {
                $scope.currentAdvertiserDrillDownName = "Red Panda Platform";
            }

			if ($scope.user.accountType == "ROOT" || $scope.user.accountType == "NETWORK" || $scope.user.accountType == "REPORTING_PRO")
			{
				$scope.publisherReportPredicate = "publisher_name";
				$scope.dates.endDate = $scope.dates.todayDate;
				$scope.currentDrillDownName = "All Publishers";
				$scope.currentAdvertiserDrillDownName = "All Advertisers";
			}

			if ($scope.user.accountType == "FREE")
			{
				$scope.currentAdvertiserDrillDownName = "All Advertisers";
			}

			setTableExportUrl();
            setEnabledMenus();

			previousStartDate = $scope.dates.startDate;
			previousEndDate = $scope.dates.endDate;

			$scope.$watch('dates.startDate', function (newValue)
			{
				if (previousStartDate != newValue)
				{

					var startDate = moment(new Date($scope.dates.startDate)).format("YYYY-MM-DD");
					$location.search("start_date", startDate);

					fetchDashboardData();
					previousStartDate = newValue;
				}
			});

			$scope.$watch('dates.endDate', function (newValue)
			{
				if (previousEndDate != newValue)
				{

					var endDate = moment(new Date($scope.dates.endDate)).format("YYYY-MM-DD");
					$location.search("end_date", endDate);

					fetchDashboardData();
					previousEndDate = newValue;
				}

			});

			reloadDataFromSearch();
		}
	});

	function fetchDashboardData()
	{
        setEnabledMenus();

        $scope.dashboardSummaryData = null;
        $scope.dashboardPubReport = null;
        $scope.dashboardAdvertiserReport = null;
        $scope.dashboardImpressionRequests = null;
        $scope.dashboardRevenue = null;
        $scope.dashboardWahwahRevenue = null;
        $scope.reportTabs = [];

		if ($scope.user.accountType == "ROOT" || $scope.user.accountType == "NETWORK" || $scope.user.accountType == "REPORTING_PRO")
		{
            if($scope.currentReportType.value == "network"){
                fetchWahwahDashboardData();
            }

            if($scope.currentReportType.value == "demand_source"){
                fetchNetworkDemandData();
            }

		}

        if($scope.user.accountType == "PUBLISHER")
		{
			fetchPublisherDashboardData();
		}

        if($scope.user.accountType == "FREE"){
            fetchFreeDashboardData();
        }

		setTableExportUrl();
	}

	function fetchPublisherDashboardData()
	{
		$scope.isDataLoading = true;
		$scope.loadingFailed = false;
		$scope.noDataForPublisher = false;

		var startDate = moment(new Date($scope.dates.startDate)).format("YYYY-MM-DD");
		var endDate = moment(new Date($scope.dates.endDate)).format("YYYY-MM-DD");

		var url = "/analytics/api/1.0/dashboard/publisher/?start_date=" + startDate + "&end_date=" + endDate + "&show=" + $scope.currentImpressionTypeFilter.value;

		if($scope.siteId != null){
			url = "/analytics/api/1.0/dashboard/publisher/site-" + $scope.siteId + "?start_date=" + startDate + "&end_date=" + endDate + "&show=" + $scope.currentImpressionTypeFilter.value;
		}

		$http.get(url).success(function (data)
		{
			$scope.dashboardData = data;

			$scope.dashboardSummaryData = data.summary;

			if ($scope.dashboardSummaryData.requests == 0)
			{
				$scope.noDataForPublisher = true;
			}

			for (var i = 0; i < data.impression_graph_data.length; i++)
			{
				data.impression_graph_data[i].date = moment(data.impression_graph_data[i].date).format("LL");
			}

			for (var i = 0; i < data.revenue_graph_data.length; i++)
			{
				data.revenue_graph_data[i].date = moment(data.revenue_graph_data[i].date).format("LL");
				data.revenue_graph_data[i].revenue = data.revenue_graph_data[i].revenue / 100;
				data.revenue_graph_data[i].ecpm = data.revenue_graph_data[i].ecpm / 100;
			}

			data.publisher_report_data.totals = {impressions: 0, requests: 0, publisher_revenue: 0};

			for (var i = 0; i < data.publisher_report_data.length; i++)
			{

				if (data.publisher_report_data[i]["entity.type"] == "site")
				{
					data.publisher_report_data[i].site_id = data.publisher_report_data[i]["entity.id"];
					data.publisher_report_data[i].site_name = data.publisher_report_data[i]["entity.name"];
				}

				data.publisher_report_data[i].publisher_revenue = data.publisher_report_data[i].publisher_revenue_cents / 100;

				data.publisher_report_data.totals.impressions += data.publisher_report_data[i].impressions;
				data.publisher_report_data.totals.requests += data.publisher_report_data[i].requests;
				data.publisher_report_data.totals.publisher_revenue += data.publisher_report_data[i].publisher_revenue;
			}

			data.publisher_report_data.totals.fill_rate = 0;

			if (data.publisher_report_data.totals.requests > 0)
			{
				data.publisher_report_data.totals.fill_rate = Math.round((data.publisher_report_data.totals.impressions * 100) / data.publisher_report_data.totals.requests);
			}

			$scope.dashboardImpressionRequests = data.impression_graph_data;
			$scope.dashboardRevenue = data.revenue_graph_data;
			$scope.dashboardPubReport = data.publisher_report_data;

			$scope.reportTabs = [{title: 'Sites', disabled: false}];
			$scope.previousTab = $scope.reportTabs[0];
			$scope.setReportTab($scope.reportTabs[0]);

			$scope.isDataLoading = false;

		}).error(function (data, status, headers, config)
		{
			$scope.isDataLoading = false;
			$scope.loadingFailed = true;
		});
	}

	function fetchWahwahDashboardData()
	{
		$scope.isDataLoading = true;
		$scope.loadingFailed = false;

		var startDate = moment(new Date($scope.dates.startDate)).format("YYYY-MM-DD");
		var endDate = moment(new Date($scope.dates.endDate)).format("YYYY-MM-DD");

		var params = {
			start_date: startDate,
			end_date: endDate,
			show: $scope.currentImpressionTypeFilter.value
		};

		var url = "/analytics/api/1.0/dashboard/network/";

		if ($scope.publisherId != null)
		{
			params.publisher_id = $scope.publisherId;
		}

		if($scope.siteId != null){
			params.site_id = $scope.siteId;
		}

        if($scope.advertiserId != null){
            params.demand_source_id = $scope.advertiserId;
        }

		$http.get(url,{params: params}).success(function (data)
		{
			$scope.dashboardData = data;
			$scope.dashboardSummaryData = data.summary;
			$scope.dashboardUnattributedRevenueData = data.unattributed_revenue;

			$scope.currentDrillDownName = data.selected_publisher_name;
            $scope.currentAdvertiserDrillDownName = data.selected_advertiser_name;

			if ($scope.dashboardUnattributedRevenueData != null)
			{
				$scope.dashboardUnattributedRevenueData.revenue = data.unattributed_revenue.revenue_cents / 100;
			}

			$scope.publishers = [];
            $scope.advertisers = [];

			for (var i = 0; i < data.impression_graph_data.length; i++)
			{
				data.impression_graph_data[i].date = moment(data.impression_graph_data[i].date).format("LL");
			}

			for (var i = 0; i < data.revenue_graph_data.length; i++)
			{
				data.revenue_graph_data[i].date = moment(data.revenue_graph_data[i].date).format("LL");
				data.revenue_graph_data[i].revenue = data.revenue_graph_data[i].revenue / 100;
				data.revenue_graph_data[i].net_revenue = data.revenue_graph_data[i].net_revenue / 100;
				data.revenue_graph_data[i].ecpm = data.revenue_graph_data[i].ecpm / 100;
			}

			data.publisher_report_data.totals = {
				requests: 0,
				impressions: 0,
				revenue: 0,
				publisher_revenue: 0,
				net_revenue: 0,
			};

			for (var i = 0; i < data.publisher_report_data.length; i++)
			{

				if (data.publisher_report_data[i]["entity.type"] == "publisher")
				{
					data.publisher_report_data[i].publisher_id = data.publisher_report_data[i]["entity.id"];
					data.publisher_report_data[i].publisher_name = data.publisher_report_data[i]["entity.name"];
				}

				if (data.publisher_report_data[i]["entity.type"] == "site")
				{
					data.publisher_report_data[i].site_id = data.publisher_report_data[i]["entity.id"];
					data.publisher_report_data[i].site_name = data.publisher_report_data[i]["entity.name"];
				}

				data.publisher_report_data[i].revenue = data.publisher_report_data[i].revenue_cents / 100;
				data.publisher_report_data[i].net_revenue = data.publisher_report_data[i].net_revenue_cents / 100;
				data.publisher_report_data[i].publisher_revenue = data.publisher_report_data[i].publisher_revenue_cents / 100;
				data.publisher_report_data[i].ecpm = data.publisher_report_data[i].ecpm_cents / 100;
				data.publisher_report_data[i].rcpm = data.publisher_report_data[i].rcpm_cents / 100;

				data.publisher_report_data.totals.requests += data.publisher_report_data[i].requests;
				data.publisher_report_data.totals.impressions += data.publisher_report_data[i].impressions;
				data.publisher_report_data.totals.revenue += data.publisher_report_data[i].revenue;
				data.publisher_report_data.totals.publisher_revenue += data.publisher_report_data[i].publisher_revenue;
				data.publisher_report_data.totals.net_revenue += data.publisher_report_data[i].net_revenue;
			}

			if ($scope.dashboardUnattributedRevenueData != null)
			{
				data.publisher_report_data.totals.impressions += $scope.dashboardUnattributedRevenueData.impressions;
				data.publisher_report_data.totals.revenue += $scope.dashboardUnattributedRevenueData.revenue;
			}

			data.publisher_report_data.totals.fill_rate = 0;

			if (data.publisher_report_data.totals.requests > 0)
			{
				data.publisher_report_data.totals.fill_rate = Math.round((data.publisher_report_data.totals.impressions * 100) / data.publisher_report_data.totals.requests);
			}

            data.publisher_report_data.totals.ecpm = Math.round (((data.publisher_report_data.totals.revenue / data.publisher_report_data.totals.impressions) * 1000) * 100) / 100;
            data.publisher_report_data.totals.rcpm = Math.round (((data.publisher_report_data.totals.revenue / data.publisher_report_data.totals.requests) * 1000) * 100) / 100;

			data.advertiser_report_data.totals = {
				requests: 0,
				impressions: 0,
				net_impressions: 0,
				revenue: 0,
				net_revenue: 0,
                ecpm_sum: 0,
                ecpm: 0,
                rcpm_sum: 0,
                rcpm: 0
			};

			for (var i = 0; i < data.advertiser_report_data.length; i++)
			{

				if (data.advertiser_report_data[i]["entity.type"] == "advertiser")
				{
					data.advertiser_report_data[i].advertiser_id = data.advertiser_report_data[i]["entity.id"];
					data.advertiser_report_data[i].advertiser_name = data.advertiser_report_data[i]["entity.name"];
				}

                if (data.advertiser_report_data[i]["entity.type"] == "lineitem")
                {
                    data.advertiser_report_data[i].lineitem_name = data.advertiser_report_data[i]["entity.name"];
                }

				data.advertiser_report_data[i].revenue = data.advertiser_report_data[i].revenue_cents / 100;
				data.advertiser_report_data[i].net_revenue = data.advertiser_report_data[i].net_revenue_cents / 100;
                data.advertiser_report_data[i].ecpm = data.advertiser_report_data[i].ecpm_cents / 100;
                data.advertiser_report_data[i].rcpm = data.advertiser_report_data[i].rcpm_cents / 100;

				data.advertiser_report_data.totals.requests += data.advertiser_report_data[i].requests;
				data.advertiser_report_data.totals.impressions += data.advertiser_report_data[i].impressions;
				data.advertiser_report_data.totals.net_impressions += data.advertiser_report_data[i].net_impressions;
				data.advertiser_report_data.totals.revenue += data.advertiser_report_data[i].revenue;
				data.advertiser_report_data.totals.net_revenue += data.advertiser_report_data[i].net_revenue;
			}

			data.advertiser_report_data.totals.fill_rate = 0;

			if (data.advertiser_report_data.totals.requests > 0)
			{
				data.advertiser_report_data.totals.fill_rate = Math.round((data.advertiser_report_data.totals.net_impressions * 100) / data.advertiser_report_data.totals.requests);
			}

            data.advertiser_report_data.totals.ecpm = Math.round (((data.advertiser_report_data.totals.revenue / data.advertiser_report_data.totals.net_impressions) * 1000) * 100) / 100;
            data.advertiser_report_data.totals.rcpm = Math.round (((data.advertiser_report_data.totals.revenue / data.advertiser_report_data.totals.requests) * 1000) * 100) / 100;
            
			$scope.dashboardImpressionRequests = data.impression_graph_data;

			if($scope.hasGraph('RevenueVsECPM')){
				$scope.dashboardRevenue = data.revenue_graph_data;
			} else {
				$scope.dashboardWahwahRevenue = data.revenue_graph_data;
			}

			$scope.dashboardPubReport = data.publisher_report_data;
			$scope.dashboardAdvertiserReport = data.advertiser_report_data;

			var publisherList = data.publishers;

			publisherList.sort(function (a, b) {
				if (a.name > b.name) {
					return 1;
				}
				if (a.name < b.name) {
					return -1;
				}
				// a must be equal to b
				return 0;
			});

			publisherList.forEach(function(item){
				$scope.publishers.push(item);
			});

            var advertiserList = data.advertisers;

            advertiserList.sort(function (a,b) {
                if (a.name > b.name) {
                    return 1;
                }
                if (a.name < b.name) {
                    return -1;
                }
                // a must be equal to b
                return 0;
            });

            advertiserList.forEach(function(item){
                $scope.advertisers.push(item);
            });

			$scope.reportTabs = [{title: 'Publishers', disabled: false}, {title: 'Advertisers', disabled: false}];
			$scope.previousTab = $scope.reportTabs[0];
			$scope.setReportTab($scope.reportTabs[0]);

			$scope.isDataLoading = false;

		}).error(function ()
		{
			$scope.isDataLoading = false;
			$scope.loadingFailed = true;
		});
	}

    function fetchFreeDashboardData(){
        $scope.isDataLoading = true;
        $scope.loadingFailed = false;
        $scope.noDataForPublisher = false;
		$scope.noDemandSourcesForPublisher = false;

        var startDate = moment(new Date($scope.dates.startDate)).format("YYYY-MM-DD");
        var endDate = moment(new Date($scope.dates.endDate)).format("YYYY-MM-DD");

        var url = "/analytics/api/1.0/dashboard/free/?start_date=" + startDate + "&end_date=" + endDate;

        $http.get(url).success(function(data){

			$scope.demandSourceCount = data.advertiser_count;
            $scope.dashboardData = data;

            $scope.dashboardSummaryData = data.summary;

            if ($scope.dashboardSummaryData.requests == 0)
            {
				if($scope.user.accountType == "FREE" && $scope.demandSourceCount == 0)
				{
					$scope.noDataForPublisher = false;
					$scope.noDemandSourcesForPublisher = true;
					$('#myModal').modal();
				} else {
					$scope.noDataForPublisher = true;
				}
            }

            $scope.isDataLoading = false;

			for (var i = 0; i < data.impression_graph_data.length; i++)
			{
				data.impression_graph_data[i].date = moment(data.impression_graph_data[i].date).format("LL");
			}

            for (var i = 0; i < data.revenue_graph_data.length; i++)
            {
                data.revenue_graph_data[i].date = moment(data.revenue_graph_data[i].date).format("LL");
                data.revenue_graph_data[i].revenue = data.revenue_graph_data[i].revenue / 100;
                data.revenue_graph_data[i].ecpm = data.revenue_graph_data[i].ecpm / 100;
            }

			data.date_report_data.totals = {impressions: 0, requests: 0, publisher_revenue: 0};

			for (var i = 0; i < data.date_report_data.length; i++)
			{

				if (data.date_report_data[i]["entity.type"] == "date")
				{
					data.date_report_data[i].date = moment(data.date_report_data[i]["entity.id"],"YYYYMMDD").toDate();
					data.date_report_data[i].date_formatted = moment(data.date_report_data[i]["entity.id"],"YYYYMMDD").format("LL");
				}

				data.date_report_data[i].revenue = data.date_report_data[i].revenue_cents / 100;

				data.date_report_data.totals.impressions += data.date_report_data[i].impressions;
				data.date_report_data.totals.requests += data.date_report_data[i].requests;
				data.date_report_data.totals.revenue += data.date_report_data[i].revenue;
			}

			data.date_report_data.totals.fill_rate = 0;

			if (data.date_report_data.totals.requests > 0)
			{
				data.date_report_data.totals.fill_rate = Math.round((data.date_report_data.totals.impressions * 100) / data.date_report_data.totals.requests);
			}

			data.advertiser_report_data.totals = {impressions: 0, requests: 0, revenue: 0};

			for (var i = 0; i < data.advertiser_report_data.length; i++)
			{

				if (data.advertiser_report_data[i]["entity.type"] == "advertiser")
				{
					data.advertiser_report_data[i].advertiser_name = data.advertiser_report_data[i]["entity.name"];
					data.advertiser_report_data[i].advertiser_id = data.advertiser_report_data[i]["entity.id"];
				}

				data.advertiser_report_data[i].revenue = data.advertiser_report_data[i].revenue_cents / 100;

				data.advertiser_report_data.totals.impressions += data.advertiser_report_data[i].impressions;
				data.advertiser_report_data.totals.requests += data.advertiser_report_data[i].requests;
				data.advertiser_report_data.totals.revenue += data.advertiser_report_data[i].revenue;
			}

			data.advertiser_report_data.totals.fill_rate = 0;

			if (data.advertiser_report_data.totals.requests > 0)
			{
				data.advertiser_report_data.totals.fill_rate = Math.round((data.advertiser_report_data.totals.impressions * 100) / data.advertiser_report_data.totals.requests);
			}

            $scope.dashboardImpressionRequests = data.impression_graph_data;
            $scope.dashboardRevenue = data.revenue_graph_data;
			$scope.dashboardDateReport = data.date_report_data;
			$scope.dashboardAdvertiserReport = data.advertiser_report_data;

			$scope.reportTabs = [{title: 'Dates', disabled: false},{title: 'Advertisers', disabled: false}];
			$scope.previousTab = $scope.reportTabs[0];
			$scope.setReportTab($scope.reportTabs[0]);

			$scope.isDataLoading = false;

        }).error(function ()
        {
            $scope.isDataLoading = false;
            $scope.loadingFailed = true;
        });

    }

    function fetchNetworkDemandData(){
        $scope.isDataLoading = true;
        $scope.loadingFailed = false;
        $scope.noDataForPublisher = false;
        $scope.noDemandSourcesForPublisher = false;

        var startDate = moment(new Date($scope.dates.startDate)).format("YYYY-MM-DD");
        var endDate = moment(new Date($scope.dates.endDate)).format("YYYY-MM-DD");

        var params = {
            start_date: startDate,
            end_date: endDate,
        };

        if($scope.advertiserId){
            params.demand_source_id = $scope.advertiserId;
        }

        var url = "/analytics/api/1.0/dashboard/network/demand-performance/";

        $http.get(url, {params: params}).success(function(data) {

            $scope.isDataLoading = false;

            $scope.demandSourceCount = data.advertiser_count;
            $scope.dashboardData = data;

            $scope.currentAdvertiserDrillDownName = data.selected_advertiser_name;

            $scope.dashboardSummaryData = data.summary;

            for (var i = 0; i < data.impression_graph_data.length; i++)
            {
                data.impression_graph_data[i].date = moment(data.impression_graph_data[i].date).format("LL");
            }

            for (var i = 0; i < data.revenue_graph_data.length; i++)
            {
                data.revenue_graph_data[i].date = moment(data.revenue_graph_data[i].date).format("LL");
                data.revenue_graph_data[i].revenue = data.revenue_graph_data[i].revenue / 100;
                data.revenue_graph_data[i].ecpm = data.revenue_graph_data[i].ecpm / 100;
            }

            data.date_report_data.totals = {
				impressions: 0,
				requests: 0,
				revenue: 0,
				ecpm_sum: 0,
				ecpm: 0,
				rcpm_sum: 0,
				rcpm: 0
			};

            for (var i = 0; i < data.date_report_data.length; i++)
            {

                if (data.date_report_data[i]["entity.type"] == "date")
                {
                    data.date_report_data[i].date = moment(data.date_report_data[i]["entity.id"],"YYYYMMDD").toDate();
                    data.date_report_data[i].date_formatted = moment(data.date_report_data[i]["entity.id"],"YYYYMMDD").format("LL");
                }

                data.date_report_data[i].revenue = data.date_report_data[i].revenue_cents / 100;

                data.date_report_data.totals.impressions += data.date_report_data[i].impressions;
                data.date_report_data.totals.requests += data.date_report_data[i].requests;
                data.date_report_data.totals.revenue += data.date_report_data[i].revenue;

                data.date_report_data[i].ecpm = data.date_report_data[i].ecpm_cents / 100;
                data.date_report_data[i].rcpm = data.date_report_data[i].rcpm_cents / 100;

            }

            data.date_report_data.totals.fill_rate = 0;

            if (data.date_report_data.totals.requests > 0)
            {
                data.date_report_data.totals.fill_rate = Math.round((data.date_report_data.totals.impressions * 100) / data.date_report_data.totals.requests);
            }

            data.date_report_data.totals.ecpm = Math.round (((data.date_report_data.totals.revenue / data.date_report_data.totals.impressions) * 1000) * 100) / 100;
            data.date_report_data.totals.rcpm = Math.round (((data.date_report_data.totals.revenue / data.date_report_data.totals.requests) * 1000) * 100) / 100;



            data.advertiser_report_data.totals = {
                impressions: 0,
                requests: 0,
                revenue: 0,
                ecpm_sum: 0,
                ecpm: 0,
                rcpm_sum: 0,
                rcpm: 0
            };

            for (var i = 0; i < data.advertiser_report_data.length; i++)
            {

                if (data.advertiser_report_data[i]["entity.type"] == "advertiser")
                {
                    data.advertiser_report_data[i].advertiser_name = data.advertiser_report_data[i]["entity.name"];
                    data.advertiser_report_data[i].advertiser_id = data.advertiser_report_data[i]["entity.id"];
                }

                if (data.advertiser_report_data[i]["entity.type"] == "lineitem")
                {
                    data.advertiser_report_data[i].lineitem_name = data.advertiser_report_data[i]["entity.name"];
                }

                data.advertiser_report_data[i].revenue = data.advertiser_report_data[i].revenue_cents / 100;

                data.advertiser_report_data[i].ecpm = data.advertiser_report_data[i].ecpm_cents / 100;
                data.advertiser_report_data[i].rcpm = data.advertiser_report_data[i].rcpm_cents / 100;

                data.advertiser_report_data.totals.impressions += data.advertiser_report_data[i].impressions;
                data.advertiser_report_data.totals.requests += data.advertiser_report_data[i].requests;
                data.advertiser_report_data.totals.revenue += data.advertiser_report_data[i].revenue;

            }

            data.advertiser_report_data.totals.fill_rate = 0;

            if (data.advertiser_report_data.totals.requests > 0)
            {
                data.advertiser_report_data.totals.fill_rate = Math.round((data.advertiser_report_data.totals.impressions * 100) / data.advertiser_report_data.totals.requests);
            }

            data.advertiser_report_data.totals.ecpm = Math.round (((data.advertiser_report_data.totals.revenue / data.advertiser_report_data.totals.impressions) * 1000) * 100) / 100;
            data.advertiser_report_data.totals.rcpm = Math.round (((data.advertiser_report_data.totals.revenue / data.advertiser_report_data.totals.requests) * 1000) * 100) / 100;


            $scope.dashboardImpressionRequests = data.impression_graph_data;
            $scope.dashboardRevenue = data.revenue_graph_data;
            $scope.dashboardDateReport = data.date_report_data;
            $scope.dashboardAdvertiserReport = data.advertiser_report_data;

            $scope.advertisers = [];

            var advertiserList = data.advertisers;

            advertiserList.sort(function (a,b) {
                if (a.name > b.name) {
                    return 1;
                }
                if (a.name < b.name) {
                    return -1;
                }
                // a must be equal to b
                return 0;
            });

            advertiserList.forEach(function(item){
                $scope.advertisers.push(item);
            });


            $scope.reportTabs = [{title: 'Dates', disabled: false},{title: 'Advertisers', disabled: false}];
            $scope.previousTab = $scope.reportTabs[0];
            $scope.setReportTab($scope.reportTabs[0]);
        });
    }

	$scope.selectPublisher = function (lineItem)
	{
		$scope.setPublisher(lineItem.publisher_id);
	};

	$scope.selectSite = function (lineItem)
	{
		$scope.setSite(lineItem.site_id);
	};

    $scope.selectAdvertiser = function (lineItem)
    {
        $scope.setAdvertiser(lineItem.advertiser_id);
    };

	$scope.setReportTab = function (tab)
	{
		$scope.currentReportTab = tab;
		setTableExportUrl();
	};

	$scope.togglePublisherPredicate = function (predicate)
	{
		if ($scope.publisherReportPredicate == predicate)
		{
			$scope.publisherReportReverse = !$scope.publisherReportReverse;
		}
		else
		{
			$scope.publisherReportPredicate = predicate;
			$scope.publisherReportReverse = false;
		}
	};

	$scope.toggleAdvertiserPredicate = function (predicate)
	{
		if ($scope.advertiserReportPredicate == predicate)
		{
			$scope.advertiserReportReverse = !$scope.advertiserReportReverse;
		}
		else
		{
			$scope.advertiserReportPredicate = predicate;
			$scope.advertiserReportReverse = false;
		}
	};

	$scope.fetchDashboardData = fetchDashboardData;

}]);
