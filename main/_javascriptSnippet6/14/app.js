(function () {

  //http://victorblog.com/2012/12/20/make-angularjs-http-service-behave-like-jquery-ajax/
  var param = function(obj) {
    var query = '', name, value, fullSubName, subName, subValue, innerObj, i;

    for(name in obj) {
      value = obj[name];

      if(value instanceof Array) {
        for(i=0; i<value.length; ++i) {
          subValue = value[i];
          fullSubName = name + '[' + i + ']';
          innerObj = {};
          innerObj[fullSubName] = subValue;
          query += param(innerObj) + '&';
        }
      } else if(value instanceof Object) {
        for(subName in value) {
          subValue = value[subName];
          fullSubName = name + '[' + subName + ']';
          innerObj = {};
          innerObj[fullSubName] = subValue;
          query += param(innerObj) + '&';
        }
      } else if(value !== undefined && value !== null) {
        query += encodeURIComponent(name) + '=' + encodeURIComponent(value) + '&';
      }
    }

    return query.length ? query.substr(0, query.length - 1) : query;
  };

  var app = angular.module("app", ['ngRoute'])

  .config(function ($routeProvider) {
    $routeProvider
    .when('/', {
      templateUrl: 'main.html'
    })
    .when('/login', {
      templateUrl: 'login.html',
      controller: 'loginController'
    })
    .when('/user', {
      templateUrl: 'user.html',
      controller: 'userController'
    })
    .when('/group', {
      templateUrl: 'group.html',
      controller: 'groupController'
    })
    .otherwise({redirectTo: '/'});
  })

  //http://stackoverflow.com/questions/11541695/redirecting-to-a-certain-route-based-on-condition
  .run(function ($rootScope, $location, securityService) {
    $rootScope.$on('$routeChangeStart', function (event, next, current) {
      if (!$rootScope.loggedUser) {
        if (next.templateUrl == 'login.html') {
        } else {
          $location.path('/login');
        }
      }
    })
  })

  .service('securityService', function ($http, $rootScope) {
    $http.defaults.headers.common['x-access-token'] = 'token';

    var loadUser = function (user) {
      $rootScope.loggedUser = user;
      $http.defaults.headers.common['x-access-token'] = user.token;
    };

    if (Storage) {
      if (localStorage.user) {
        loadUser(angular.fromJson(localStorage.user));
      }
    };

    this.login = function (options, callback) {
      $http({
        method: 'POST',
        url: '/login',
        data: param({
          username: options.username,
          password: options.password
        }),
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
      })
      .success(function (data, status) {
        loadUser(data);
        if (Storage) {
          localStorage.user = angular.toJson(data);
        };
        return callback();
      })
      .error(function (data, status) {
        if (status == 401) {
          return callback(data);
        } else {
          return callback('Some error happened');
        }
      });
    };

    this.logout = function (callback) {
      var removeLocal = function () {
        /* remove token local */
        delete $rootScope.loggedUser;
        delete $http.defaults.headers.common['x-access-token'];
        if (Storage) {
          if (localStorage.user) {
            delete localStorage.removeItem('user');
          }
        }
      };

      /* Logout from server */
      $http({
        method: 'GET',
        url: '/logout'
      })
      .success(function (data, status) {
        removeLocal();
        if (callback) {
          return callback();
        } else {
          return;
        }
      })
      .error(function (data, status) {
        removeLocal();
        if (callback) {
          return callback('Some error happened');
        } else {
          return;
        }
      });
    };
  })

  .factory('userFactory', function ($http) {
    var getAll = function (callback) {
      $http({
        method: 'GET',
        url: '/api/user'
      })
      .success(function (data, status) {
        return callback(null, data);
      })
      .error(function (data, status) {
        if (status == 401) {
          return callback(data);
        } else {
          return callback('Some error happened');
        }
      });
    };

    return {
      getAll: getAll
    }
  })

  .factory('groupFactory', function ($http) {
    var getAll = function (callback) {
      $http({
        method: 'GET',
        url: '/api/group'
      })
      .success(function (data, status) {
        return callback(null, data);
      })
      .error(function (data, status) {
        if (status == 401) {
          return callback(data);
        } else {
          return callback('Some error happened');
        }
      });
    };

    return {
      getAll: getAll
    }
  })

  .controller('mainController', function ($scope, $location, securityService) {
    $scope.logout = function () {
      securityService.logout(function () {
        $location.path('/');
      });
    };
  })

  .controller('loginController', function ($scope, $rootScope, $http, $location, securityService) {
    $scope.form = {
      username: '',
      password: ''
    };

    $scope.login = function () {
      securityService.login($scope.form, function (err) {
        if (err) {
          $scope.message = err;
        } else {
          $location.path('/');
        }
      });
    }
  })

  .controller('userController', function ($scope, $rootScope, userFactory) {
    $scope.message = 'Getting users';
    $scope.users = [];
    userFactory.getAll(function (err, data) {
      if (err) {
        $scope.message = err;
      } else {
        $scope.users = data;
        $scope.message = '';
      }
    });
  })

  .controller('groupController', function ($scope, $rootScope, groupFactory) {
    $scope.message = 'Getting groups';
    $scope.groups = [];
    groupFactory.getAll(function (err, data) {
      if (err) {
        $scope.message = err;
      } else {
        $scope.groups = data;
        $scope.message = '';
      }
    });
  });
})();
