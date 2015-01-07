define([

    'app',

    'common/directives/menu/controller'

], function(app, Controller) {
    "use strict";

    app.directive('appMenu', function() {
        return {
            restrict: 'E',
            templateUrl: '/app/common/directives/menu/template.html',
            link: function(scope, element, attrs) {},
            controller: Controller
        }
    });
});
