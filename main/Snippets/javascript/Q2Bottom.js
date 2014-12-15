$http
.post('/echo/json/', 'json=' + encodeURIComponent(angular.toJson(data)), {
    headers: {
        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
    }
}).success(function(data) {
    $scope.data = data;
});