'use strict';

var oa = angular.module('oa', ['ngRoute']);

//oa.config(function ($locationProvider, $routeProvider) {
//    $locationProvider.html5Mode(true);
//});

oa.controller('OaCtrl', ['$scope', '$http', '$compile', '$location', function ($scope, $http, $compile, $location) {
    $scope.inputMsg = "";
    $scope.outputMsg = "";
    $scope.baseURI = "/oa/";
    $scope.service = "SmStudy";
    $scope.actionName = "home";
    $scope.currentURI = "/ui";
    $scope.prevURI = "/ui";
    $scope.currentPage = 0;

    $scope.processURI = function (uri) {
        console.log("processingURI: " + uri);

        $scope.currentURI = uri;

        $http.get(uri, {
            headers: { "X-Oaclient": "y" }
        }).success($scope.process);


    }

    $scope.findDialog = $('<div></div>')
        .html('This dialog will show every time!')
        .dialog({
            autoOpen: false,
            title: 'Select Value...',
            modal: true,
            height: 300,
            width: 350
        });

    $scope.lov = function (uri) {
        console.log("Show LOV");
        var fullURI = $scope.baseURI +  uri
        $http({
            url: fullURI,
            method: "GET",
            headers: { 'Accept': 'text/html',"X-Oaclient" : "y"}
        }).success(function(data) {
                $scope.findDialog
                    .html(data)
                    .dialog('open');
            });

    }

    $scope.processURI1 = function (uri,data) {
        console.log("processingURI: " + uri);

        $scope.currentURI = uri;

        $http.get(uri, {
            headers: { "X-Oaclient": "y" }
        }).success($scope.process);


        $http({
            url: uri,
            method: "POST",
            data: data,
            headers: {'Content-Type': 'application/json', "X-Oaclient" : "y"}
        }).success($scope.process);
    }

    $scope.processPage = function (uri) {
        console.log("processPage: " + uri);

        $scope.currentURI = uri;

        $http.get(uri).
            success(function(data) {
                console.log("Got data back");
                var template = angular.element(data);
                var linkFn = $compile(template);
                linkFn($scope);

                $('#maincontent').empty();
                $('#maincontent').append(template);
            });
    }

    $scope.validate = function (uri) {
        console.log("validate: " + uri);
        var fullURI = $scope.baseURI + uri;
        $http.get(fullURI).
            success(function(data) {
                console.log("Validation done on server");
                console.log(data);
            });
    }

    $scope.process = function (msg) {
        var data = msg.data;
        if (data) {
            //console.log(data);
            if (msg.resultType) {
                console.log("Setting grid data");
                $scope.rows = data;
            }
            else {
                console.log("Setting fdata");
                $scope.fdata = data;
            }
        }
        if (msg.service) $scope.service = msg.service;

        if( msg.ui )
        {
            // $scope.$emit('dataLoaded',$scope.target)
            var template = angular.element(msg.ui);
            var linkFn = $compile(template);
            linkFn($scope);

            $('#maincontent').empty();
            $('#maincontent').append(template);
        }
        //console.log("Form Loaded with " + msg.ui)
    }

    $scope.$on('gotoForm', function(e, data) {
        if( data.target ) $scope.processURI(data.target);
        else $scope.process(data);
    });

    $scope.$on('gotoPage', function(e, data) {
        $scope.processPage(data.target);
    });

    $scope.$on('get', function(e, data) {
        $scope.get(data.target);
    });
    // in controller
    $scope.init = function () {
        $scope.processPage("/build");
    };

   // in controller
    $scope.post = function(uri, data){
        console.log("In MAster Post");
        $location.path(uri);
        $scope.prevURI = $scope.currentURI;
        $scope.currentURI = uri;

        //'Content-Type': 'application/json',
        //'Content-Type': 'application/x-www-form-urlencoded'

        $http({
            url: $scope.baseURI + uri,
            method: "POST",
            data: data,
            headers: { 'Content-Type': 'application/json',"X-Oaclient" : "y"}
        }).success(function (data) {
                var template = angular.element(data);
                var linkFn = $compile(template);
                linkFn($scope);

                $('#maincontent').empty();
                $('#maincontent').append(template);
            });

    };

    $scope.getTab = function (tab, uri, pageNum) {
        console.log("GET1:  " + uri);
        var appendPage = false;
        if (typeof pageNum === "undefined")
        {
            appendPage = false;
            $scope.currentPage=0;
            var fullURI = $scope.baseURI +  uri
        }
        else
        {
            appendPage = true;
            $scope.currentPage=pageNum;
            var fullURI = $scope.baseURI + uri + "/" + pageNum;
        }

        $location.path(uri);
        $scope.prevURI = $scope.currentURI;
        $scope.currentURI = uri;

        $http({
            url: fullURI,
            method: "GET",
            headers: { 'Accept': 'text/html',"X-Oaclient" : "y"}
        }).success(function(data) {
                console.log("Got data back");

                var template = angular.element(data);
                var linkFn = $compile(template);
                linkFn($scope);

                $('#'+tab).empty();
                $('#'+tab).append(template);
            });

        console.log("BackURI: " + $scope.prevURI);
    };

    $scope.get = function (uri, pageNum) {
        console.log("GET1:  " + uri);
        var appendPage = false;
        if (typeof pageNum === "undefined")
        {
            appendPage = false;
            $scope.currentPage=0;
            var fullURI = $scope.baseURI +  uri
        }
        else
        {
            appendPage = true;
            $scope.currentPage=pageNum;
            var fullURI = $scope.baseURI + uri + "/" + pageNum;
        }

        $location.path(uri);
        $scope.prevURI = $scope.currentURI;
        $scope.currentURI = uri;

        $http({
            url: fullURI,
            method: "GET",
            headers: { 'Accept': 'text/html',"X-Oaclient" : "y"}
        }).success(function(data) {
                console.log("Got data back");
                var template = "";
                if( data.fdata )
                {
                    $scope.fdata = data.fdata;
                    template = angular.element(data.ui);
                    var linkFn = $compile(template);
                    linkFn($scope);
                }
                else
                {
                    template = angular.element(data);
                    var linkFn = $compile(template);
                    linkFn($scope);
                }

                $('#maincontent').empty();
                $('#maincontent').append(template);
            });

        console.log("BackURI: " + $scope.prevURI);
    };

//    $scope.$on("$locationChangeStart", function (event, newUrl, oldUrl) {
//        $scope.startPath = $location.path();
//        $scope.startNewUrl = newUrl;
//        $scope.startOldUrl = oldUrl;
//    });
//
//    $scope.$on("$locationChangeSuccess", function (event, newUrl, oldUrl) {
//        $scope.successPath = $location.path();
//        $scope.successNewUrl = newUrl;
//        $scope.successOldUrl = oldUrl;
//        console.log("New URI: " + newUrl);
//        console.log("Old URI: " + newUrl);
//    });

    $scope.back = function () {
        $scope.get($scope.prevURI);
    };

    $scope.forward = function () {
        $window.history.forward();
    };

    //$scope.init();

    $scope.nextPage = function () {
        console.log("Gto to Next Page " + $scope.currentURI);
        $scope.get($scope.currentURI,$scope.currentPage + 1 );
    };

    $scope.prevPage = function () {
        console.log("Gto to Prev Page " + + $scope.currentURI);
        var page = $scope.currentPage;
        if( $scope.currentPage > 0 ) page = page -1;
        $scope.get($scope.currentURI,page );
    };

    $scope.goPage = function (pageNum) {
        console.log("Gto to Page " + pageNum);
    };

    $scope.action = function (uri, data) {
        console.log("actione " + uri);
        $scope.processURI1(uri,data);
    };
}]);

//oa.run(function($rootScope, $route, $location){
//    //Bind the `$locationChangeSuccess` event on the rootScope, so that we dont need to
//    //bind in induvidual controllers.
//
//    $rootScope.$on('$locationChangeSuccess', function() {
//        $rootScope.actualLocation = $location.path();
//    });
//
//    $rootScope.$watch(function () {return $location.path()}, function (newLocation, oldLocation) {
//        if($rootScope.actualLocation === newLocation) {
//            alert('Why did you use history back?');
//        }
//    });
//});

oa.controller('OaFormCtrl', ['$scope', '$http', '$compile', '$element', function ($scope, $http, $compile,$element) {
    $scope.inputMsg = "";

    $scope.toJson = function(form){
            var o = {};
            var a = form.serializeArray();
            $.each(a, function() {
                if (o[this.name]) {
                    if (!o[this.name].push) {
                        o[this.name] = [o[this.name]];
                    }
                    o[this.name].push(this.value || '');
                } else {
                    o[this.name] = this.value || '';
                }
            });
            return o;
    };

    $scope.onBlur = function (val, data) {
        console.log("In Blur  for " + val);
        console.log(data);
        console.log(encodeURIComponent(data));
    };

    $scope.submit = function (uri) {
        console.log("Submitting form");
        var data = $element.serializeJSON();
        $scope.post(uri,data);
//        console.log( data );
//        var target = "/oa/view" + $element.attr("action");
//        $http({
//            url: target,
//            method: "POST",
//            data: data,
//            headers: {'Content-Type': 'application/json', "X-Oaclient" : "y"}
//        }).success(function (data, status, headers, config) {
//                //$scope.persons = data; // assign  $scope.persons here as promise is resolved here
//                //headers: {'Content-Type': 'application/x-www-form-urlencoded', "X-Oaclient" : "y"}
//                $scope.$emit('gotoForm', data);
//            }).error(function (data, status, headers, config) {
//                $scope.status = status;
//            });
    };

    $scope.clear = function () {
        console.log("Clearing form");
    };

    $scope.nextPage = function () {
        console.log("Gto to Next Page");
    };

    $scope.prevPage = function () {
        console.log("Gto to Prev Page");
    };

    $scope.goPage = function (pageNum) {
        console.log("Gto to Page " + pageNum);
    };

    $scope.action = function (uri, data) {
        console.log("actione " + uri);
        $scope.processURI1(uri,data);
    };

    $scope.cancel = function() {
        $scope.back();
    };

}]);

oa.controller('OaMenuCtrl', ['$scope', '$http', '$compile', '$element', function ($scope, $http, $compile,$element) {
    $scope.inputMsg = "";
    $scope.currentMenu="";

    $scope.menu1 = function () {
        console.log("Submitting form");
        var data = $element.serialize();
        console.log( $scope.fdata );
        var target = "/oa/view" + $element.attr("action");
        $http({
            url: target,
            method: "POST",
            data: $scope.fdata,
            headers: {'Content-Type': 'application/json', "X-Oaclient" : "y"}
        }).success(function (data, status, headers, config) {
                //$scope.persons = data; // assign  $scope.persons here as promise is resolved here
                //headers: {'Content-Type': 'application/x-www-form-urlencoded', "X-Oaclient" : "y"}
                $scope.$emit('gotoForm', data);
            }).error(function (data, status, headers, config) {
                $scope.status = status;
            });
    };

    $scope.menu = function (code, action) {
        console.log("menu click form " + code );
        $scope.currentMenu = code;
        var data = {};
        data.target =  action;
        $scope.$emit('get', data);
    };

    $scope.action = function (uri, data) {
        console.log("actione " + uri);
        $scope.processURI1(uri,data);
    };
}]);

oa.directive('oaValidate', ['$http', function($http) {
    return {
        require: 'ngModel',
        link: function(scope, ele, attrs, c) {

            // add a parser that will process each time the value is
            // parsed into the model when the user updates it.
            ctrl.$parsers.unshift(function(value) {
                // test and set the validity after update.
                var valid = regex.test(value);
                ctrl.$setValidity('regexValidate', valid);

                // if it's valid, return the value to the model,
                // otherwise return undefined.
                return valid ? value : undefined;
            });

            scope.$watch(attrs.ngModel, function() {
                $http({
                    method: 'POST',
                    url: '/oa/' + attrs.oaValidate + '/'+ attrs.ngModel,
                    data: {'field': attrs.ensureUnique}
                }).success(function(data, status, headers, cfg) {
                        console.log("Validation from server");
                        console.log(data);
                        c.$setValidity('oaValidate', data.valid);
                    }).error(function(data, status, headers, cfg) {
                        c.$setValidity('oaValidate', false);
                    });
            });
        }
    }
}]);

oa.directive('oaBlur', ['$parse', function($parse) {
    return function(scope, element, attr) {
        var fn = $parse(attr['oaBlur']);
        element.bind('blur', function(event) {
            scope.$apply(function() {
                fn(scope, {$event:event});
            });
        });
    }
}]);

