/**
 * Created by Brian.Bober on 4/14/2016.
 */


function DemandSourceEditController($scope,$http,$window,$user,$location,$routeParams,$timeout,$cookies)
{

	$scope.$ctrl = {};
	var ctrl = this;

	ctrl.demandSource = {};
	ctrl.demandSourceManagement = {};
	ctrl.demandGlobalVars = {};
	ctrl.demandSourceLineItems = {};
	ctrl.demandSourceConnections = {};
	ctrl.demandSourceAdvanced = {
		test: "Hello World!"
	};



	ctrl.demandGlobalVars.isCreating = false;

	ctrl.tabs = [
		{name: 'General', id: 'demand-source-info'},
	];

	ctrl.demandGlobalVars.tabs = ctrl.tabs;


	ctrl.errorMsg = "";
	ctrl.demandGlobalVars.disableSubmit = ctrl.disableSubmit = false;
	ctrl.showSaveError = false;
	ctrl.showSaveSuccess = false;

	if($routeParams.demandSourceId){

		ctrl.demandGlobalVars.isCreating = false;
		ctrl.demandSourceId = $routeParams.demandSourceId;
		ctrl.disableSubmit = false;

		$http.get(getUrl("/api/2.0/demand-sources/" + $routeParams.demandSourceId)).success(function(data){
			ctrl.setDemandSourceVars(data);
		});
	}

    ctrl.hasLoadedUser = false;

	ctrl.setDemandSourceVars = function(data)
	{
		ctrl.demandSourceManagement = data;
		ctrl.demandSource = ctrl.demandSourceManagement.demand_source;
		ctrl.account = ctrl.demandSourceManagement.account;
		ctrl.demandSourceConnections = ctrl.demandSourceManagement.connections;
		ctrl.demandSourceTargetingInfo = ctrl.demandSourceManagement.targeting;
		ctrl.demandSourceLineItems = ctrl.demandSourceManagement.line_items;
		ctrl.creatives = ctrl.demandSourceManagement.creatives;
		ctrl.placements = ctrl.demandSourceManagement.placements;

        reloadDataFromSearch();
	};



	ctrl.setCurrentTab = function(tab){
		$location.search('tab',tab.id);
	};


	function reloadDataFromSearch()
	{
		ctrl.currentTab = ctrl.tabs[0];

		var searchParams = $location.search();

		ctrl.selectedTabId = searchParams.tab;

        if(!ctrl.hasLoadedUser){
            $user.getUserPromise().then(function(user){

                var doNotEdit = false;

                if(ctrl.demandSource.name == "OpenX - OpenX Market"){
                    doNotEdit = true;
                }

                ctrl.demandSource.is_locked = doNotEdit;

                ctrl.hasLoadedUser = true;

                ctrl.user = user;

                if((ctrl.account.account_type == "ROOT" || ctrl.account.account_type == "NETWORK") && !ctrl.demandGlobalVars.isCreating && !doNotEdit){
                    ctrl.tabs.push({name: 'Line Items', id: 'demand-source-line-item'});
                    ctrl.tabs.push({name: 'Creatives', id: 'demand-source-creative'});
					ctrl.tabs.push({name: 'Placements', id: 'demand-source-placements'});
                }

                if(ctrl.account.account_type == "REPORTING_PRO" && !ctrl.demandGlobalVars.isCreating && !doNotEdit){
                	ctrl.tabs.push({name: 'Placements', id: 'demand-source-placements'});
				}

				for(var i = 0; i < ctrl.tabs.length; i++){
					if(ctrl.selectedTabId == ctrl.tabs[i].id) {
						ctrl.currentTab = ctrl.tabs[i];
						break;
					}
				}
            });
        }


	}

	ctrl.getUrl = function(url){
		return $window.getUrl(url);
	};

	ctrl.save = function(){

		ctrl.disableSubmit = true;
		ctrl.showSaveError = false;

		$http.put(getUrl("/api/2.0/demand-sources/" + ctrl.demandSourceId), ctrl.demandSourceManagement).success(function(data){

				ctrl.setDemandSourceVars(data);

				ctrl.showSaveSuccess = true;
				ctrl.disableSubmit = false;

				$timeout(function(){
					ctrl.showSaveSuccess = false;
				},10000);
			})
			.error(function (data)
			{

				ctrl.disableSubmit = false;
				ctrl.showSaveError = true;

				var msgLower = "";

				if (typeof(data.message)!= "undefined" && data.message != null){
					msgLower = data.message.toString().toLowerCase();
				}


				ctrl.errorMsg = "Unable to save. Unknown error";


				$timeout(function ()
				{
					ctrl.showSaveError = false;
				}, 15000);
			});
	};





}

wahwahApp.controller('DemandSourceEditController', DemandSourceEditController);
