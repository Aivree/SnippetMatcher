/*jslint node: true */
/*global angular */
"use strict";

angular.module("moviesApp.directives", []).directive("movie", function() {
    var directive = { };
    directive.restrict = 'AE';

    directive.scope = {
        score: '=score',
        max: '=max'
    };

    directive.templateUrl = "app/templates/rating.html";


    return directive;
});