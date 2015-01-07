'use strict';

angular.module('myApp.controllers.AppController', ['ngResource'])
    .controller('AppController', function ($scope, AppPropertiesService) {
		init();
		function init() {
			AppPropertiesService.get(function(data){
				$scope.props = {
						version: data.version,
						appName: data.appName
				};
			});			
		};
    });