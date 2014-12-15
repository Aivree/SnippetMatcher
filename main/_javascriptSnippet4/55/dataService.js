angular.module('blogifire').factory("dataService", ["$http", "$q", function ($http, $q) {
    return {
        getItems: function (url, p) {
            return $http.get(url, {
                params: p
            });
        },
        addItem: function (url, item) {
            return $http({
                url: url,
                method: 'POST',
                data: item
            });
        },
        deleteItem: function (url, item) {
            if (item.Id) {
                return $http({
                    url: url + "/" + item.Id,
                    method: 'DELETE'
                });
            }
            else {
                return $http({
                    url: url,
                    method: 'DELETE',
                    data: item
                });
            }
        },
        updateItem: function (url, item) {
            return $http({
                url: url,
                method: 'PUT',
                data: item
            });
        },
        processChecked: function (url, items) {
            return $http({
                url: url,
                method: 'PUT',
                data: items
            });
        },
        uploadFile: function (url, file) {
            return $http({
                url: url,
                method: 'POST',
                data: file,
                withCredentials: true,
                headers: { 'Content-Type': undefined },
                transformRequest: angular.identity
            });
        }
    };
}]);