/**
 * Created by Brian.Bober on 4/14/2016.
 */


/* Not really sure why var ctrl = this; doesn't work */
function DemandSourceEditInfoPanelController($routeParams, $http)
{
	var ctrl = this;


	ctrl.$onInit = function()
	{

		ctrl.isCreating = ctrl.demandGlobalVars.isCreating;
		ctrl.tabs = ctrl.demandGlobalVars.tabs;

		ctrl.disableSubmit = ctrl.demandGlobalVars.disableSubmit;

		// ctrl.demandSourceConnections
		// ctrl.demandSource

		ctrl.showVASTWarning = false;
		ctrl.showDemandSourceLogos = false;
		ctrl.shouldLoadConnectionList = false;

		ctrl.needsAuthorization = ['GOOGLE_ADSENSE', 'GOOGLE_ADX'];
		ctrl.hasAuthorization = [];

		var isAdSenseCallback = false;
		var isGoogleAdXCallack = false;

		if (isAdSenseCallback)
		{
			ctrl.hasAuthorization.push('GOOGLE_ADSENSE');
			ctrl.connectionTypeFields["adsense_refresh_token"] = $routeParams.token;
		}

		if (isGoogleAdXCallack)
		{
			ctrl.hasAuthorization.push('GOOGLE_ADX');
			ctrl.connectionTypeFields["adx_refresh_token"] = $routeParams.token;
		}


		loadConnectionTypes();
	};


	ctrl.authorizeAccess  = function(){
		var selectedType = ctrl.selectedPopularDemandSource.connection_type_key;

		if(selectedType == "GOOGLE_ADSENSE"){
			window.location = "/google/adsense/login";
		}

		if(selectedType == "GOOGLE_ADX"){
			window.location = "/google/adx/login";
		}
	};

	ctrl.saveNewConnection = function(){

	};

	ctrl.validateCredentials = function(){
		doValidateCredentials(function(isValid){
			if(isValid){
				bootbox.alert("The provided credentials are correct");
			} else {
				bootbox.alert("The provided credentials are incorrect");
			}
		});
	};

	function doValidateCredentials(callback){
		ctrl.isTestingCredentials = true;

		var validateCredentialsRequest = {
			type_key: ctrl.selectedPopularDemandSource.connection_type_key,
			connection_details: ctrl.connectionTypeFields
		};

		$http.post(getUrl("/api/1.0/demand-sources/validate-credentials"),validateCredentialsRequest).success(function(data){

			ctrl.isTestingCredentials = false;
			callback(data.model);

		});
	}

	function loadConnectionTypes(){

		$http.get(getUrl("/api/1.0/demand-sources/connection-types")).success(function(data){
			ctrl.connectionTypes = data.connection_types;
            ctrl.connectionTypesByKey = {};

			for(var i = 0; i < ctrl.connectionTypes.length; i++){
                ctrl.connectionTypes[i].connection_metadata = JSON.parse(ctrl.connectionTypes[i].connection_metadata_encoded);

				var typeKey = ctrl.connectionTypes[i].type_key;

                ctrl.connectionTypesByKey[typeKey] = ctrl.connectionTypes[i];

                ctrl.connectionTypes[i].needs_auth = false;

				if(ctrl.needsAuthorization.indexOf(typeKey) >= 0){
                    ctrl.connectionTypes[i].needs_auth = true;
				}

				if(ctrl.hasAuthorization.indexOf(typeKey) >= 0){
                    ctrl.connectionTypes[i].needs_auth = false;
				}
			}
		});
	}
}

function DemandSourceEditLineItemPanelController($location)
{
	var ctrl = this;

    ctrl.selectedLineItem = null;

	ctrl.$onInit = function()
	{
		ctrl.lineItemsToShow = [];
		setVisibleLineItems();
	};

	function setVisibleLineItems(){
		var lineItemsToShow = [];

		if(ctrl.demandSourceLineItems) {

			for (var i = 0; i < ctrl.demandSourceLineItems.length; i++) {
				var lineItem = ctrl.demandSourceLineItems[i];

                if(lineItem.id && lineItem.id == $location.search().line_item_id){
                    ctrl.selectedLineItem = lineItem;
                }

				if (ctrl.demandSourceCreatives)
				{
					for (var j = 0; j < lineItem.ads.length; j++)
					{
						var ad = lineItem.ads[j];
						for (var k = 0; k < ctrl.demandSourceCreatives.length; k++)
						{
							var creative = ctrl.demandSourceCreatives[k];
							if (creative.id == ad.creative_id)
							{
								// WARNING: item.$$creative_name will not be serialized into JSON for output
								ad["$$creative_name"] = creative.name;
							}
						}
					}
				}

				if (lineItem.is_paused && ctrl.showPausedLineItems) {
					lineItemsToShow.push(lineItem);
				}
 				else if (!lineItem.is_paused) {
					lineItemsToShow.push(lineItem);
				}
			}

			ctrl.lineItemsToShow = lineItemsToShow;
		}

	}

	ctrl.$onChanges = function(changesObj){
		if(changesObj.demandSourceLineItems && (changesObj.demandSourceLineItems.currentValue || changesObj.demandSourceCreatives.currentValue))
		{
			setVisibleLineItems();
		}
	};

	ctrl.onLineItemEdit = function(lineItem)
	{
        lineItem.$$_rp_id = createUUID();
		ctrl.selectedLineItem = angular.copy(lineItem);

        $location.search("line_item_id",lineItem.id);
	};


	ctrl.onLineItemDelete = function(lineItem)
	{
		for (var i = 0; i < ctrl.demandSourceLineItems.length; i++)
		{
			var el = ctrl.lineItemsToShow[i];
			if (el.id == lineItem.id)
			{
				ctrl.demandSourceLineItems.splice(i,1);
			}
			setVisibleLineItems();
		}
	};


	ctrl.createLineItem = function()
	{
        var name = ctrl.demandSource.name + " - Line Item #" + (ctrl.demandSourceLineItems.length + 1);

        var newLineItem = {
            ads: [],
            targeting: [],
            name: name,
            is_paused: false,
            delivery_mode: null,
			targeting_cardinality: "INCLUSIVE",
            $$_rp_id: createUUID(),
        };

        ctrl.selectedLineItem = newLineItem;
	};

    ctrl.applyLineItem = function(){

        var foundLineItem = false;

        for(var i = 0; i < ctrl.demandSourceLineItems.length; i++){
            if(ctrl.demandSourceLineItems[i].$$_rp_id == ctrl.selectedLineItem.$$_rp_id){
                ctrl.demandSourceLineItems[i] = ctrl.selectedLineItem;
                foundLineItem = true;
                break;
            }
        }

        if(!foundLineItem){
            ctrl.demandSourceLineItems.push(ctrl.selectedLineItem);
        }

        ctrl.closeLineItem();
    };

    ctrl.closeLineItem = function(){
        ctrl.selectedLineItem = null;
        $location.search("line_item_id",null);
        $location.search("line_item_tab",null);
        setVisibleLineItems();
    };
}


function DemandSourceEditCreativePanelController($http)
{
	var ctrl = this;

    ctrl.selectedCreative = null;
    ctrl.newCreative = null;

    ctrl.newCreativeId = -1;

    ctrl.creativeTypeOptions = {
        HTML: 'Javascript',
        VAST_XML: 'Video (VAST)'
    };

    ctrl.platforms = {
        DESKTOP: 'Desktop',
        MOBILE: 'Mobile',
        ALL: 'Both Desktop & Mobile'
    };

    ctrl.createCreative = function(){
        ctrl.newCreative = {
            id: ctrl.newCreativeId--,
            creative_platform: 'DESKTOP'
        };
    };

    ctrl.addCreative = function(){
        ctrl.creatives.push(ctrl.newCreative);
        ctrl.newCreative = null;
    };

    ctrl.deleteCreative = function(creative){

        // Check to see if creative is associated

        var isAssociated = false;

        for(var i = 0; i < ctrl.lineItems.length; i++){

            var lineItem = ctrl.lineItems[i];

            for(var j = 0; j < lineItem.ads.length; j++){
                if(lineItem.ads[j].creative_id == creative.id){
                    isAssociated = true;
                }
            }
        }


        if(!isAssociated) {
            var ix = ctrl.creatives.indexOf(creative);
            ctrl.creatives.splice(ix, 1);
        } else {

            bootbox.alert("This creative cannot be removed because it's associated with an ad");

        }
    };
	
	ctrl.$onInit = function(){

        $http.get(getUrl("/api/1.0/creatives/vendors/HTML")).success(function(htmlData){
            $http.get(getUrl("/api/1.0/creatives/vendors/VAST_XML")).success(function(vastData){

                ctrl.vendorOptions = {
                    HTML: htmlData.vendors,
                    VAST_XML: vastData.vendors
                };

                ctrl.vendorOptions['HTML'].push({type: null, name: "Other"});
                ctrl.vendorOptions['VAST_XML'].push({type: null, name: "Other"});
                
            });
        });

	}
};


function DemandSourceLineItemDialogController($location){
    var ctrl = this;

    ctrl.tabs = [
        {name: 'General', id: 'line-item-info'},
        {name: 'Targeting', id: 'line-item-targeting'},
        {name: 'Ads', id: 'line-item-ads'},
		{name: 'Advanced', id: 'line-item-advanced'}
    ];

    ctrl.$onInit = function(){
        ctrl.currentTab = ctrl.tabs[0];

        var searchParams = $location.search();

        ctrl.selectedTabId = searchParams.line_item_tab;

        for(var i = 0; i < ctrl.tabs.length; i++){
            if(ctrl.selectedTabId == ctrl.tabs[i].id) {
                ctrl.currentTab = ctrl.tabs[i];
                break;
            }
        }
    };

    ctrl.$onChanges = function(changesObj){

        if(!changesObj.lineItem || !changesObj.lineItem.currentValue){
            ctrl.currentTab = ctrl.tabs[0];
        }

    };
    
    ctrl.setCurrentTab = function(tab){
        $location.search("line_item_tab",tab.id);
    };
}

function DemandSourceLineItemTargetingController($http)
{

	var ctrl = this;

	ctrl.start = function()
	{
		ctrl.cardinality = {"INCLUSIVE": {value: "INCLUSIVE", name: "Include only chosen"}, "EXCLUSIVE": {value: "EXCLUSIVE", name: "Exclude chosen"}, "INCLUDE_ALL": {value: "INCLUDE_ALL", name: "Include all"}};
		ctrl.safeToShow = false;
		ctrl.allSites = [];
		ctrl.publisherEntries = [];
		ctrl.siteEntries = [];
		ctrl.selectedEntries = [];
		ctrl.selectedTargetingRules = [];
		ctrl.backupLineItemTargeting = null;
		ctrl.targetingRules = [];
		ctrl.filterText = "";
		ctrl.chosenCardinality = null; // Set when lineItem is avail
		ctrl.oldSelections = {};
	};

	ctrl.$onInit = function()
	{
		ctrl.start();
	};

	ctrl.$onChanges = function(changesObj){
		if(changesObj.lineItem)
		{
			ctrl.start();
			
            if (ctrl.lineItem){
                ctrl.targetingRules = ctrl.lineItem.targeting;

				ctrl.chosenCardinality = ctrl.lineItem.targeting_cardinality;
            }
				
			ctrl.getAllEntries();


			if (ctrl.chosenCardinality == "EXCLUSIVE" && ctrl.targetingRules.length <= 0 )
			{

				ctrl.chosenCardinality = "INCLUDE_ALL";
				ctrl.$onChanges(ctrl.chosenCardinality);
			}

		}

		if (changesObj === ctrl.chosenCardinality)
		{
			if (ctrl.chosenCardinality=="INCLUDE_ALL")
			{
				ctrl.lineItem.targeting_cardinality = "EXCLUSIVE";
				ctrl.backupLineItemTargeting = [];
				angular.copy(ctrl.lineItem.targeting, ctrl.backupLineItemTargeting); // Save in case entry is changed
				ctrl.targetingRules = [];
				angular.copy(ctrl.targetableList, ctrl.targetingRules);
				ctrl.lineItem.targeting = []; // Exclude with none chosen :-)
			}
			else
			{
				ctrl.lineItem.targeting_cardinality = ctrl.chosenCardinality;
				if (ctrl.backupLineItemTargeting != null)
				{
					angular.copy(ctrl.backupLineItemTargeting, ctrl.lineItem.targeting);
					ctrl.targetingRules = ctrl.lineItem.targeting;
					ctrl.backupLineItemTargeting = null;
				}
			}
		}

	};

	ctrl.getAllEntries = function()
	{
		$http.get(getUrl("/api/2.0/demand-sources/targetable-entities")).success(function(data){
			var networks = data.targetable_entities;

            var list = [];

            // Network
            for(var i = 0; i < networks.length; i++){
                var network = networks[i];
                network.isAdded = false;
                network.tree_name = network.name;

                list.push(network);

                var lastPublisherIndex = network.children.length - 1;

                // Publisher
                if(network.children.length > 0){
                    for(var j = 0; j < network.children.length; j++){
                        var publisher = network.children[j];
                        publisher.isAdded = false;

                        publisher.tree_name = "╟-- " + publisher.name;


                        if(j == lastPublisherIndex){
                            publisher.tree_name = "╙- " + publisher.name;
                        }

                        list.push(publisher);

                        var lastSiteIndex = publisher.children.length - 1;

                        // Site
                        if(publisher.children.length > 0){
                            for(var k = 0; k < publisher.children.length; k++){
                                var site = publisher.children[k];
                                site.isAdded = false;

                                site.tree_name = "║ ╟--- " + site.name;

                                if(lastSiteIndex == k){
                                    site.tree_name = "║ ╙---- " + site.name;
                                }

                                if(lastPublisherIndex == j && lastSiteIndex != k){
                                    site.tree_name = " ╟---- " + site.name;
                                }

                                if(lastPublisherIndex == j && lastSiteIndex == k){
                                    site.tree_name = " ╙---- " + site.name;
                                }

                                list.push(site);
                            }
                        }
                    }

                }
            }

            ctrl.targetableList = list;

            if(ctrl.targetingRules){
                for(var i = 0; i < ctrl.targetingRules.length; i++){

                    for(var j = 0; j < ctrl.targetableList.length; j++){

                        if(ctrl.targetingRules[i].targetable_type == ctrl.targetableList[j].type){

                            switch(ctrl.targetingRules[i].targetable_type){
                                case "NETWORK":
                                    if(ctrl.targetingRules[i].network_id == ctrl.targetableList[j].id){
                                        ctrl.targetingRules[i].$$entity = ctrl.targetableList[j];
                                    }

                                    break;
                                case "PUBLISHER":
                                    if(ctrl.targetingRules[i].publisher_id == ctrl.targetableList[j].id){
                                        ctrl.targetingRules[i].$$entity = ctrl.targetableList[j];
                                    }

                                    break;
                                case "SITE":
                                    if(ctrl.targetingRules[i].site_id == ctrl.targetableList[j].id){
                                        ctrl.targetingRules[i].$$entity = ctrl.targetableList[j];
                                    }

                                    break;
                            }

                        }

                    }

                }
            }
		});
	};

	ctrl.addRules = function()
	{
		for (var i = 0; i < ctrl.selectedEntries.length; i++)
		{
			var entity = ctrl.selectedEntries[i];
            var networkId = null;
			var publisherId = null;
			var siteId = null;
			var productId = null;

			if (!entity.isAdded && entity.type)
			{
                if (entity.type == "NETWORK")
                {
                    networkId = entity.id;
                }

				if (entity.type == "SITE")
				{
					siteId = entity.id;
				}

				if (entity.type == "PUBLISHER")
				{
					publisherId = entity.id;
				}

				ctrl.targetingRules.push({
                    line_item_id: ctrl.lineItem.id,
                    targetable_type: entity.type,
                    name: entity.name,
                    network_id: networkId,
                    publisher_id: publisherId,
                    site_id: siteId,
                    productId: productId,
                    display_type: "BACKUP",
                    $$entity: entity
                });

                ctrl.lineItem.targeting = ctrl.targetingRules;

                entity.isAdded = true;
			}
		}
	};

	ctrl.removeRules = function()
	{
		for (var t = 0; t < ctrl.selectedTargetingRules.length; t++)
		{
			for (var i = 0 ; i < ctrl.targetingRules.length; i++)
			{
				if (ctrl.targetingRules[i].name == ctrl.selectedTargetingRules[t].name)
				{
					ctrl.targetingRules[i].$$entity.isAdded = false;
					ctrl.targetingRules.splice(i,1);
				}
			}
		}
	};
}

function DemandSourceLineItemAdvancedController($http)
{
	var ctrl = this;

	ctrl.$onInit = function(){
	    ctrl.timeFrame = [ "Day", "Week", "Month" ];

        $http.get("/analytics/api/1.0/custom-reports/dimensions/Country").success(function(data){
           ctrl.countries = data;
        });
	};
}


function DemandSourceLineItemInfoController()
{
	var ctrl = this;
    ctrl.allowDeliveryEdit = false;

    ctrl.deliveryModes = {
        'WEB': 'Web',
        'LINEAR_VIDEO': 'Video'
    };

	ctrl.cpmTypes = {
	    'GUARANTEED': 'Guaranteed CPM',
        'GUARANTEED_MINIMUM': 'Guaranteed Minimum CPM',
        'ESTIMATED': 'Estimated CPM'
	};

    ctrl.$onInit = function(){
        if(ctrl.lineItem != null) {
            ctrl.allowDeliveryEdit = (ctrl.lineItem.delivery_mode == null);

            if(ctrl.lineItem.cpm_type == null){
                ctrl.lineItem.cpm_type = 'ESTIMATED';
            }
        }
    };

    ctrl.$onChanges = function(changesObj){
        ctrl.allowDeliveryEdit = false;

        if(changesObj.lineItem && changesObj.lineItem.currentValue) {
            ctrl.allowDeliveryEdit = (changesObj.lineItem.currentValue.delivery_mode == null);

            if(ctrl.lineItem.cpm_type == null){
                ctrl.lineItem.cpm_type = 'ESTIMATED';
            }
        }
    };
};

function DemandSourceLineItemAdController($scope){
    var ctrl = this;

    ctrl.newAd = null;
    ctrl.selectedAd = null;

    ctrl.webCreatives = [];
    ctrl.videoCreatives = [];

    ctrl.runningOptions = [
        {name: 'Running', value: false},
        {name: 'Paused', value: true}
    ];

    ctrl.$onInit = function(){
		if(ctrl.creatives) {
			setCreatives(ctrl.creatives);
		}
    };

    ctrl.$onChanges = function(changesObj){
        if(changesObj.creatives && changesObj.creatives.currentValue){
            setCreatives(changesObj.creatives.currentValue);
        }
    };

    function setCreatives(creatives){

        ctrl.webCreatives = [];
        ctrl.videoCreatives = [];

        for(var i = 0; i < creatives.length; i++){

            var creative = creatives[i];

            if(creative.content_type == "HTML"){
                ctrl.webCreatives.push(creative);
            }

            if(creative.content_type == "VAST_XML"){
                ctrl.videoCreatives.push(creative);
            }
        }
    }

    ctrl.selectAd = function(ad){
		ad.is_editing = true;
        ctrl.newAd = null;
    };

    ctrl.createAd = function(){
        ctrl.selectedAd = null;
        ctrl.newAd = {};
		ctrl.newAd.is_editing = true;

		if(ctrl.lineItem.enable_manual_weighting){
			ctrl.newAd.weight_percentage = 0;
		}

		ctrl.ads.push(ctrl.newAd);
        ctrl.newAd = null;
    };

	ctrl.saveAd = function(ad) {
		ad.is_editing = false;
        ctrl.newAd = null;
	};

	ctrl.removeAd = function(ad) {
		var i = ctrl.ads.indexOf(ad);
		ctrl.ads.splice(i,1);
	};

    ctrl.addAd = function(){

        if(ctrl.lineItem.enable_manual_weighting){
            ctrl.newAd.weight_percentage = 0;
        }

        ctrl.ads.push(ctrl.newAd);
        ctrl.selectedAd = null;
        ctrl.newAd = null;
    };

    $scope.$watch('$ctrl.lineItem.enable_manual_weighting',function(newValue,previousValue){
        if(newValue === true && previousValue === false){
            
            var defaultWeight = Math.floor(100 / ctrl.ads.length);
            var firstItemWeight = defaultWeight;
            
            if(defaultWeight * ctrl.ads.length < 100){
                firstItemWeight += (100 - (defaultWeight * ctrl.ads.length));
            }
            
            for(var i = 0; i < ctrl.ads.length; i++){
                if(i == 0){
                    ctrl.ads[i].weight_percentage = firstItemWeight;
                } else {
                    ctrl.ads[i].weight_percentage = defaultWeight;
                }
            }
            
        }
    });
}

function DemandSourcePlacementController($http,$location){
    var ctrl = this;

    ctrl.$onInit = function() {
        reset();
    };

    ctrl.$onChanges = function(changesObj){
        if(changesObj.placements)
        {
            reset();
        }

        if(changesObj.account){
			ctrl.tabs = [];

			if(ctrl.account) {
				if (ctrl.account.account_type == "REPORTING_PRO") {
					ctrl.tabs.push({name: 'Site Assignment', id: 'site-assignment'});
				}

				ctrl.tabs.push({name: 'Financial Model', id: 'financial-model'});
			}

			reset();
		}
    };

    function reset(){

    	if(ctrl.tabs){
			if(ctrl.tabs.length > 0){
				ctrl.currentTab = ctrl.tabs[0];
			}

			var searchParams = $location.search();

			ctrl.selectedTabId = searchParams.placement_tab;

			for (var i = 0; i < ctrl.tabs.length; i++) {
				if (ctrl.selectedTabId == ctrl.tabs[i].id) {
					ctrl.currentTab = ctrl.tabs[i];
					break;

				}
			}
		}

		ctrl.targetedEntities = [];
        ctrl.selectedTargetedEntities = [];
        ctrl.getAllEntries();
    }

    ctrl.setCurrentTab = function(tab){
        $location.search("placement_tab",tab.id);
    };

    ctrl.selectPlacement = function(placement){
        ctrl.selectedPlacement = placement;
        reset();
    };

    ctrl.addRules = function(){
        for (var i = 0; i < ctrl.selectedEntries.length; i++)
        {
            var entity = ctrl.selectedEntries[i];
            var siteId = null;

            if (!entity.isAdded && entity.type)
            {
                if (entity.type == "SITE")
                {
                    siteId = entity.id;
                }

                ctrl.selectedPlacement.targeted_sites.push(siteId);
                ctrl.targetedEntities.push(entity);

                entity.isAdded = true;
            }
        }
    };

    ctrl.removeRules = function(){
        for (var t = 0; t < ctrl.selectedTargetedEntities.length; t++)
        {
            for (var i = 0 ; i < ctrl.targetedEntities.length; i++)
            {
                if (ctrl.targetedEntities[i].type == ctrl.selectedTargetedEntities[t].type && ctrl.targetedEntities[i].id == ctrl.selectedTargetedEntities[t].id)
                {
                    ctrl.targetedEntities[i].isAdded = false;
                    ctrl.targetedEntities.splice(i,1);
                }
            }
        }
    };

    ctrl.getAllEntries = function()
    {
        $http.get(getUrl("/api/2.0/demand-sources/targetable-entities")).success(function(data){
            var networks = data.targetable_entities;

            var list = [];
            var siteIdMap = {};

            // Network
            for(var i = 0; i < networks.length; i++){
                var network = networks[i];
                network.isAdded = false;

                // Publisher
                if(network.children.length > 0){
                    for(var j = 0; j < network.children.length; j++){
                        var publisher = network.children[j];
                        publisher.isAdded = false;

                        // Site
                        if(publisher.children.length > 0){
                            for(var k = 0; k < publisher.children.length; k++){
                                var site = publisher.children[k];
                                site.isAdded = false;
                                list.push(site);
                                siteIdMap[site.id] = site;
                            }
                        }
                    }

                }
            }

            list.sort(function(a,b){
				return a.name.localeCompare(b.name);
			});

            ctrl.targetableList = list;

			if(ctrl.selectedPlacement != null) {
				for (var i = 0; i < ctrl.selectedPlacement.targeted_sites.length; i++) {
					var siteId = ctrl.selectedPlacement.targeted_sites[i];

					if (siteIdMap.hasOwnProperty(siteId)) {
					    var site = siteIdMap[siteId];
						ctrl.targetedEntities.push(site);
					}
				}
			}

          });
    };
}

angular.module('wahwah.components').component('demandSourceEditInfoPanel', {
	templateUrl: getUrl('/components/DemandSource/demand-source-info.html'),
	controller: DemandSourceEditInfoPanelController,
	bindings: {
		demandSource: '=',
		demandGlobalVars: '=',
		demandSourceConnections: '='
	}
});

angular.module('wahwah.components').component('demandSourceEditLineItemsPanel', {
	templateUrl: getUrl('/components/DemandSource/demand-source-lineitems.html'),
	controller: DemandSourceEditLineItemPanelController,
	bindings: {
		demandSource: '=',
		demandGlobalVars: '=',
		demandSourceLineItems: '<',
		demandSourceCreatives: '<'
	}
});

angular.module('wahwah.components').component('demandSourceLineItemTargetingPanel', {
	templateUrl: getUrl('/components/DemandSource/demand-source-line-item-targeting.html'),
	controller: DemandSourceLineItemTargetingController,
	bindings: {
		lineItem: '<',
		creatives: '<'
	}
});

angular.module('wahwah.components').component('demandSourceLineItemAdvancedPanel', {
	templateUrl: getUrl('/components/DemandSource/demand-source-line-item-advanced.html'),
	controller: DemandSourceLineItemAdvancedController,
	bindings: {
		lineItem: '<',
		creatives: '<'
	}
});

angular.module('wahwah.components').component('demandSourceLineItemInfoPanel', {
	templateUrl: getUrl('/components/DemandSource/demand-source-line-item-info.html'),
	controller: DemandSourceLineItemInfoController,
	bindings: {
		lineItem: '<',
		creatives: '<'
	}
});

angular.module('wahwah.components').component('demandSourceEditCreativePanel', {
	templateUrl: getUrl('/components/DemandSource/demand-source-creative.html'),
	controller: DemandSourceEditCreativePanelController,
	bindings: {
		creatives: '<',
        lineItems: '<'
	}
});

angular.module('wahwah.components').component('demandSourceLineItemDialog', {
    templateUrl: getUrl('/components/DemandSource/demand-source-lineitem-dialog.html'),
    controller: DemandSourceLineItemDialogController,
    bindings: {
        lineItem: '<',
        creatives: '<'
    }
});

angular.module('wahwah.components').component('demandSourceLineItemAd', {
    templateUrl: getUrl('/components/DemandSource/demand-source-lineitem-ad.html'),
    controller: DemandSourceLineItemAdController,
    bindings: {
        lineItem: '<',
        ads: '<',
        creatives: '<'
    }
});

angular.module('wahwah.components').component('demandSourcePlacementPanel', {
    templateUrl: getUrl('/components/DemandSource/demand-source-placement.html'),
    controller: DemandSourcePlacementController,
    bindings: {
        placements: '<',
		account: '<'
    }
});