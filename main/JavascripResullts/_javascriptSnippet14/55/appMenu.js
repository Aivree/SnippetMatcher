angular.module('core')
	.directive('core.appMenu', [function() {
		
		
		return {
			replace: true,
			scope: {
				menuItems: '=',
				title: '@'
			},
			templateUrl: 'core/appMenu/appMenu.tpl.html',
			controller: ['$scope', function( $scope ) {
				
			}],
			link: function( scope, element, attrs) {
				
			}
		};
		
		
		
	}]
);