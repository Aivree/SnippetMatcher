$(document).keypress(function(e) {
    if ((e.keyCode || e.which) == 13) {
        $('#search-btn').trigger('click');
    }
});

$(document).ready(function(){
    var $search_button = $('#search-btn'),
        $q = $('#search');

    $search_button.on('click', function(e) {
        e.preventDefault();

        var attrs = window.location.href.substring(window.location.href.lastIndexOf('/') + 1).split('&'),
            href = window.location.href.substring(0, window.location.href.lastIndexOf('/')) + '/',
            query = $q.val();

        window.location.replace(href + "?q=" + query);

        // $.each(attrs, function(index, val) {
        //     if (val.substring(0, 1) == '?') {
        //         val = val.substring(1, val.length);
        //     }
        //     var attr = val.split('=');
        //     // if (attr[0] == 'q') {
        //     //     window.location.replace(href + '?q=' + attr[1]);
        //     // }
            
        //     window.location.replace(href + '?q=' + $q.val());
        // });

        // var loc = window.location.href.substring(window.location.href.lastIndexOf('/') + 1);
        // var attrs = window.location.href.substring(window.location.href.lastIndexOf('/') + 1).split('&');
        // $.each(attrs, function(index, val) {
        //     var attr = val.split('=');
        //     if (attr[0] == 'q'){
        //         window.location.replace(window.location.href.substring(0, window.location.href.lastIndexOf('/') + 1) + "?q=" + attr[1]);
        //     }
        // });

        // loc.substring(loc.lastIndexOf("/") + 1);
        // window.location.replace($search_button[0].href + "?q=" + $q.val())
    });
});
