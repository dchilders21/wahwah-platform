angular.module('wahwah.components').service('$user',UserService);

function UserService($http,$q,$window) {

    var userDeferred = $q.defer();
    var userPromise = userDeferred.promise;

    var scope = {};
    scope.user = {};
    scope.user.roles = [];

    $http.get(getUrl("/api/1.0/users/current/")).success(function (data) {
        scope.user = data;
        userDeferred.resolve(scope.user);
    });

    this.hasRole =function (roleName) {

        if (roleName == "NETWORK_USER") {
            return scope.user.accountType == "ROOT" || scope.user.accountType == "NETWORK" || scope.user.accountType == "REPORTING_PRO";
        }

        if (roleName == "DEMAND_SOURCE_ADMIN") {
            return this.hasRole("NETWORK_ADMIN") || scope.user.accountType == "FREE";
        }

        if (roleName == "AD_OPS_ADMIN"){

            if(scope.user.accountType == "FREE"){
                return false;
            }

            return this.hasRole("NETWORK_ADMIN") || this.hasRole("INTERNAL_USER");
        }

        return $window.hasRole(scope.user.roles, roleName);
    };

    this.hasRolePromise = function (roleName) {
        var rolesDeferred = $q.defer();
        var rolesPromise = rolesDeferred.promise;

        userPromise.then(function () {
            var doesHasRole = $window.hasRole(scope.user.roles, roleName);
            rolesDeferred.resolve(doesHasRole);
        });

        return rolesPromise;
    };

    this.getUser = function () {
        return angular.copy(user);
    };

    this.getUserPromise = function () {
        return userPromise;
    };

    this.updateUserPromise = function (user) {

        var updateUserDeferred = $q.defer();
        var updateUserPromise = updateUserDeferred.promise;

        $http.put(getUrl("/api/1.0/users/current/"), user).success(function (data) {
            angular.merge(scope.user, data);
            updateUserDeferred.resolve(scope.user);
        });

        return updateUserPromise;
    };
}