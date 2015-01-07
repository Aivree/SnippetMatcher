'use strict';

app.directive('tableTrips', [function () {
    return {
        scope: false,
        transclude: true,
        templateUrl: 'views/directives/dataTableTripsTemplate.html',
        controller: function ($scope, $element, $attrs) {

        },
        replace: true
    };
}]);