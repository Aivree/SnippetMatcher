angular.module('myApp', [])
	.controller('GenderCtrl', function($scope) {

	  $scope.init = function(gens) {
	  	$scope.gen_options = gens;
	  	$scope.selected_gen_option = $scope.gen_options[0];
	  };

	});

