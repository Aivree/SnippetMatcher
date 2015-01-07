'use strict';

angular.module('sylwia').directive('project', function() {
	return {
		scope: {
			project: '=project'
		},
		templateUrl: 'views/projectDirective.html'
	};
});