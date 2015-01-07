'use strict';

project.directive('terminal', function () {
    return {
        restrict: 'E',
        scope: true,
        templateUrl: 'app/tpl/partials/terminal.html',
        controller: function ($scope, $http,ngProgress) {
            $scope.commande = '';
            $scope.result = null;
            $scope.submit = function () {
                var data = {
                    'commande': $scope.commande
                };

                ngProgress.reset();
                ngProgress.start();
                $http({
                    url: 'app/script/terminal.php',
                    method: "POST",
                    data: data,
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                    },
                    transformRequest: function (obj) {
                        var str = [];
                        for (var p in obj)
                            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                        return str.join("&");
                    }
                }).success(function (data) {
                        $scope.result = data;
                        ngProgress.complete();
                    });

            }
        }
    }
});
