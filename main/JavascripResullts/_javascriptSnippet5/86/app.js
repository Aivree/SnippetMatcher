var myApp = angular.module('myApp', ['ngRoute']);

myApp.config(function ($routeProvider, $locationProvider) {

    $routeProvider
        .when('/Home/', {
            templateUrl: '/Templates/customer.html',
            controller: 'customerCtrl'
        })
        .otherwise({
            redirectTo: '/Home/'
        });

    $locationProvider.html5Mode(true);

});

myApp.controller('customerCtrl', function ($scope, customerSvc) {

    $scope.customer = {
        firstName: 'John',
        lastName: 'Doe'
    };

    var antiForgeryToken = angular.element(document.querySelector('[name=__RequestVerificationToken]'));

    $scope.save1 = function (customer) {
        customerSvc.save1(customer, antiForgeryToken);
    };

    $scope.save2 = function (customer) {
        customerSvc.save2(customer, antiForgeryToken);
    };

});

myApp.factory('customerSvc', function ($http, transformRequestAsFormPost) {
    return {
        save1: function (customer, antiForgeryToken) {

            // CustomerVm
            $http({
                method: 'POST',
                url: '/Home/Save1',
                data: customer
            });
        },
        save2: function (customer, antiForgeryToken) {

            // FormCollection
            $http({
                method: 'POST',
                url: '/Home/Save2',
                transformRequest: transformRequestAsFormPost,
                data: { __RequestVerificationToken: antiForgeryToken, jsonRequest: JSON.stringify(customer || {}) }
            });
        }
    };
});

myApp.factory('transformRequestAsFormPost', function () {

        // I prepare the request data for the form post.
        function transformRequest(data, getHeaders) {

            var headers = getHeaders();

            headers["Content-Type"] = "application/x-www-form-urlencoded; charset=utf-8";

            var serData = serializeData(data);

            return serData;

        }


        // Return the factory value.
        return (transformRequest);


        // ---
        // PRVIATE METHODS.
        // ---


        // I serialize the given Object into a key-value pair string. This
        // method expects an object and will default to the toString() method.
        // --
        // NOTE: This is an atered version of the jQuery.param() method which
        // will serialize a data collection for Form posting.
        // --
        // https://github.com/jquery/jquery/blob/master/src/serialize.js#L45
        function serializeData(data) {

            // If this is not an object, defer to native stringification.
            if (!angular.isObject(data)) {

                return ((data == null) ? "" : data.toString());

            }

            var buffer = [];

            // Serialize each key in the object.
            for (var name in data) {

                if (!data.hasOwnProperty(name)) {

                    continue;

                }

                var value = data[name];

                buffer.push(
                    encodeURIComponent(name) +
                    "=" +
                    encodeURIComponent((value == null) ? "" : value)
                );

            }

            // Serialize the buffer and clean it up for transportation.
            var source = buffer
                .join("&")
                .replace(/%20/g, "+")
            ;

            return (source);
        }
    }
);