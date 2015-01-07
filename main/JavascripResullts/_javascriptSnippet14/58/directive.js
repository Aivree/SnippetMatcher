define([

    'app',

    'kanban/directives/editData/controller'

], function(app, Controller) {
    "use strict";

    app.directive('appEditData', function() {
        return {
            restrict: 'E',
            templateUrl: '/app/kanban/directives/editData/template.html',
            link: function(scope, element, attrs) {},
            controller: Controller
        }
    });
});
