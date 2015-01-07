;(function() {
    $().ready(function() {

        var Qs = document.location.search.substring(1),
            params = {},
            queries = Qs.split("&"),
            temp,
            i,
            l;

        for ( i = 0, l = queries.length; i < l; i++ ) {
            temp = queries[i].split('=');
            params[temp[0]] = window.decodeURIComponent(temp[1]);
        }

        $('[data-js="message"]').html(params['message'] || '');
    });
})();