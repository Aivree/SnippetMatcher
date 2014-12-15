(function() {
  window.app.service('EmployeeService', [
    '$http', function($http) {
      return;
      return {
        login: function(data) {
          return $http({
            url: "/api/employees/login",
            data: data,
            method: "POST"
          }).success(function(status, data, headers, config) {
            return {
              sucess: true,
              status: status,
              data: data,
              headers: headers,
              config: config
            };
          }).error(function(status, data, headers, config) {
            return {
              sucess: false,
              status: status,
              data: data,
              headers: headers,
              config: config
            };
          });
        },
        getAllPet: function() {
          return $http({
            url: "/api/employees/GetAllPet",
            method: "GET"
          }).success(function(status, data, headers, config) {
            return {
              sucess: true,
              status: status,
              data: data,
              headers: headers,
              config: config
            };
          }).error(function(status, data, headers, config) {
            return {
              sucess: false,
              status: status,
              data: data,
              headers: headers,
              config: config
            };
          });
        },
        createPet: function(data) {
          return $http({
            url: "/api/employees/CreatePet",
            data: data,
            method: "POST"
          }).success(function(status, data, headers, config) {
            return {
              sucess: true,
              status: status,
              data: data,
              headers: headers,
              config: config
            };
          }).error(function(status, data, headers, config) {
            return {
              sucess: false,
              status: status,
              data: data,
              headers: headers,
              config: config
            };
          });
        }
      };
    }
  ]);

  window.app.service('CustomerService', [
    '$http', function($http) {
      return;
      return {
        login: function(data) {
          return $http({
            url: "/api/customers/login",
            data: data,
            method: "POST"
          }).success(function(status, data, headers, config) {
            return {
              sucess: true,
              status: status,
              data: data,
              headers: headers,
              config: config
            };
          }).error(function(status, data, headers, config) {
            return {
              sucess: false,
              status: status,
              data: data,
              headers: headers,
              config: config
            };
          });
        }
      };
    }
  ]);

}).call(this);
