/* jshint -W089 */

var forgotCtrl = function ($scope, $location, $http, configService, usernameService, $timeout, $anchorScroll) {

    $scope.processForm = function(isValid){
        if (isValid){
            $http({
                method  : 'POST',
                url     : configService.API_END_POINT+'auth/forgot-password',
                data: {'email': $scope.email},
                transformRequest: function(obj) {
                    var str = [];
                    for(var p in obj) {
                        str.push(encodeURIComponent(p) + '=' + encodeURIComponent(obj[p]));
                    }
                    return str.join('&');
                },
                headers : { 'Content-Type': 'application/x-www-form-urlencoded' }  // set the headers so angular passing info as form data (not request payload)
            })
            .success(function(data) {
                //$location.path('/login');
                $scope.forgotSuccessMessage = data.status;
            });
        
        } else{
            $scope.submittedError = true;
            $anchorScroll('top');
        }
    };
};

forgotCtrl.$inject = ['$scope', '$location', '$http', 'configService', 'usernameService', '$timeout', '$anchorScroll'];
