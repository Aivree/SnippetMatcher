app.factory('directoryService', ['$http', 'sessionService', function ($http, Session) {

    var subService = 'DirectoryService/';

    return {
        findUser: function (pattern) {
            return $http.get(hostBase + subService + 'FindUser' + pattern); //contains string
        },

        authenticate: function(credentials) {
            //return $http.post(hostBase + subService + 'Authenticate?username=' + credentials.username + '&password=' + credentials.password).then(function (res) {
            //    console.log(res);
            //    Session.create(res.id, res.userid, res.role);
            //});

            $http({
                method: 'POST',
                url: hostBase + subService + 'Authenticate',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                transformRequest: function(obj) {
                    var str = [];

                    for (var p in obj) {
                        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                    }


                    return str.join("&");
                },
                data: { __RequestVerificationToken: CryptoJS.SHA512(credentials.verificationToken), UserName: credentials.username, Password: credentials.password }
            }).then(function(res) {
            });
        },

        isAuthenticated: function() {
            return !!Session.userId;
        },

        isAuthorized: function(authorizedRoles) {
            if (!angular.isArray(authorizedRoles)) {
                authorizedRoles = [authorizedRoles];
            }

            return (this.isAuthenticated() && authorizedRoles.indexOf(Session.userRole) !== -1);
        }
    }
}]);