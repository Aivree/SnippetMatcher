'use strict';

maestroApp.factory('controlService', function($http, $log) {
    return {
        back : function() {
            $http({
                method: "PUT",
                url: "/Back"
            })
                .success(function () {
                    console.log("Back.");
                })
                .error(function (data, status, headers, config) {
                    $log.warn(data, status, headers, config);
                });
        },
        next : function() {
            $http({
                method: "PUT",
                url: "/Next"
            })
                .success(function () {
                    console.log("Next.");
                })
                .error(function (data, status, headers, config) {
                    $log.warn(data, status, headers, config);
                });
        },
        play : function() {
            $http({
                method: "PUT",
                url: "/Play"
            })
                .success(function () {
                    console.log("Play.");
                })
                .error(function (data, status, headers, config) {
                    $log.warn(data, status, headers, config);
                });
        },
        pause : function() {
            $http({
                method: "PUT",
                url: "/pause"
            })
                .success(function () {
                    console.log("Pause.");
                })
                .error(function (data, status, headers, config) {
                    $log.warn(data, status, headers, config);
                });
        },
        stop : function() {
            $http({
                method: "PUT",
                url: "/stop"
            })
                .success(function () {
                    console.log("Stop.");
                })
                .error(function (data, status, headers, config) {
                    $log.warn(data, status, headers, config);
                });
        },
        playFromPlaylist: function(id) {
            $http({
                method: "POST",
                url: "/play/" + encodeURI(id)
            })
                 .success(function () {
                     console.log("Called play " + id);
                 })
                 .error(function (data, status, headers, config) {
                     $log.warn(data, status, headers, config);
                 });
        },
        removeFromPlaylist: function(id, index) {
            $http({
                method: "DELETE",
                url: "/Playlist/" + encodeURI(id) + "/" + encodeURI(index)
            })
                 .success(function () {
                     console.log("Removed from playlist " + id);
                 })
                 .error(function (data, status, headers, config) {
                     $log.warn(data, status, headers, config);
                 });
        },
        enqueueSong: function(id, callback) {
            $http({
                method: "POST",
                url: "/enqueue/" + encodeURI(id),
            })
                 .success(function () {
                     console.log("Called enqueue " + id);
                     callback();
                 })
                 .error(function (data, status, headers, config) {
                     $log.warn(data, status, headers, config);
                 });
        },

    };
});