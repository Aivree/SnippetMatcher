'use strict';

maestroApp.factory('currentStatusService', function($http, $log) {
    return {
        getPlaylist: function(callback) {
            $http({ method: 'GET', url: '/Playlist' })
                .success(function(data, status, headers, config) {
                    callback(data);
                })
                .error(function(data, status, headers, config) {
                    $log.warn(data, status, headers, config);
                });
        },
        getNowPlaying: function(callback) {
            $http({ method: 'GET', url: '/NowPlaying' })
                .success(function(data, status, headers, config) {
                    callback(data);
                })
                .error(function(data, status, headers, config) {
                    $log.warn(data, status, headers, config);
                });
        }
    };
});