HotelApp.controller('HotelListController', ['$scope', '$http', function ($scope, $http) {
   
	$http({
	    method: 'GET',
	    url: 'media/data/hotels.json'
	})
	.success(function (data, status, headers, config) {	   
	    $scope.hotels = data;
	})
	
}]);
