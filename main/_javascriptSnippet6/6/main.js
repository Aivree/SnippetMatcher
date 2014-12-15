(function () {

    /* use strict */

    var app = angular.module('attendance', ['ui.bootstrap', 'checklist-model']);

    app.factory('httpRequestInterceptor', function () {
        return {
            request: function (config) {
                if (config.url.indexOf('/token') < 0)
                {
                    // only add the auth header if we're not currently authenticating
                    config.headers['Authorization'] = 'Bearer ' + sessionStorage.getItem(tokenKey);
                }
                return config;
            }
        };
    });

    app.config(function ($httpProvider) {
        $httpProvider.interceptors.push('httpRequestInterceptor');
    });

    //#region TODO Set these as a app wide constant or service somewhere
    var baseUrl = "http://attendance1-api.azurewebsites.net";
    var apiBaseUrl = baseUrl + "/api";
    var tokenKey = "accessTokenKey";
    if (window.location.href.indexOf('localhost') >= 0) {
        var apiBaseUrl = 'http://localhost/attendance.webapi/api'
    }
    //#endregion

    app.controller('VisitCtrl', ['$scope', '$http', '$rootScope', function ($scope, $http, $rootScope) {

        var visitApiUrl = apiBaseUrl + "/visit";

        // child controls will populate these 
        // do we need to instantiate them here?
        $scope.persons = {};
        $scope.events = {};
        $scope.selectedEvent = {};
        $scope.selectedPersons = {};
        $scope.visitDateTime = {};

        $scope.postVisits = function () {

            var visits,
                selectedEventId,
                selectedDate,
                selectedDateAtNoon,
                iso8601Date,
                selectedDateTime;

            visits = [];
            selectedEventId = $scope.selectedEvent.Id;

            selectedDateAtNoon = new Date($scope.visitDateTime);
            selectedDateAtNoon.setHours(0, 0, 0, 0); // midnight on the morning of the selected date
            selectedDateAtNoonInIso8601Format = selectedDateAtNoon.toJSON();

            console.log(selectedDateAtNoonInIso8601Format);

            // iterate persons and add selected ones
            angular.forEach($scope.selectedPersons, function (value, key) {
                var visit = {
                    PersonId: value.Id,
                    EventId: selectedEventId,
                    DateTime: selectedDateAtNoonInIso8601Format
                };
                visits.push(visit);
            });

            console.table(visits);

            $http.post(visitApiUrl, visits)
                .success(function (data, status, headers, config) {
                    console.log('success');
                    $rootScope.$broadcast('postVisitsComplete', null);

                })
                .error(function (data, status, headers, config) {
                    console.table(data);
                });

        };

    }]);

    app.controller('DatepickerCtrl', ['$scope', function ($scope) {

        // set dt as today's date
        $scope.today = function () {
            $scope.$parent.visitDateTime = new Date();
        };
        $scope.today();
        $scope.format = 'dd-MMMM-yyyy';

    }]);

    app.controller('PersonCtrl', ['$scope', '$http', function ($scope, $http) {

        var personApiUrl = apiBaseUrl + "/person";

        $scope.$parent.selectedPersons = [];

        function initNewPerson() {
            $scope.newPerson = { First: '', Last: '', Id: null };
        }
        initNewPerson();

        $scope.postPerson = function (person) {
            var newPerson = {
                FirstName: person.First,
                LastName: person.Last,
            };

            $http.post(personApiUrl, newPerson)
                .success(function (data, status, headers, config) {
                    $scope.$parent.persons.push(data);
                    $scope.$parent.selectedPersons.push(data);
                    initNewPerson();
                })
                .error(function (data, status, headers, config) { });
        };

        $scope.putPerson = function (person) {
            $http.put(personApiUrl, person)
                .success(function (data, status, headers, config) {
                    person.edit = false;
                })
                .error(function (data, status, headers, config) {
                    console.log('error');
                });
        }

        $scope.deletePerson = function (person) {
            $http.delete(personApiUrl + '/' + person.Id)
                .success(function (data, status, headers, config) {
                    var index = $scope.$parent.persons.indexOf(person);
                    $scope.$parent.persons.splice(index, 1);
                })
                .error(function (data, status, headers, config) {
                    console.log('error');
                });
        }

        $scope.getPersons = function () {
            $http.get(personApiUrl)
                .success(function (data, status, headers, config) {

                    angular.forEach(data, function (value, key) {
                        value.Selected = false;
                    });

                    $scope.$parent.persons = data;
                })
                .error(function (data, status, headers, config) {
                    if (status == 401) {
                        console.log('unauthorized');
                    }
                });
        };

        $scope.getPersons();


    }]);

    app.controller('EventCtrl', ['$scope', '$http', function ($scope, $http) {

        var eventApiUrl = apiBaseUrl + "/event";

        function setSelectedEvent(index) {
            $scope.$parent.selectedEvent = $scope.$parent.events[index];
        }

        function initNewEvent() {
            $scope.newEvent = { Name: '' };
        }
        initNewEvent();

        $scope.postEvent = function () {
            var newEvent = {
                Name: $scope.newEvent.Name
            };

            $http.post(eventApiUrl, newEvent)
                .success(function (data, status, headers, config) {
                    $scope.$parent.events.push(data);
                    setSelectedEvent($scope.events.length - 1);
                    initNewEvent();
                })
                .error(function (data, status, headers, config) { });
        };

        $scope.putEvent = function (event) {
            $http.put(eventApiUrl, event)
                .success(function (data, status, headers, config) {
                    event.edit = false;
                })
                .error(function (data, status, headers, config) {
                    console.log('error');
                });
        }

        $scope.deleteEvent = function (event) {
            $http.delete(eventApiUrl + '/' + event.Id)
                .success(function (data, status, headers, config) {
                    var index = $scope.$parent.events.indexOf(event);
                    $scope.$parent.events.splice(index, 1);
                })
                .error(function (data, status, headers, config) {
                    console.log('error');
                });
        }

        $http.get(eventApiUrl)
            .success(function (data, status, headers, config) {
                $scope.$parent.events = data;
                setSelectedEvent(0);
                console.log('success');
            })
            .error(function (data, status, headers, config) {
                console.log('error');
            });
    }]);

    app.controller('StatisticsCtrl', ['$scope', '$http', function ($scope, $http) {

        var statisticsApiUrl = apiBaseUrl + "/statistics";

        $scope.visits = {};

        $scope.monthNames = [
            'January',
            'February',
            'March',
            'April',
            'May',
            'June',
            'July',
            'August',
            'September',
            'October',
            'November',
            'December'
        ];

        // get the total number of visits for each event over its lifetime
        function getVisitsSinceInception() {

            $http.get(statisticsApiUrl + '/visits/inception')
                .success(function (data, status, headers, config) {
                    $scope.visits.all = data;
                    console.log('success');
                })
                .error(function (data, status, headers, config) {
                    console.log('error');
                });

        }

        function getVisitsByMonth() {
            $http.get(statisticsApiUrl + '/visits/month')
                .success(function (data, status, headers, config) {
                    $scope.visits.month = data;
                    console.log('success');
                })
                .error(function (data, status, headers, config) {
                    console.log('error');
                });
        }

        function getVisitsComprehensive() {
            $http.get(statisticsApiUrl + '/visits/comprehensive')
                .success(function (data, status, headers, config) {
                    $scope.visits.comprehensive = data;
                    console.log('success');
                })
                .error(function (data, status, headers, config) {
                    console.log('error');
                });
        }

        $scope.$on("postVisitsComplete", function (event, args) {

            ////getVisitsSinceInception();
            ////getVisitsByMonth();
            getVisitsComprehensive();

        });

        // TODO: consider making these one network call not two
        ////getVisitsSinceInception();
        ////getVisitsByMonth();
        getVisitsComprehensive();

    }]);

    app.controller('AuthCtrl', ['$scope', '$http', function ($scope, $http) {

        $scope.accessTokenRequestData = {
            grant_type: 'password',
            username: '',
            password: ''
        };

        $scope.requestAccessToken = function (requestData) {

            var config = {
                method: 'post',
                url: baseUrl + '/token',
                data: requestData,
                transformRequest: function (obj) {
                    // transform data to form-url-encoded
                    var str = [];
                    for (var p in obj)
                        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                    return str.join("&");
                },
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                }
            };

            $http(config)
                .success(function (data, status, headers, config) {
                    console.log('success');
                    sessionStorage.setItem(tokenKey, data.access_token);
                })
                .error(function (data, status, headers, config) {
                    console.log('error');
                });

        };

        $scope.forgetAccessToken = function ()
        {
            sessionStorage.removeItem(tokenKey);
        }

    }]);

}());
