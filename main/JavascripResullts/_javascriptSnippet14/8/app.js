var app = angular.module('plunker', []);
app.directive('hello', function () {
    return {
        restrict: 'E',
        templateUrl: function (tElement, tAttrs) {
            return tAttrs.templateUrl ? tAttrs.templateUrl : 'hello.html';
        }
    };
});

app.controller('MainCtrl', function ($scope) {
    $scope.name = 'World';
});