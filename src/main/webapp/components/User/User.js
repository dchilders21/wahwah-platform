function ImpersonateDialogController($document){

    var ctrl = this;

    ctrl.userTypes = {
        "RED_PANDA_USER":"Internal",
        "MARKETPLACE_PUBLISHER_USER":"Marketplace",
        "FREE_USER":"Free",
        "NETWORK_USER":"Network",
        "NETWORK_PUBLISHER_USER":"Network Publisher",
        "REPORTING_PRO_USER":"Reporting Pro"
    };

    ctrl.selectUser = function(user){

        var doc = $document[0];

        var form = doc.createElement("form");
        form.method = "POST";
        form.action = getUrl("/useraccount/impersonate");

        var field = doc.createElement("input");
        field.name = "ImpersonateUserId";
        field.value = user.userId;

        form.appendChild(field);
        doc.body.appendChild(form);

        form.submit();
    };

    ctrl.accountNameFunc = function(user) {
        var str = user.accountName;
        if (str == "")
        {
            return 1;
        }
        return 0;
    };
}

function UserMenuController($user,$http){
    var ctrl = this;

    ctrl.logoUrl = getUrl('/content/platform/images/redPanda_20x20.png');
    ctrl.appVersion = angular.element('body').data('version');

    ctrl.showImpersonationMenu = false;
    ctrl.isImpersonationDialogVisible = false;

    ctrl.$onInit = function(){
        $http.get(getUrl("/api/1.0/users/current/?use-real-user=true")).success(function (data)
        {
            ctrl.realUserObj = data;
            ctrl.enabledImpersonation = false;

            for (var i = 0; i < ctrl.realUserObj.roles.length; i++)
            {
                if (ctrl.realUserObj.roles[i] == "SUPER_USER")
                    ctrl.enabledImpersonation = true;
            }

            $user.getUserPromise().then(function (data)
            {
                ctrl.selectedImpersonationUser = data;
                ctrl.setImpersonationUserUI();
            });

            $http.get(getUrl("/api/1.0/users/?page=0&size=100000&use-real-user=true")).success(function (data)
            {
                ctrl.impersonationUsers = [];

                for (var i = 0; i < data.users.length; i++)
                {
                    // place internal users at end
                    if (data.users[i].activated == true && data.users[i].loginEnabled == true) {

                        if(data.users[i].accountName == null) {
                            data.users[i].accountName = ""; // make it ready for emptyToEnd filter
                        }
                        ctrl.impersonationUsers.push(data.users[i]);
                    }
                }

                if(ctrl.impersonationUsers.length > 0){
                    ctrl.showImpersonationMenu = true;
                }
            });
        });
    };

    ctrl.setImpersonationUserUI = function ()
    {
        ctrl.isImpersonated = false;

        if (ctrl.selectedImpersonationUser.emailAddress == ctrl.realUserObj.emailAddress){
            ctrl.impersonationUser = "";
        }  else { // Impersonated
            ctrl.isImpersonated = true;
            ctrl.impersonationUser = ctrl.selectedImpersonationUser.firstName + " " + ctrl.selectedImpersonationUser.lastName;
        }

        ctrl.realUser = ctrl.realUserObj.firstName + " " + ctrl.realUserObj.lastName;
    };

    ctrl.showImpersonationDialog = function(){
        ctrl.isImpersonationDialogVisible = true;
    };

    ctrl.hideImpersonationDialog = function(){
        ctrl.isImpersonationDialogVisible = false;
    };
}

angular.module('wahwah.components').component('impersonationDialog', {
    templateUrl: getUrl('/components/User/impersonate-user-dialog.html'),
    controller: ImpersonateDialogController,
    bindings: {
        realUserObj: "=",
        impersonationUsers: "=",
        selectedImpersonationUser: "=",
        isImpersonated: "=",
        impersonationUser: "=",
        visible: "<",
        onClose: "&"
    }
});

angular.module('wahwah.components').component('userMenu', {
    templateUrl: getUrl('/components/User/user-menu.html'),
    controller: UserMenuController
});