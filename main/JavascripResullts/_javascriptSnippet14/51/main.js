
var app = angular.module('app', ['ngRoute', 'ngAnimate', 'ui.bootstrap'])

app.directive('navbar', function(){
	return {
		restrict: 'AE', 
		templateUrl: '/ng/navbar.html'
	}
})


app.directive('heading', function(){
	return {
		restrict: 'AE', 
		scope: {
			title: '@'
		},
		templateUrl: '/ng/page-header.html', 
		link: function(scope, elem, attrs){
			//scope.title = attrs.title
		}
	}
})


app.directive('quote', function(){
	return {
		restrict: 'AE', 
		scope: {
			quote: '@',
			author: '@'
		},
		templateUrl: '/ng/quote.html', 
		link: function(scope, elem, attrs){
			//scope.title = attrs.title
		}
	}
})


app.controller('NavCtrl', function(){})