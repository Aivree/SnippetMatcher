'use strict';

angular.module('desktopApp')
  .controller('BitcoinTickerCtrl', function ($scope, $http) {

  	$http({method: 'GET', url: 'http://localhost:3000/api/dayhigh'}).
  	success(function(data, status, headers, config) { 
  	$scope.dayHigh = data;
  });
  	$http({method: 'GET', url: 'http://localhost:3000/api/daylow'}).
	success(function(data, status, headers, config) { 
  	$scope.dayLow = data;
  });
	$http({method: 'GET', url: 'http://localhost:3000/api/last'}).
	success(function(data, status, headers, config) { 
  	$scope.lastPrice = data;
  });
	$http({method: 'GET', url: 'http://localhost:3000/api/bid'}).
	success(function(data, status, headers, config) { 
  	$scope.askPrice = data;
  });
	$http({method: 'GET', url: 'http://localhost:3000/api/ask'}).
	success(function(data, status, headers, config) { 
  	$scope.bidPrice = data;
  });

});