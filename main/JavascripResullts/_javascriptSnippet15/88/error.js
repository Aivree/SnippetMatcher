/**
 * Error directive module.
 * 
 * @author imoiseyenko93@gmail.com
 */
define(["angular"], function (angular) {

	var errorModule = angular.module("errorModule", []);

	errorModule.directive("error", function () {

		return {

			restrict: "E",
			templateUrl: "resources/js/app/directive/error/error.html",
			scope: {

				errors: "=errors"
			}
		};
	});

	return errorModule;
});
