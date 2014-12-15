'use strict';

angular.module('chatterApp')
  .service('dataService',['$http',  function($http) {
	return { 
		  deleteResource : function(url) {
				return $http({
					method : 'DELETE',
					url : url
				});
			},
			put : function(url, data) {
				return $http({
					method : 'PUT',
					url : url,
					data : data,
					headers : {
						'Content-Type' : 'application/json'
					}
				});
			},
			get : function(url) {
				return $http({
					method : 'GET',
					url : url,
					header : {
						'Accept' : 'application/json'
					}
				});
			},
			post : function(url, data) {
				return $http({
					method : 'POST',
					url : url,
					data : data,
					headers : {
						'Content-Type' : 'application/json'
					}
				});
			}
     };
  }]);
