(function() {
    'use strict';

    angular.module('<%= moduleName %>')
        .directive('<%= directiveName %>', ['dependency',
            function <%= directiveName %>(dependency) {
                return {
                    restrict: 'A',
                    scope: {},
                    link: function(scope, element, attrs, controllers) {

                    },
                    templateUrl: 'template.html',
                };
            }
        ]);
}());
