angular.
    module('__MODULE_ID__').
    directive('__DIRECTIVE__', function () {
        'use strict';
        var directive;

        function link($scope, $element, $attributes) {

        }

        directive = {
            link: link,
            replace: true,
            scope: {

            },
            templateUrl: 'components/__MODULE_FILE__/__MODULE_FILE__-__FILE__-directive.html'
        };
        return directive;
    });
