function UpsellFromFreeController($http){
    var ctrl = this;

    ctrl.sendUpgradeRequest = function(){
        $http.post(getUrl("/api/1.0/accounts/request-upgrade")).success(function(){
            bootbox.alert("Your upgrade request has been sent! Someone should be in touch shortly");
        })
    };
}

wahwahApp.controller('UpsellFromFreeController',UpsellFromFreeController);