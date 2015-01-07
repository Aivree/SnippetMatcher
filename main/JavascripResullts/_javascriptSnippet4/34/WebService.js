'use strict';

angular.module('yoApp').service('Webservice', function Webservice($http) {

	var monUrl = "http://127.0.0.1:8080/bananaSport/rest";
	
	return {
		getUserByID : function($scope,$id) {
			$http({
				method : 'GET',
				url : monUrl+"/utilisateur/"+$id
			}).success(function(data, status, headers, config) {
				console.log("Success : " + data.nom);
				$scope.user=data;
			}).error(function(data, status, headers, config) {
			    $scope.status = status;
			    console.log("Fail " + data);
			});
		},
		login : function($AuthParams,$callback) {
			$http({
				method : 'GET',
				url : monUrl+"/login/"+$AuthParams
			}).success(function(data, status, headers, config) {
				console.log("Success : " + data.id);
				$callback(data.id);
			}).error(function(data, status, headers, config) {
			    $callback(false);
			    console.log("Fail " + data);
			});
		},
		
		getUsers : function($scope) {
			$http({
				method : 'GET',
				url : monUrl+"/utilisateur"
			}).success(function(data, status, headers, config) {
				console.log("Success : " + data);
				$scope.user=data;
			}).error(function(data, status, headers, config) {
			    $scope.status = status;
			    console.log("Fail " + data);
			});
		},
		
		getSports : function($scope) {
			$http({
				method : 'GET',
				url : monUrl+"/sport"
			}).success(function(data, status, headers, config) {
				console.log("Success : " + data);
				$scope.sports=data;
				//$callback(data);
			}).error(function(data, status, headers, config) {
				//$callback(false);
			    console.log("Fail " + data);
			});
		},
		
		getSeances : function($scope){
			$http({
				method : 'GET',
				url : monUrl+"/utilisateur/1"
			}).success(function(data, status, headers, config) {
				console.log("Success : " + data.nom);
				$scope.seances=data.seances;
			}).error(function(data, status, headers, config) {
			    $scope.status = status;
			    console.log("Fail " + data);
			});
		},
		
		newUser : function($user,$callback) {
			$http({
				method : 'POST',
				url : monUrl+"/utilisateur/add",
				data : $user
			}).success(function(data, status, headers, config) {
				console.log("Success : " + data);
				$callback(data);
			}).error(function(data, status, headers, config) {
			    $callback(data);
			    console.log("Fail " + data);
			});
		},

		deleteUser : function($user,$callback) {
			$http({
				method : 'POST',
				url : monUrl+"/utilisateur/delete",
				data : $user
			}).success(function(data, status, headers, config) {
				console.log("Success : " + data);
				$callback(data);
			}).error(function(data, status, headers, config) {
			    $callback(data);
			    console.log("Fail " + data);
			});
		},

		updateUser : function($user,$callback) {
			$http({
				method : 'POST',
				url : monUrl+"/utilisateur/update",
				data : $user
			}).success(function(data, status, headers, config) {
				console.log("Success : " + data);
				$callback(data);
			}).error(function(data, status, headers, config) {
			    $callback(data);
			    console.log("Fail " + data);
			});
		},
		
		sendMessage : function($newMessage,$callback){
			$http({
				method : 'POST',
				url : monUrl+"/message",
				data : $newMessage
			}).success(function(data, status, headers, config) {
				$callback(true);
			}).error(function(data, status, headers, config) {
			    $callback(false);
			});
		},
		
		getMessages : function($callback){
			$http({
				method : 'GET',
				url : monUrl+"/message"
			}).success(function(data, status, headers, config) {
				$callback(data);
			}).error(function(data, status, headers, config) {
			    $callback(false);
			});
		},
		
		createSeance : function($callback,$data){
			$http({
				method : 'POST',
				url : monUrl+"/seance",
				data : $data
			}).success(function(data, status, headers, config) {
				$callback(data);
			}).error(function(data, status, headers, config) {
			    $callback(false);
			});
		}
	};

});
