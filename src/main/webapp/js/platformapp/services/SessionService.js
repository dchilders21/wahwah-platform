angular.module('wahwah.components').service('SessionService',SessionService);

angular.module('wahwah.components').factory('SessionHttpInterceptor',function(SessionService){
    return {
        response: function(response) {
            var isPingUrl = response.config.url == getUrl("/api/1.0/users/ping");
            var isAnalyticsUrl = response.config.url.indexOf("/analytics/") == 0;

            if(!isPingUrl && !isAnalyticsUrl) {
                SessionService.updateLastRequestTime();
            }

            return response;
        }
    };
});

angular.module('wahwah.components').config(function($httpProvider){
    $httpProvider.interceptors.push('SessionHttpInterceptor');
});

function SessionService($q,$interval){

    var service = this;

    this.lastRequestTime = new Date().getTime();

    function init(){
        service.sessionTimeoutDeferred = $q.defer();
        service._promise = service.sessionTimeoutDeferred.promise;
    }

    init();

    var CHECK_INTERVAL_TIME = 15 * 1000;

    $interval(function(){

        var currentTime = new Date().getTime();
        var expiredTime = service.lastRequestTime + (10 * 60 * 1000); // 10 minutes

        if(currentTime > expiredTime){
            service.sessionTimeoutDeferred.resolve({current: currentTime, expiresAt: expiredTime});
            init();
        }

    },CHECK_INTERVAL_TIME);

    this.getSessionTimeoutPromise = function(){

        var instanceDeferred = $q.defer();
        var instancePromise = instanceDeferred.promise;

        service._promise.then(function(timeoutObject){
            instanceDeferred.resolve(timeoutObject);
        });

        return instancePromise;
    };

    this.updateLastRequestTime = function(){
        service.lastRequestTime = new Date().getTime();
    };
}