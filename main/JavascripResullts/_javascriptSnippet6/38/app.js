var base_domain = location.protocol + "//" + location.hostname + (location.port && ":" + location.port) + "/";
var api_domain = location.protocol + "//" + location.hostname + (location.port && ":" + location.port) + "/" + "api/";
var api_format = "json";
var adminPrivilege = "Admin";
var studentPrivilege = "Student";
var coursexml;
var studentxml;
var selectedcourselectureid = 0;
var selectedcoursetutorialid = 0;
var selectedcourselabid = 0;
var takendivids = [];
var clashcode;
var day_array = new Array("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
var tablerows = ['22302245', '22152230', '22002215', '21452200', '21302145', '21152130', '21002115', '20452100', '20302045', '20152030', '20002015', '19452000', '19301945', '19151930', '19001915', '18451900', '18301845', '18151830', '18001815', '17451800', '17301745', '17151730', '17001715', '16451700', '16301645', '16151630', '16001615', '15451600', '15301545', '15151530', '15001515', '14451500', '14301445', '14151430', '14001415', '13451400', '13301345', '13151330', '13001315', '12451300', '12301245', '12151230', '12001215', '11451200', '11301145', '11151130', '11001115', '10451100', '10301045', '10151030', '10001015', '09451000', '09300945', '09150930', '09000915'];
var ResistorApp = angular.module("ResistorApp", ['ngRoute', 'angularFileUpload']);

ResistorApp.run(['$rootScope', 'resistorFactory', function($rootScope, resistorFactory) {
        $rootScope.user = [];
        resistorFactory.updateMenu();
        $rootScope.$on('$routeChangeSuccess', function() {
            $(".overlay").css("display", "none");
            takendivids = [];
            coursexml = null;
            studentxml = null;
        });
    }]);

ResistorApp.config(['$httpProvider', '$locationProvider', function($httpProvider, $locationProvider) {
        $locationProvider.html5Mode(true);
// Use x-www-form-urlencoded Content-Type
        $httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=utf-8';
// Override $http service's default transformRequest
        $httpProvider.interceptors.push('$q', '$rootScope', '$location', function($q, $rootScope, $location) {
            return {
// optional method
                'request': function(config) {
                    config.headers['Timestamp'] = Math.ceil(new Date().getTime() / 1000);
                    return config || $q.when(config);
                },
// optional method
                'requestError': function(rejection) {
                    $rootScope.heading = "Unexpected Error.";
                    $rootScope.message = "An unexpected error has occurred. Please, try again later.";
                    $location.url('/error');
                    return $q.reject(rejection);
                },
// optional method
                'response': function(response) {
                    if (response.data.authtoken !== undefined) {
                        $httpProvider.defaults.headers.common['Authtoken'] = response.data.authtoken;
                        response.data.authtoken = null;
                    }
                    if (response.data.user !== undefined && response.data.user.privilege !== undefined) {
                        $httpProvider.defaults.headers.common['Privilege'] = response.data.user.privilege;
                    }
                    return response || $q.when(response);
                }, // optional method
                'responseError': function(rejection) {
                    console.log(rejection);
// do something on error
                    switch (rejection.status) {
                        case 401:
                            $rootScope.message = "You need to login to continue.";
                            if ($location.path() !== "/") {
                                $rootScope.redirecturl = $location.path();
                            }
                            $rootScope.user = [];
                            $location.url('/');
                            break;
                        case 422:// validation errors
                            break;
                        case 404:
                            $location.url('/notfound');
                            break;
                        default:
                            $rootScope.heading = "Unexpected Error.";
                            $rootScope.message = "An unexpected error has occurred. Please, try again later.";
                            $location.url('/error');
                            break;
                    }
                    return $q.reject(rejection);
                }
            };
        });
        /**
         * The workhorse; converts an object to x-www-form-urlencoded
         * serialization.
         * 
         * @param {Object}
         *            obj
         * @return {String}
         */
        var param = function(obj) {
            var query = '', name, value, fullSubName, subName, subValue, innerObj, i;
            for (name in obj) {
                value = obj[name];
                if (value instanceof Array) {
                    for (i = 0; i < value.length; ++i) {
                        subValue = value[i];
                        fullSubName = name + '[' + i + ']';
                        innerObj = {};
                        innerObj[fullSubName] = subValue;
                        query += param(innerObj) + '&';
                    }
                }
                else if (value instanceof Object) {
                    for (subName in value) {
                        subValue = value[subName];
                        fullSubName = name + '[' + subName + ']';
                        innerObj = {};
                        innerObj[fullSubName] = subValue;
                        query += param(innerObj) + '&';
                    }
                }
                else if (value !== undefined && value !== null)
                    query += encodeURIComponent(name) + '=' + encodeURIComponent(value) + '&';
            }

            return query.length ? query.substr(0, query.length - 1) : query;
        };
// Override $http service's default transformRequest
        $httpProvider.defaults.transformRequest = [function(data) {
                return angular.isObject(data) && String(data) !== '[object File]' ? param(data) : data;
            }];
    }]);

ResistorApp.config(['$routeProvider', function($routeProvider) {
        $routeProvider
                .when('/', {controller: 'loginCtrl', templateUrl: 'partials/login.html'})
                .when('/logout', {controller: 'logoutCtrl', templateUrl: 'partials/empty.html'})
                .when('/error', {controller: 'errorCtrl', templateUrl: 'partials/error.html'})
                .when('/notfound', {controller: 'notFoundCtrl', templateUrl: 'partials/error.html'})
                .when('/about', {controller: 'aboutCtrl', templateUrl: 'partials/about.html'})
                .when('/faq', {controller: 'faqCtrl', templateUrl: 'partials/faq.html'})
                .when('/student', {controller: 'studentCtrl', templateUrl: 'partials/student.html'})
                .when('/scheduler/manual', {controller: 'manualSchedulerCtrl', templateUrl: 'partials/manualscheduler.html'})
                .when('/scheduler/automatic', {controller: 'automaticSchedulerCtrl', templateUrl: 'partials/automaticscheduler.html'})
                .when('/scheduler/saved', {controller: 'savedScheduleCtrl', templateUrl: 'partials/savedschedule.html'})
                .when('/admin', {controller: 'adminCtrl', templateUrl: 'partials/upload.html'})
                .otherwise({redirectTo: '/notfound'});
    }]);

ResistorApp.controller('aboutCtrl', function() {

});

ResistorApp.controller('faqCtrl', function() {

});
function isEmpty(obj) {
    return Object.keys(obj).length === 0;
}

ResistorApp.controller('automaticSchedulerCtrl', ['$rootScope', '$scope', '$http', 'resistorFactory', function($rootScope, $scope, $http, resistorFactory) {
        if (resistorFactory.validLoggedIn(studentPrivilege)) {
            resistorFactory.getStudentRecord();
            $scope.semester = "None";
            $scope.semestermessage = "Please, select a semester";
            $scope.timeofday = "";
            $scope.noofcourses = 1;
            $scope.accepteddivs = [];
            $scope.excludeday = 0;
            $scope.addedschedules = {};
            $scope.magicschedule = [];
            $scope.$watch('timeofday', function() {
                $scope.accepteddivs = [];
                if ($scope.timeofday === "day" || $scope.timeofday === "evening") {
                    var starttime;
                    var endtime;
                    if ($scope.timeofday === "day") {
                        starttime = "08:45";
                        endtime = "16:00";
                    }
                    if ($scope.timeofday === "evening") {
                        starttime = "16:00";
                        endtime = "23:00";
                    }
                    var nextdate = starttime;
                    while (nextdate !== endtime) {
                        for (var i = 1; i < 6; i++) {
                            $scope.accepteddivs.push(getClassFromTime(i, nextdate));
                        }
                        nextdate = getNextDate(nextdate);
                    }
                }
            });
            $scope.saveSchedule = function() {
                if (isEmpty($scope.addedschedules)) {
                    return opendialog("No schedule is detected.");
                }
                $http({
                    url: api_domain + "students/" + $rootScope.user.netname + "/schedules",
                    method: "POST",
                    data: {schedulejson: angular.toJson($scope.addedschedules), semester: $scope.semester}
                }).success(function(data) {
                    $scope.editSchedule();
                    $scope.addedschedules = {};
                    takendivids = [];
                    opendialog("Schedule was successfully saved.");
                }).error(function(data) {
                    if (data.errors !== undefined) {
                        return opendialog("Schedule could not be added because : " + data.errors.schedule);
                    }
                });
            };
            $scope.notSatisfied = function() {
                takendivids = [];
                $("#scheduler" + 0).remove();
                for (var i in $scope.addedschedules) {
                    for (var j  in  $scope.addedschedules[i].sections) {
                        removeoverlay(0, $scope.addedschedules[i].sections[j]);
                    }
                    if ($scope.addedschedules[i].tutorial !== undefined) {
                        removeoverlay(0, $scope.addedschedules[i].tutorial);
                    }
                    if ($scope.addedschedules[i].lab !== undefined) {
                        removeoverlay(0, $scope.addedschedules[i].lab);
                    }
                }
                $(".overlay").css("display", "none");
                $scope.addedschedules = {};
                $scope.performMagic();
            };
            $scope.editSchedule = function() {
                $('#schedulerdiv').slideUp();
                $("#selectcourse").slideDown();
                for (var i in $scope.addedschedules) {
                    for (var j  in  $scope.addedschedules[i].sections) {
                        removeoverlay(0, $scope.addedschedules[i].sections[j]);
                    }
                    if ($scope.addedschedules[i].tutorial !== undefined) {
                        removeoverlay(0, $scope.addedschedules[i].tutorial);
                    }
                    if ($scope.addedschedules[i].lab !== undefined) {
                        removeoverlay(0, $scope.addedschedules[i].lab);
                    }
                }
                $(".overlay").css("display", "none");
                $("#scheduler" + 0).remove();
            };
            $scope.generateSchedule = function() {
                if (!resistorFactory.isValidSemester($scope.semester)) {
                    return opendialog("You need to select a semester and add at least a course.");
                }
                if ($scope.timeofday !== "day" && $scope.timeofday !== "evening") {
                    return opendialog("You need to select daily timing for your preferences.");
                }
                if (Number($scope.noofcourses) < 1 || Number($scope.noofcourses) > 5) {
                    return opendialog("Number of courses provided is invalid.");
                }
                $http
                        .get(api_domain + 'students/' + $rootScope.user.netname + "/schedules?semester=" + $scope.semester + "&automatic=true")
                        .success(function(data) {
                            if (data.schedule === null || data.schedule.length === 0) {
                                return opendialog("No schedule was found for the selected preference.");
                            }
                            $scope.magicschedule = data.schedule;
                            $scope.performMagic();
                        })
                        .error(function(data) {
                            return opendialog("We could not fetch schedules. Please, try again later.");
                        });
            };

            $scope.performMagic = function() {
                if (!resistorFactory.isValidSemester($scope.semester)) {
                    return opendialog("You need to select a semester and add at least a course.");
                }
                if ($scope.timeofday !== "day" && $scope.timeofday !== "evening") {
                    return opendialog("You need to select daily timing for your preferences.");
                }
                if (Number($scope.noofcourses) < 1 || Number($scope.noofcourses) > 5) {
                    return opendialog("Number of courses provided is invalid.");
                }
                takendivids = [];
                $("#scheduler" + 0).remove();
                for (var i in $scope.addedschedules) {
                    for (var j  in  $scope.addedschedules[i].sections) {
                        removeoverlay(0, $scope.addedschedules[i].sections[j]);
                    }
                    if ($scope.addedschedules[i].tutorial !== undefined) {
                        removeoverlay(0, $scope.addedschedules[i].tutorial);
                    }
                    if ($scope.addedschedules[i].lab !== undefined) {
                        removeoverlay(0, $scope.addedschedules[i].lab);
                    }
                }
                $(".overlay").css("display", "none");
                $scope.addedschedules = {};
                var tempschedule = {};
                var coursecount = 0;
                var currentcredit = 0;
                $scope.magicschedule = shuffleArray($scope.magicschedule);
                for (var index in $scope.magicschedule) {
                    if (coursecount >= $scope.noofcourses) {
                        break;
                    }
                    tempschedule = filterScheduleCourse($scope.accepteddivs, $scope.magicschedule[index], $scope.excludeday);
                    if (!isEmpty(tempschedule)) {
                        var clashdey = false;
                        for (var m = 0; m < tempschedule.sections.length; m++) {
                            if (doesScheduleClash($scope.magicschedule[index].code, tempschedule.sections[m].day, tempschedule.sections[m].starttime, tempschedule.sections[m].endtime)) {
                                clashdey = true;
                            }
                        }
                        if (tempschedule.tutorial !== undefined) {
                            if (doesScheduleClash($scope.magicschedule[index].code, tempschedule.tutorial.day, tempschedule.tutorial.starttime, tempschedule.tutorial.endtime)) {
                                clashdey = true;
                            }
                        }
                        if (tempschedule.lab !== undefined) {
                            if (doesScheduleClash($scope.magicschedule[index].code, tempschedule.lab.day, tempschedule.lab.starttime, tempschedule.lab.endtime)) {
                                clashdey = true;
                            }
                        }
                        if (clashdey === false) {
                            currentcredit = currentcredit + parseFloat($scope.magicschedule[index].credit);
                            if (currentcredit > parseFloat($rootScope.user.profile.maxtermcredit)) {
                                break;
                            }
                            coursecount = coursecount + 1;
                            $scope.addedschedules[$scope.magicschedule[index].code] = tempschedule;
                        }
                    }
                }
                if (isEmpty($scope.addedschedules)) {
                    return opendialog("We could not generate schedule at the moment. Please, try again later.");
                }
                $("#selectcourse").slideUp();
                displaySchedule(0, $scope.addedschedules, "automaticscheduler");
            };
        }
    }]);

function shuffleArray(array) {
    for (var i = array.length - 1; i > 0; i--) {
        var j = Math.floor(Math.random() * (i + 1));
        var temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
    return array;
}

function filterScheduleCourse(accepteddivs, course, excludeday) {
    course.lectures = shuffleArray(course.lectures);
    var tempschedule = {};
    var sectionfit;
    for (var lectureindex in course.lectures) {
        sectionfit = true;
        for (var sectionindex in course.lectures[lectureindex].sections) {
            if (!isAllowed(accepteddivs, course.lectures[lectureindex].sections[sectionindex]) || Number(course.lectures[lectureindex].sections[sectionindex].day) === Number(excludeday)) {
                sectionfit = false;
                break;
            }
        }
        if (sectionfit === true) {
            if (course.lectures[lectureindex].tutorials.length !== 0) {
                course.lectures[lectureindex].tutorials = shuffleArray(course.lectures[lectureindex].tutorials);
                for (var tutindex in course.lectures[lectureindex].tutorials) {
                    if (isAllowed(accepteddivs, course.lectures[lectureindex].tutorials[tutindex]) && Number(course.lectures[lectureindex].tutorials[tutindex].day) !== Number(excludeday)) {
                        if (course.lectures[lectureindex].tutorials[tutindex].labs.length !== 0) {
                            course.lectures[lectureindex].tutorials[tutindex].labs = shuffleArray(course.lectures[lectureindex].tutorials[tutindex].labs);
                            for (var labindex in course.lectures[lectureindex].tutorials[tutindex].labs) {
                                if (isAllowed(accepteddivs, course.lectures[lectureindex].tutorials[tutindex].labs[labindex]) && Number(course.lectures[lectureindex].tutorials[tutindex].labs[labindex]).day !== Number(excludeday)) {
                                    tempschedule.courselectureid = course.lectures[lectureindex].courselectureid;
                                    tempschedule.lecture = course.lectures[lectureindex].lecture;
                                    tempschedule.sections = course.lectures[lectureindex].sections;
                                    tempschedule.code = course.code;
                                    tempschedule.tutorial = {};
                                    tempschedule.tutorial.coursetutorialid = course.lectures[lectureindex].tutorials[tutindex].coursetutorialid;
                                    tempschedule.tutorial.starttime = course.lectures[lectureindex].tutorials[tutindex].starttime;
                                    tempschedule.tutorial.day = course.lectures[lectureindex].tutorials[tutindex].day;
                                    tempschedule.tutorial.endtime = course.lectures[lectureindex].tutorials[tutindex].endtime;
                                    tempschedule.tutorial.name = course.lectures[lectureindex].tutorials[tutindex].name;
                                    tempschedule.tutorial.location = course.lectures[lectureindex].tutorials[tutindex].location;
                                    tempschedule.lab = {};
                                    tempschedule.lab.courselabid = course.lectures[lectureindex].tutorials[tutindex].labs[labindex].courselabid;
                                    tempschedule.lab.starttime = course.lectures[lectureindex].tutorials[tutindex].labs[labindex].starttime;
                                    tempschedule.lab.day = course.lectures[lectureindex].tutorials[tutindex].labs[labindex].day;
                                    tempschedule.lab.endtime = course.lectures[lectureindex].tutorials[tutindex].labs[labindex].endtime;
                                    tempschedule.lab.name = course.lectures[lectureindex].tutorials[tutindex].labs[labindex].name;
                                    tempschedule.lab.location = course.lectures[lectureindex].tutorials[tutindex].labs[labindex].location;
                                    return tempschedule;
                                }
                            }
                        }
                        else {
                            tempschedule.courselectureid = course.lectures[lectureindex].courselectureid;
                            tempschedule.lecture = course.lectures[lectureindex].lecture;
                            tempschedule.sections = course.lectures[lectureindex].sections;
                            tempschedule.code = course.code;
                            tempschedule.tutorial = {};
                            tempschedule.tutorial.coursetutorialid = course.lectures[lectureindex].tutorials[tutindex].coursetutorialid;
                            tempschedule.tutorial.starttime = course.lectures[lectureindex].tutorials[tutindex].starttime;
                            tempschedule.tutorial.day = course.lectures[lectureindex].tutorials[tutindex].day;
                            tempschedule.tutorial.endtime = course.lectures[lectureindex].tutorials[tutindex].endtime;
                            tempschedule.tutorial.name = course.lectures[lectureindex].tutorials[tutindex].name;
                            tempschedule.tutorial.location = course.lectures[lectureindex].tutorials[tutindex].location;
                            return tempschedule;
                        }
                    }
                }
            } else {
                tempschedule.code = course.code;
                tempschedule.courselectureid = course.lectures[lectureindex].courselectureid;
                tempschedule.lecture = course.lectures[lectureindex].lecture;
                tempschedule.sections = course.lectures[lectureindex].sections;
                return tempschedule;
            }
        }
    }
    return tempschedule;
}
function isAllowed(accepteddivs, schedule) {
    var starttime = roundUpTime(schedule.starttime);
    var endtime = roundUpTime(schedule.endtime);
    var nextdate = starttime;
    while (nextdate !== endtime) {
        if (accepteddivs.indexOf(getClassFromTime(schedule.day, roundUpTime(nextdate))) === -1) {
            return false;
        }
        nextdate = getNextDate(nextdate);
    }
    return true;
}
ResistorApp.controller('loginCtrl', ['$scope', '$http', '$rootScope', '$location', 'resistorFactory', function($scope, $http, $rootScope, $location, resistorFactory) {
        resistorFactory.updateMenu();
        $scope.formData = {};
        $scope.errors = {};
        $scope.message = "You need to login to continue.";
        $scope.submitLoginForm = function() {
            switch ($scope.loginForm.$valid) {
                case false:
                    $scope.message = "You have provided invalid data in one or more fields.";
                    break;
                default :
                    $scope.errors = {};
                    $scope.message = "Attempting to log you in.....";
                    $http
                            .post(api_domain + 'sessions', $scope.formData)
                            .success(function(data) {
                                $scope.message = "Log in successful.";
                                if (data.user !== undefined) {
                                    $rootScope.user = data.user;
                                }
                                resistorFactory.updateMenu();
                                if ($rootScope.redirecturl !== undefined) {
                                    $location.url($rootScope.redirecturl);
                                    return true;
                                }
                                if ($rootScope.user.privilege !== undefined) {
                                    $location.url("/" + $rootScope.user.privilege.toLowerCase());
                                    return true;
                                }
                            })
                            .error(function(data) {
                                $scope.message = "We were unable to log you in.";
                                $scope.errors = data.errors;
                            });
                    break;
            }
        };
        $scope.reset = function() {
            $scope.formData = {};
        };
    }]);
ResistorApp.controller('adminCtrl', ['$scope', 'resistorFactory', '$upload', function($scope, resistorFactory, $upload) {
        if (resistorFactory.validLoggedIn(adminPrivilege)) {
            $scope.errors = {};
            $scope.coursexmlmessage = "Please, select your file below. File should not more than 500kb.";
            $scope.onCourseXMLSelect = function($files) {
                if ($files.length === 0) {
                    $scope.coursexmlmessage = "You are yet to upload any file.";
                    return false;
                }
                var file = $files[0];
                if (file.name.toString().slice(-4) !== ".xml" || file.type.toString() !== "text/xml") {
                    $scope.coursexmlmessage = "Uploaded file is not valid xml file.";
                    return false;
                }
                if (Number(file.size) > 512000) {
                    $scope.coursexmlmessage = "Uploaded file is bigger than the allowed file size";
                    return false;
                }
                $scope.coursexmlmessage = "Attempting to upload your file...this might take a while.";
                $scope.upload = $upload
                        .upload({
                            url: api_domain + 'courses', method: 'POST',
                            data: JSON.stringify({model: {foo: '3'}}), // does
                            // nothing
                            // really
                            // but
                            // necessary
                            // for
                            // upload
                            file: file
                        })
                        .success(function(data) {
                            $scope.coursexmlmessage = "Courses were successfully uploaded.";
                            $scope.errors = {};
                        })
                        .error(function(data) {
                            $scope.errors = data.errors;
                            $scope.coursexmlmessage = "File upload failed. Please, try again.";
                        });
            };
            $scope.studentxmlmessage = "Please, select your file below. File should not more than 500kb.";
            $scope.onStudentXMLSelect = function($files) {
                if ($files.length === 0) {
                    $scope.studentxmlmessage = "You are yet to upload any file.";
                    return false;
                }
                var file = $files[0];
                if (file.name.toString().slice(-4) !== ".xml" || file.type.toString() !== "text/xml") {
                    $scope.studentxmlmessage = "Uploaded file is not valid xml file.";
                    return false;
                }
                if (Number(file.size) > 512000) {
                    $scope.studentxmlmessage = "Uploaded file is bigger than the allowed file size";
                    return false;
                }
                $scope.studentxmlmessage = "Attempting to upload your file...this might take a while.";
                $scope.upload = $upload
                        .upload({
                            url: api_domain + 'students', method: 'POST',
                            data: JSON.stringify({model: {foo: '3'}}), // does
                            // nothing
                            // really
                            // but
                            // necessary
                            // for
                            // upload
                            file: file
                        })
                        .success(function(data) {
                            $scope.studentxmlmessage = "Students were successfully uploaded.";
                            $scope.errors = {};
                        })
                        .error(function(data) {
                            $scope.errors = data.errors;
                            $scope.studentxmlmessage = "File upload failed. Please, try again.";
                        });
            };
        }
    }]);
ResistorApp.controller('studentCtrl', ['$rootScope', '$http', '$scope', 'resistorFactory', function($rootScope, $http, $scope, resistorFactory) {
        if (resistorFactory.validLoggedIn(studentPrivilege)) {
            resistorFactory.getStudentRecord();
        }
    }]);
ResistorApp.controller('errorCtrl', ['$rootScope', '$scope', function($rootScope, $scope) {
        $scope.heading = $rootScope.heading === undefined ? "Unexpected Error." : $rootScope.heading;
        $rootScope.heading = "";
        $scope.message = $rootScope.message === undefined ? "An unexpected error has occurred. Please, try again later." : $rootScope.message;
        $rootScope.message = "";
    }]);
ResistorApp.controller('notFoundCtrl', ['$scope', function($scope) {
        $scope.heading = "Page Not Found.";
        $scope.message = "The page you requested could not be found.";
    }]);
ResistorApp.controller('logoutCtrl', ['$http', '$location', '$rootScope', 'resistorFactory', function($http, $location, $rootScope, resistorFactory) {
        $http.delete(api_domain + 'sessions');
        $rootScope.user = [];
        $location.url('/');
    }]);
ResistorApp.filter("urlString", function() {
    return function(string, id) {
        return id + "-" + string.toLowerCase().replace(/ +/g, '-').replace(/[0-9]/g, '').replace(/[^a-z0-9-_]/g, '').trim();
    };
});
ResistorApp.filter("idFromUrlString", function() {
    return function(string) {
        return string.toString().split("-")[0];
    };
});
ResistorApp.factory('resistorFactory', ['$rootScope', '$location', '$http', function($rootScope, $location, $http) {
        return {
            isValidSemester: function(semester) {
                if (semester !== "Fall" && semester !== "Winter" && semester !== "Summer") {
                    return false;
                }
                return true;
            },
            getStudentRecord: function() {
                $http
                        .get(api_domain + 'students/' + $rootScope.user.netname)
                        .success(function(data) {
                            $rootScope.user.profile = data.profile;
                            $rootScope.user.studentrecordtaken = [];
                            $rootScope.user.studentrecordregistered = [];
                            for (var index in data.studentrecord) {
                                if (data.studentrecord[index].semester === null) {
                                    $rootScope.user.studentrecordtaken.push(data.studentrecord[index]);
                                }
                                else {
                                    $rootScope.user.studentrecordregistered.push(data.studentrecord[index]);
                                }
                            }
                        })
                        .error(function(data) {
                            $location.url('/error');
                            return true;
                        });
            },
            validLoggedIn: function(privilege) {
                if ($rootScope.user.privilege === undefined || $rootScope.user.privilege !== privilege) {
                    $rootScope.user = [];
                    if ($location.path() !== "/") {
                        $rootScope.redirecturl = $location.path();
                    }
                    $location.url('/');
                    return false;
                }
                return true;
            },
            updateMenu: function() {
                var privilege = ($rootScope.user !== undefined && $rootScope.user.privilege !== undefined) ? $rootScope.user.privilege : "guest";
                $http
                        .get(base_domain + "partials/menu/" + privilege.toLowerCase() + ".json")
                        .success(function(data) {
                            $rootScope.menu = data;
                        })
                        .error(function(data) {
                            $location.url('/error');
                        });
                return;
            },
            isArray: function(obj) {
                return toString.call(obj) === "[object Array]";
            }
        };
    }]);
function htmlEntities(str) {
    return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

function getFile(id) {
    document.getElementById(id).click();
}

function sub(obj, divid) {
    if (obj.value !== undefined) {
        var file = obj.value;
        var fileName = file.split("\\");
        document.getElementById(divid).innerHTML = htmlEntities(fileName[fileName.length - 1]).toString().substring(0, 40);
    }
}

function opendialog(text) {
    $("#dialoginner").html(text);
    $("#dialog").dialog("open").dialog({
        buttons: {"Close": function() {
                $(this).dialog("close");
            }
        }
    });
}

function selectLecture(courselectureid) {
    $(".innerpreferenceouter").slideUp();
    $(".innerinnerpreferenceouter").slideUp();
    selectedcoursetutorialid = 0;
    selectedcourselabid = 0;
    if (courselectureid !== 0 && $("#" + courselectureid + "-innerpreference")) {
        selectedcourselectureid = courselectureid;
        $("#" + courselectureid + "-innerpreference").slideDown();
    }
}
function selectTutorial(coursetutorialid) {
    $(".innerinnerpreferenceouter").slideUp();
    selectedcourselabid = 0;
    if (coursetutorialid !== 0 && $("#" + coursetutorialid + "-innerinnerpreference")) {
        selectedcoursetutorialid = coursetutorialid;
        $("#" + coursetutorialid + "-innerinnerpreference").slideDown();
    }
}
function selectLab(courselabid) {
    selectedcourselabid = courselabid;
}
function trimTime(time) {
    return time.substring(0, 5);
}
function doesScheduleClash(coursecode, day, starttime, endtime) {
    var temp = takendivids;
    starttime = roundUpTime(starttime);
    endtime = roundUpTime(endtime);
    var nextdate = starttime;
    var divclass;
    var i = 0;
    while (nextdate !== endtime) {
        divclass = getClassFromTime(day, nextdate);
        for (var index in temp) {
            if (temp[index].split("-")[1] === divclass) {
                clashcode = temp[index].split("-")[0];
                return true;
            }
        }
        i++;
        temp.push(coursecode + "-" + divclass);
        nextdate = getNextDate(nextdate);
    }
    takendivids = temp;
    return false;
}
function roundUpTime(min) {
    min = min.split(":");
    if (Number(min[1]) === 00) {
        min[1] = "00";
    }
    else if (Number(min[1]) <= 15) {
        min[1] = "15";
    }
    else if (Number(min[1]) > 15 && Number(min[1]) <= 30) {
        min[1] = "30";
    }
    else if (Number(min[1]) > 30 && Number(min[1]) <= 45) {
        min[1] = "45";
    } else {
        min[0] = Number(min[0]) + 1;
        if (min[0].toString().length === 1) {
            min[0] = "0" + min[0].toString();
        }
        min[1] = "00";
    }
    min = removeFromArray(min, 2);
    return min.join(":");
}
function getClassFromTime(day, starttime) {
    starttime = roundUpTime(starttime);
    var next = getNextDate(starttime);
    starttime = starttime.split(":");
    next = next.split(":");
    return day + starttime[0] + starttime[1] + next[0] + next[1];
}
function getNextDate(time) {
    time = time.split(":");
    var next = [];
    if ((Number(time[1]) + 15) > 45) {
        next[0] = Number(time[0]) + 1;
        next[1] = "00";
    } else {
        next[0] = Number(time[0]);
        next[1] = Number(time[1]) + 15;
    }
    if (next[0].toString().length === 1) {
        next[0] = "0" + next[0].toString();
    }
    return next.join(":");
}
function removeoverlay(tableid, schedule) {
    if ($("#scheduler" + tableid).find("." + getClassFromTime(schedule.day, schedule.starttime)).length)
        $("#scheduler" + tableid).find("." + getClassFromTime(schedule.day, schedule.starttime)).remove();
}
function showoverlay(tableid, schedule, content) {
    $("#overlay").clone().attr("id", "overlay-" + getClassFromTime(schedule.day, schedule.starttime)).appendTo("body").css({"position": "absolute", "height": calculateHeight(schedule.starttime, schedule.endtime) + "px", "display": "flex"}).position({my: "left top", at: "left top",
        of: $("#scheduler" + tableid).find("." + getClassFromTime(schedule.day, schedule.starttime)),
        collision: "fit"
    }).html(content);
}

function displaySchedule(id, schedules, destinationid) {
    $("#schedulertable").clone().attr("id", "scheduler" + id).appendTo("#" + destinationid);
    var rowtaken;
    var temp;
    for (var i = 0; i < tablerows.length; i++) {
        rowtaken = false;
        for (var j = 1; j < 6; j++) {
            temp = j + tablerows[i];
            for (var index in takendivids) {
                if (takendivids[index].split("-")[1] === temp) {
                    rowtaken = true;
                }
            }
        }
        if (rowtaken === false) {
            $("#scheduler" + id).find("." + tablerows[i]).slideUp();
            $("#scheduler" + id).find("." + tablerows[i]).remove();
        }
    }
    $('#schedulerdiv').fadeIn();
    $("#scheduler" + id).fadeIn();
    var content;
    for (var i in schedules) {
        for (var j in schedules[i].sections) {
            content = htmlEntities(schedules[i].code) + " " + htmlEntities(schedules[i].lecture) + " <br/>" + " Location : " + htmlEntities(schedules[i].sections[j].location);
            showoverlay(id, schedules[i].sections[j], content);
        }
        if (schedules[i].tutorial !== undefined) {
            content = htmlEntities(schedules[i].code) + " " + htmlEntities(schedules[i].tutorial.name) + " <br/>" + " Location : " + htmlEntities(schedules[i].tutorial.location);
            showoverlay(id, schedules[i].tutorial, content);
        }
        if (schedules[i].lab !== undefined) {
            content = htmlEntities(schedules[i].code) + " " + htmlEntities(schedules[i].lab.name) + " <br/>" + " Location : " + htmlEntities(schedules[i].lab.location);
            showoverlay(id, schedules[i].lab, content);
        }
    }

}
function calculateHeight(starttime, endtime) {
    starttime = roundUpTime(starttime);
    endtime = roundUpTime(endtime);
    starttime = starttime.split(":");
    endtime = endtime.split(":");
    return (((4 * (Number(endtime[0]) - Number(starttime[0]))) + ((Number(endtime[1]) - Number(starttime[1])) / 15)) * 40) + 1;
}
function removeFromArray(arr, from) {
    var temp = [];
    for (var i = 0; i < arr.length; i++) {
        if (i === from) {
            continue;
        }
        temp.push(arr[i]);
    }
    return temp;
}

ResistorApp.controller('savedScheduleCtrl', ['$rootScope', '$scope', '$http', 'resistorFactory', function($rootScope, $scope, $http, resistorFactory) {
        if (resistorFactory.validLoggedIn(studentPrivilege)) {
            resistorFactory.getStudentRecord();
            $scope.allsavedschedules = [];
            $scope.currentscheduleindex = 0;
            $scope.semester = "None";
            $http
                    .get(api_domain + 'students/' + $rootScope.user.netname + "/schedules")
                    .success(function(data) {
                        if (data.schedule.length === 0) {
                            $("#optiontable").remove();
                            return opendialog("You don't have any saved schedule to display.");
                        }
                        $scope.allsavedschedules = data.schedule;
                        $scope.loadNext();
                    })
                    .error(function(data) {
                        return opendialog("We could not retrieve your saved schedules at the moment.");
                    });
            $scope.deleteSchedule = function() {
                $http({
                    url: api_domain + 'students/' + $rootScope.user.netname + "/schedules",
                    method: "DELETE",
                    data: {scheduleid: $scope.allsavedschedules [($scope.currentscheduleindex - 1) % $scope.allsavedschedules.length]['scheduleid']}
                })
                        .success(function(data) {
                            opendialog("Schedule was successfully deleted.");
                            $scope.allsavedschedules = removeFromArray($scope.allsavedschedules, ($scope.currentscheduleindex - 1) % $scope.allsavedschedules.length);
                            $scope.currentscheduleindex = 0;
                            $scope.loadNext();
                        })
                        .error(function(data) {
                            if (data.errors !== undefined)
                                return opendialog("Schedule could not be deleted because " + data.errors.schedule);
                            opendialog("Schedule could not be deleted.");
                        });

            };
            $scope.loadNext = function() {
                $("#scheduler" + $scope.currentscheduleindex).remove();
                if ($scope.allsavedschedules.length === 0) {
                    $("#schedulerdiv").remove();
                    $(".overlay").css("display", "none");
                    return opendialog("You don't have any saved schedule to display.");
                }
                var tempschedule = angular.fromJson($scope.allsavedschedules[$scope.currentscheduleindex % $scope.allsavedschedules.length]['schedulejson']);
                $scope.semester = $scope.allsavedschedules[$scope.currentscheduleindex % $scope.allsavedschedules.length]['semester'];
                for (var i in tempschedule) {
                    for (var j  in tempschedule[i].sections) {
                        removeoverlay(0, tempschedule[i].sections[j]);
                    }
                    if (tempschedule[i].tutorial !== undefined) {
                        removeoverlay(0, tempschedule[i].tutorial);
                    }
                    if (tempschedule[i].lab !== undefined) {
                        removeoverlay(0, tempschedule[i].lab);
                    }
                }
                takendivids = [];
                $(".overlay").css("display", "none");
                for (var index in tempschedule) {
                    for (var m = 0; m < tempschedule[index].sections.length; m++) {
                        doesScheduleClash(index, tempschedule[index].sections[m].day, tempschedule[index].sections[m].starttime, tempschedule[index].sections[m].endtime);
                    }
                    if (tempschedule[index].tutorial !== undefined) {
                        doesScheduleClash(index, tempschedule[index].tutorial.day, tempschedule[index].tutorial.starttime, tempschedule[index].tutorial.endtime);
                    }
                    if (tempschedule[index].lab !== undefined) {
                        doesScheduleClash(index, tempschedule[index].lab.day, tempschedule[index].lab.starttime, tempschedule[index].lab.endtime);
                    }
                }
                displaySchedule($scope.currentscheduleindex + 1, tempschedule, "savedschedule");
                $scope.currentscheduleindex = $scope.currentscheduleindex + 1;
            };
            $scope.registerCourses = function() {
                $http({
                    url: api_domain + 'students/' + $rootScope.user.netname + "/courses",
                    method: "POST",
                    data: {scheduleid: $scope.allsavedschedules [($scope.currentscheduleindex - 1) % $scope.allsavedschedules.length]['scheduleid']}
                })
                        .success(function(data) {
                            opendialog("Courses were successfully registered.");
                        })
                        .error(function(data) {
                            if (data.errors !== undefined)
                                return opendialog("Course could not be registered because " + data.errors.courses);
                            opendialog("Schedule could not be deleted.");
                        });

            };
        }
    }]);

ResistorApp.controller('manualSchedulerCtrl', ['$rootScope', '$scope', '$http', 'resistorFactory', function($rootScope, $scope, $http, resistorFactory) {
        if (resistorFactory.validLoggedIn(studentPrivilege)) {
            resistorFactory.getStudentRecord();
            $scope.semester = "None";
            $scope.coursemessage = "Please provide the course code/name.";
            $scope.semestermessage = "No courses loaded.";
            $scope.addedschedules = {};
            $scope.currentschedule = {};
            $scope.currentcredit = 0;
            $scope.availablecourses = [];
            $scope.saveCourses = function() {
                if (isEmpty($scope.addedschedules)) {
                    return opendialog("No schedule is detected.");
                }
                $http({
                    url: api_domain + "students/" + $rootScope.user.netname + "/courses",
                    method: "POST",
                    data: {schedulejson: angular.toJson($scope.addedschedules), semester: $scope.semester}
                }).success(function(data) {
                    $scope.editSchedule();
                    $scope.addedschedules = {};
                    takendivids = [];
                    $scope.currentcredit = 0;
                    opendialog("Courses were successfully updated.");
                }).error(function(data) {
                    if (data.errors !== undefined) {
                        return opendialog("Courses could not be updated because : " + data.errors.courses);
                    }
                });
            };
            $scope.saveSchedule = function() {
                if (isEmpty($scope.addedschedules)) {
                    return opendialog("No schedule is detected.");
                }
                $http({
                    url: api_domain + "students/" + $rootScope.user.netname + "/schedules",
                    method: "POST",
                    data: {schedulejson: angular.toJson($scope.addedschedules), semester: $scope.semester}
                }).success(function(data) {
                    $scope.editSchedule();
                    $scope.addedschedules = {};
                    takendivids = [];
                    $scope.currentcredit = 0;
                    opendialog("Schedule was successfully saved.");
                }).error(function(data) {
                    if (data.errors !== undefined) {
                        return opendialog("Schedule could not be added because : " + data.errors.schedule);
                    }
                });
            };
            $scope.editSchedule = function() {
                $('#schedulerdiv').slideUp();
                $("#selectcourse").slideDown();
                for (var i in $scope.addedschedules) {
                    for (var j  in  $scope.addedschedules[i].sections) {
                        removeoverlay(0, $scope.addedschedules[i].sections[j]);
                    }
                    if ($scope.addedschedules[i].tutorial !== undefined) {
                        removeoverlay(0, $scope.addedschedules[i].tutorial);
                    }
                    if ($scope.addedschedules[i].lab !== undefined) {
                        removeoverlay(0, $scope.addedschedules[i].lab);
                    }
                }
                $(".overlay").css("display", "none");
                $("#scheduler" + 0).remove();
            };
            $scope.generateSchedule = function() {
                $scope.editSchedule();
                if (!resistorFactory.isValidSemester($scope.semester)) {
                    return opendialog("You need to select a semester and add at least a course.");
                }
                if (isEmpty($scope.addedschedules)) {
                    return opendialog("You need to add at least a course first!");
                }
                $("#selectcourse").slideUp();
                displaySchedule(0, $scope.addedschedules, "manualscheduler");
            };
            $scope.removeAddedCourse = function(coursecode) {
                var newaddedschedules = $scope.addedschedules;
                $scope.addedschedules = {};
                takendivids = [];
                for (var index in newaddedschedules) {
                    if (coursecode === index) {
                        $scope.currentcredit = $scope.currentcredit - parseFloat(newaddedschedules[index].credit);
                    } else {
                        $scope.addedschedules[index] = newaddedschedules[index];
                        for (var m = 0; m < $scope.addedschedules[index].sections.length; m++) {
                            doesScheduleClash(coursecode, $scope.addedschedules[index].sections[m].day, $scope.addedschedules[index].sections[m].starttime, $scope.addedschedules[index].sections[m].endtime);
                        }
                        if ($scope.addedschedules[index].tutorial !== undefined) {
                            doesScheduleClash(coursecode, $scope.addedschedules[index].tutorial.day, $scope.addedschedules[index].tutorial.starttime, $scope.addedschedules[index].tutorial.endtime);
                        }
                        if ($scope.addedschedules[index].lab !== undefined) {
                            doesScheduleClash(coursecode, $scope.addedschedules[index].lab.day, $scope.addedschedules[index].lab.starttime, $scope.addedschedules[index].lab.endtime);
                        }
                    }
                }
            };
            $scope.addCourse = function() {
                selectedcoursetutorialid = 0;
                selectedcourselabid = 0;
                selectedcourselectureid = 0;
                $scope.currentschedule = {};
                var coursecode = $("#coursetext").val().toString().split("-")[0].toString().trim();
                var tempcurrentcredit = $scope.currentcredit;
                var found = false;
                for (var i = 0; i < $scope.availablecourses.length; i++) {
                    if (coursecode === $scope.availablecourses[i].code) {
                        tempcurrentcredit = tempcurrentcredit + Number($scope.availablecourses[i].credit);
                        found = true;
                        break;
                    }
                }
                if (found === false) {
                    return opendialog(coursecode + " is not a valid course.");
                }
                if (tempcurrentcredit > parseFloat($rootScope.user.profile.maxtermcredit)) {
                    return opendialog("Adding " + coursecode + " will exceed the maximum number of credits you can register per semester.");
                }
                for (var index in $scope.addedschedules) {
                    if (coursecode === index) {
                        return opendialog(coursecode + " has already been added. Please, remove it first.");
                    }
                }
                $http
                        .get(api_domain + "students/" + $rootScope.user.netname + "/courses/" + coursecode + "/schedules?semester=" + $scope.semester)
                        .success(function(data) {
                            data = data.schedule;
                            var content = "   <form class='form-horizontal' role='form'><div id='preferencemessage' class='message'>Please, select your preference below:</div>";
                            for (var i = 0; i < data.lectures.length; i++) {
                                content = content + "<div class='preference'>";
                                content = content + "<input onclick='selectLecture(" + data.lectures[i].courselectureid + ")' value='" + data.lectures[i].courselectureid + "' type='radio' name='lecturepreference' />&nbsp;" + data.lectures[i].lecture + "(" + data.lectures[i].professor + ")";
                                content = content + "<div id='" + data.lectures[i].courselectureid + "-innerpreference' class='innerpreferenceouter'>";
                                for (var j = 0; j < data.lectures[i].sections.length; j++) {
                                    content = content + "<div class='innerpreference'>";
                                    content = content + "Lect : " + day_array[Number(data.lectures[i].sections[j].day)] + ", " + trimTime(data.lectures[i].sections[j].starttime) + "-" + trimTime(data.lectures[i].sections[j].endtime) + ", " + data.lectures[i].sections[j].location;
                                    content = content + "</div>";
                                }
                                if (data.lectures[i].tutorials !== undefined && data.lectures[i].tutorials !== null && data.lectures[i].tutorials.length > 0) {
                                    for (var j = 0; j < data.lectures[i].tutorials.length; j++) {
                                        content = content + "<div class='innerpreference'>";
                                        content = content + "<input onclick='selectTutorial(" + data.lectures[i].tutorials[j].coursetutorialid + ")' value='" + data.lectures[i].tutorials[j].coursetutorialid + "' type='radio' name='tutorialpreference' />&nbsp;" + data.lectures[i].tutorials[j].name + " : " + day_array[Number(data.lectures[i].tutorials[j].day)] + "," + trimTime(data.lectures[i].tutorials[j].starttime) + "-" + trimTime(data.lectures[i].tutorials[j].endtime) + "," + data.lectures[i].tutorials[j].location;
                                        if (data.lectures[i].tutorials[j].labs !== undefined && data.lectures[i].tutorials[j].labs !== null && data.lectures[i].tutorials[j].labs.length > 0) {
                                            content = content + "<div id='" + data.lectures[i].tutorials[j].coursetutorialid + "-innerinnerpreference' class='innerinnerpreferenceouter'>";
                                            for (var k = 0; k < data.lectures[i].tutorials[j].labs.length; k++) {
                                                content = content + "<div class='innerpreference'>";
                                                content = content + "<input value='" + data.lectures[i].tutorials[j].labs[k].courselabid + "' type='radio' name='labpreference' />&nbsp;Lab : " + day_array[Number(data.lectures[i].tutorials[j].labs[k].day)] + ", " + trimTime(data.lectures[i].tutorials[j].labs[k].starttime) + "-" + trimTime(data.lectures[i].tutorials[j].labs[k].endtime) + ", " + data.lectures[i].tutorials[j].labs[k].location;
                                                content = content + "</div>";
                                            }
                                            content = content + "</div>";
                                        }
                                        content = content + "</div>";
                                    }
                                }
                                content = content + "</div>";
                                content = content + "</div>";
                                content = content + "</div>";
                            }
                            content = content + "</form>";
                            opendialog(content);
                            $scope.currentschedule = data;
                            $("#dialog").data('coursecode', coursecode).data('tempcurrentcredit', tempcurrentcredit).dialog({
                                buttons: {"Add Course": function() {
                                        for (var index in $scope.addedschedules) {
                                            if ($scope.currentschedule.code === index) {
                                                return opendialog($scope.currentschedule.code + " has already been added. Please, remove it first.");
                                            }
                                        }
                                        var tempschedule = {};
                                        tempschedule.code = $(this).data("coursecode");
                                        tempschedule.credit = 0;
                                        for (var i = 0; i < $scope.availablecourses.length; i++) {
                                            if (tempschedule.code === $scope.availablecourses[i].code) {
                                                tempschedule.credit = parseFloat($scope.availablecourses[i].credit);
                                                break;
                                            }
                                        }
                                        var success = false;
                                        for (var i = 0; i < $scope.currentschedule.lectures.length; i++) {
                                            if (Number($scope.currentschedule.lectures[i].courselectureid) === selectedcourselectureid) {
                                                tempschedule.courselectureid = $scope.currentschedule.lectures[i].courselectureid;
                                                tempschedule.lecture = $scope.currentschedule.lectures[i].lecture;
                                                tempschedule.sections = $scope.currentschedule.lectures[i].sections;
                                                if (selectedcoursetutorialid !== 0 && $scope.currentschedule.lectures[i].tutorials !== undefined && $scope.currentschedule.lectures[i].tutorials !== null && $scope.currentschedule.lectures[i].tutorials.length > 0) {
                                                    for (var j = 0; j < $scope.currentschedule.lectures[i].tutorials.length; j++) {
                                                        if (Number($scope.currentschedule.lectures[i].tutorials[j].coursetutorialid) === selectedcoursetutorialid) {
                                                            tempschedule.tutorial = {};
                                                            tempschedule.tutorial.coursetutorialid = $scope.currentschedule.lectures[i].tutorials[j].coursetutorialid;
                                                            tempschedule.tutorial.starttime = $scope.currentschedule.lectures[i].tutorials[j].starttime;
                                                            tempschedule.tutorial.day = $scope.currentschedule.lectures[i].tutorials[j].day;
                                                            tempschedule.tutorial.endtime = $scope.currentschedule.lectures[i].tutorials[j].endtime;
                                                            tempschedule.tutorial.name = $scope.currentschedule.lectures[i].tutorials[j].name;
                                                            tempschedule.tutorial.location = $scope.currentschedule.lectures[i].tutorials[j].location;
                                                            if (selectedcourselabid !== 0 && $scope.currentschedule.lectures[i].tutorials[j].labs !== undefined && $scope.currentschedule.lectures[i].tutorials[j].labs !== null && $scope.currentschedule.lectures[i].tutorials[j].labs.length > 0) {
                                                                for (var k = 0; k < $scope.lectures[i].tutorials[j].labs.length; k++) {
                                                                    if (Number($scope.currentschedule.lectures[i].tutorials[j].labs[k].courselabid) === selectedcourselabid) {
                                                                        tempschedule.lab = {};
                                                                        tempschedule.lab.courselabid = $scope.currentschedule.lectures[i].tutorials[j].labs[k].courselabid;
                                                                        tempschedule.lab.starttime = $scope.currentschedule.lectures[i].tutorials[j].labs[k].starttime;
                                                                        tempschedule.lab.day = $scope.currentschedule.lectures[i].tutorials[j].labs[k].day;
                                                                        tempschedule.lab.endtime = $scope.currentschedule.lectures[i].tutorials[j].labs[k].endtime;
                                                                        tempschedule.lab.name = $scope.currentschedule.lectures[i].tutorials[j].labs[k].name;
                                                                        tempschedule.lab.location = $scope.currentschedule.lectures[i].tutorials[j].labs[k].location;
                                                                    }
                                                                    break;
                                                                }
                                                            }
                                                            break;
                                                        }
                                                    }
                                                }
                                                success = true;
                                                break;
                                            }
                                        }
                                        if (success === false) {
                                            return  $("#dialoginner").html("There was a problem adding while trying to add course. Please, try again.");
                                        }
                                        for (var m = 0; m < tempschedule.sections.length; m++) {
                                            if (doesScheduleClash(coursecode, tempschedule.sections[m].day, tempschedule.sections[m].starttime, tempschedule.sections[m].endtime)) {
                                                $("#preferencemessage").html("This course clashes with your choice of " + htmlEntities(clashcode) + ". Please, select a new course.");
                                                return;
                                            }
                                        }
                                        if (tempschedule.tutorial !== undefined) {
                                            if (doesScheduleClash(coursecode, tempschedule.tutorial.day, tempschedule.tutorial.starttime, tempschedule.tutorial.endtime)) {
                                                $("#preferencemessage").html("Selected tutorial clashes with your choice of " + htmlEntities(clashcode) + ". Please, select another tutorial.");
                                                return;
                                            }
                                        }
                                        if (tempschedule.lab !== undefined) {
                                            if (doesScheduleClash(coursecode, tempschedule.lab.day, tempschedule.lab.starttime, tempschedule.lab.endtime)) {
                                                $("#preferencemessage").html("Selected lab clashes with your choice of " + htmlEntities(clashcode) + ". Please, select another lab.");
                                                return;
                                            }
                                        }
                                        $scope.addedschedules[$(this).data("coursecode")] = tempschedule;
                                        $scope.currentcredit = parseFloat($(this).data("tempcurrentcredit"));
                                        $scope.$apply();
                                        $("#coursetext").val('');
                                        $(this).dialog("close");
                                    },
                                    "Cancel"
                                            : function() {
                                                $(this).dialog("close");
                                                $("#coursetext").val('');
                                            }
                                }
                            });
                        })
                        .error(function(data) {
                            if (data.errors !== undefined) {
                                return opendialog(data.errors.course);
                            }
                        }
                        );
            };
            $scope.fetchAvailableCourses = function() {
                if (resistorFactory.isValidSemester($scope.semester)) {
                    $scope.semestermessage = "Loading " + $scope.semester + " courses...";
                    $http
                            .get(api_domain + 'students/' + $rootScope.user.netname + '/courses?available=true&semester=' + $scope.semester)
                            .success(function(data) {
                                if (data.available_courses !== undefined && data.available_courses !== null) {
                                    var temp = [];
                                    for (var i = 0; i < data.available_courses.length; i++) {
                                        temp[i] = data.available_courses[i].code + " - " + data.available_courses[i].name;
                                    }
                                    $("#coursetext").autocomplete({source: temp});
                                    $scope.availablecourses = data.available_courses;
                                    $scope.semestermessage = data.available_courses.length + " " + $scope.semester.toLowerCase() + " courses successfully loaded.";
                                }
                            })
                            .error(function(data) {
                                opendialog("We were unable to fetch available courses at the moment. Please, try again.");
                                $scope.semestermessage = "Please, change semester to try again.";
                            });
                }
                else {
                    $scope.semestermessage = "No courses loaded.";
                }
            };
            $scope.$watch('semester', function() {
                $("#coursetext").autocomplete({source: []});
                $scope.addedschedules = {};
                $scope.availablecourses = [];
                $scope.currentcredit = 0;
                $scope.fetchAvailableCourses();
            });
        }
    }]);