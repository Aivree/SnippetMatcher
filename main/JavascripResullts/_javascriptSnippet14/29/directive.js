define([

    'app'

], function(app) {
    "use strict";

    app.directive('appFooter', function() {
        return {
            restrict: 'E',
            templateUrl: '/app/common/directives/footer/template.html',
            link: function(scope, element, attrs) {},
            controller: function() {}
        }
    });
});
