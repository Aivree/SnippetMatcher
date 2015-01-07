'use strict';

app.directive('tableDrivers', [function () {
    return {
        scope: false,
        transclude: true,
        templateUrl: 'views/directives/dataTableDriversTemplate.html',
        controller: function ($scope, $element, $attrs) {

        },
        replace: true
    };
}]);