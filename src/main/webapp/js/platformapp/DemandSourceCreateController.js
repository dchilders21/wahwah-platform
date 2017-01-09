function DemandSourceCreateController($http,$window,$user,$location,$routeParams,$timeout,$cookies){
	var ctrl = this;
	ctrl.demandSource = {};
	ctrl.connectionTypeFields = {};

	ctrl.isCreating = true;
	ctrl.showVASTWarning = false;
	ctrl.showDemandSourceLogos = false;
	ctrl.shouldLoadConnectionList = false;

	ctrl.needsAuthorization = ['GOOGLE_ADSENSE','GOOGLE_ADX'];
	ctrl.hasAuthorization = [];

	if($routeParams.demandSourceId){

		ctrl.isCreating = false;

		$http.get(getUrl("/api/1.0/demand-sources/" + $routeParams.demandSourceId)).success(function(data){
			ctrl.demandSource = data;
		});
	}

	var isAdSenseCallback = false;
	var isGoogleAdXCallack = false;

	if($routeParams.googleService == "adsense"){
		isAdSenseCallback = true;
	}

	if($routeParams.googleService == "adx"){
		isGoogleAdXCallack = true;
	}

	if(isAdSenseCallback){
		ctrl.isCreating = true;
		ctrl.hasAuthorization.push('GOOGLE_ADSENSE');

		ctrl.connectionTypeFields["adsense_refresh_token"] = $routeParams.token;
	}

	if(isGoogleAdXCallack){
		ctrl.isCreating = true;
		ctrl.hasAuthorization.push('GOOGLE_ADX');

		ctrl.connectionTypeFields["adx_refresh_token"] = $routeParams.token;
	}

	if(isAdSenseCallback || isGoogleAdXCallack){
		var googleAuthorizationObject = $cookies.getObject("GoogleAuthorization");

		if(googleAuthorizationObject){

			var path = "/demand-source-" + googleAuthorizationObject.demand_source_id + "/connections/";

			if(googleAuthorizationObject.connection_id){
				path += googleAuthorizationObject.connection_id;
			} else {
				path += "create";
			}

			$location.path(path);
			$location.search("google_service",$routeParams.googleService);
		}
	}

	ctrl.getUrl = function(url){
		return $window.getUrl(url);
	};

	$user.getUserPromise().then(function(user){
		ctrl.user = user;
	});

	ctrl.save = function(){

		if(ctrl.demandSource.demand_source_id){

			$http.put(getUrl("/api/1.0/demand-sources/" + ctrl.demandSource.demand_source_id),ctrl.demandSource).success(function(data){

				ctrl.demandSource = data;
				ctrl.showSaveSuccess = true;

				$timeout(function(){
					ctrl.showSaveSuccess = false;
				},5000);

			});

		} else {
			$http.post(getUrl("/api/1.0/demand-sources/"),ctrl.demandSource).success(function(data){

				ctrl.demandSource = data;
				$location.path("/demand-sources/" + ctrl.demandSource.demand_source_id);

			});
		}
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

	ctrl.setConnectionParams = function(){
		ctrl.connectionTypeFields = {};

        if(ctrl.selectedPopularDemandSource.extra_data){
            for(var key in ctrl.selectedPopularDemandSource.extra_data){
                ctrl.connectionTypeFields[key] = ctrl.selectedPopularDemandSource.extra_data[key];
            }
        }
	};

	ctrl.saveNewConnection = function(){

		doValidateCredentials(function(isValid){

			if(isValid) {

				var name;

				if(ctrl.user.accountName != null){
					name = ctrl.user.accountName + " - " + ctrl.selectedPopularDemandSource.demand_source_name;
				} else {
					name = ctrl.selectedPopularDemandSource.demand_source_name;
				}

				var createModel = {
					demand_source: {
						name: name
					},
					connection: {
						type_key: ctrl.selectedPopularDemandSource.connection_type_key,
						type_name: ctrl.connectionTypes[ctrl.selectedPopularDemandSource.connection_type_key].name,
						connection_details: ctrl.connectionTypeFields
					}
				};

				$http.post(getUrl('/api/1.0/demand-sources/with-connection'), createModel).success(function (data) {
					$location.path("/demand-sources/" + data.demand_source_id);
				});
			} else {
				bootbox.alert("The provided credentials are incorrect. Please correct them and try again");
			}
		});
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

	$http.get(getUrl("/api/1.0/demand-sources/popular")).success(function(data){
		ctrl.popularDemandSources = data.demand_sources;
		ctrl.popularDemandSources.push({demand_source_name: "Other", is_request: true});

		if(isAdSenseCallback){
			for(var i = 0; i < ctrl.popularDemandSources.length; i++){
				if(ctrl.popularDemandSources[i].connection_type_key == "GOOGLE_ADSENSE"){
					ctrl.selectedPopularDemandSource = ctrl.popularDemandSources[i];
				}
			}
		}

		if(isGoogleAdXCallack){
			for(var i = 0; i < ctrl.popularDemandSources.length; i++){
				if(ctrl.popularDemandSources[i].connection_type_key == "GOOGLE_ADX"){
					ctrl.selectedPopularDemandSource = ctrl.popularDemandSources[i];
				}
			}
		}

		$http.get(getUrl("/api/1.0/demand-sources/connection-types")).success(function(data){
			ctrl.connectionTypes = {};

			for(var i = 0; i < data.connection_types.length; i++){
				var typeKey = data.connection_types[i].type_key;
				ctrl.connectionTypes[typeKey] = data.connection_types[i];
				ctrl.connectionTypes[typeKey].connection_metadata = JSON.parse(ctrl.connectionTypes[typeKey].connection_metadata_encoded);

				ctrl.connectionTypes[typeKey].needs_auth = false;
				ctrl.connectionTypes[typeKey].has_auth = false;

				if(ctrl.needsAuthorization.indexOf(typeKey) >= 0){
					ctrl.connectionTypes[typeKey].needs_auth = true;
				}

				if(ctrl.hasAuthorization.indexOf(typeKey) >= 0){
					ctrl.connectionTypes[typeKey].needs_auth = false;
					ctrl.connectionTypes[typeKey].has_auth = true;
				}
			}
		});

	});

	ctrl.sendDemandSourceRequest = function(){

		if(ctrl.selectedPopularDemandSource.connection_type_key == "OTHER"){
			ctrl.demandSourceToRequest.name = ctrl.selectedPopularDemandSource.demand_source_name;
		}

		$http.post(getUrl("/api/1.0/demand-sources/request"),ctrl.demandSourceToRequest).success(function(){
			bootbox.alert("Thanks for your submission.  Depending on the source, you may be contacted by a Red Panda Platform Representative for additional information and for an associated timeframe for integration.");
		});
	};


}

wahwahApp.controller('DemandSourceCreateController', DemandSourceCreateController);