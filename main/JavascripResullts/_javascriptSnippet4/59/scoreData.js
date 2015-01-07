'use strict';

conquiztadorApp.factory('scoreData', function ($http) {

    return {
        getData: function (success) {
            $http({method: 'GET', url: 'http://conquiztador.apphb.com/api/Players/HighScore'})
                .success(function(data,status,headers,config){
                    success(data);
                })
                .error(function(data, status, headers, config){

                });
        }
    }
});