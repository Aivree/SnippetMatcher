var httpRequester = (function () {

    var promiseObj = function (url, method, data, headers) {
        var deferred = Q.defer();

        $.ajax({
            type: method,
            url: url,
            data: data,
            headers: headers,
            contentType: "application/json"
        }).done(function (reponse) {
                deferred.resolve(reponse);
            }).fail(function (err) {
                deferred.reject(err);
            });

        return deferred.promise;
    };

    return {
        getJson: function (url, headers) {
            return promiseObj(url, "GET", headers);
        },
        postJson: function (url, data, headers) {
            return promiseObj(url, "POST", data, headers);
        },
        putJson: function (url, data, headers) {
            return promiseObj(url, "PUT", data, headers);
        }
    };

}())
  
