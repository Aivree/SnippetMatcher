(function() {
    'use strict';

    angular
        .module('app.common')
        .directive('shadowbox', shadowbox);

    /* @ngInject */
    function shadowbox () {
        // Usage:
        //
        // Creates:
        //
        var directive = {
            link: link,
            restrict: 'A',
			transclude: true,
			templateUrl: 'app/layout/partials/shadowbox.html',
			scope: {
				videoUrl: '@url',
				videoName: '@id'
			},
			controller: 'ShadowboxController'
        };
        return directive;

        function link(scope, element, attrs) {
        	scope.videoID = attrs.id;
			scope.frameSource = attrs.href;
			$(element).click(function(event) {
       			event.preventDefault();
    		});
        }
    }
})();