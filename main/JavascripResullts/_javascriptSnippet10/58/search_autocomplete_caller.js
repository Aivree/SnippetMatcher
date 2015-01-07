// import this script if you need autocomplete for search, the actual logic of autocomplete is in jquery-1.9.1.js and ui/1.10.4/jquery-ui.js

$(function() {
    // this is for the /search
    if ($("#search_query" ).length > 0){
        $( "#search_query" ).autocomplete({
            source: function(request, response) {
                $.ajax({
                    type: "POST",
                    url: "/search",
                    dataType: "json",
                    data: {
                        format: "json",
                        term: request.term,
                        area: $("input[type='radio'][name='area']:checked").val()
                    },
                    success: function(data) {
                        response(data);
                    }
                });
            },
            minLength: 3
        })
        // with extra value returned to jsp
        .data( "ui-autocomplete" )._renderItem = function( ul, item ) {
            return $( "<li></li>" )
                .append( "<a><strong>" + item.value + "</strong> - " + item.desc + "</a>" ) // json:[{"value":"burger","desc":"doooc"},{}]
                .appendTo( ul );
        };
        // hit enter to submit the search form
        $("#search_query" ).submit(function(event){

        });
    }

    // this is for the search from nav bar
    if ($("#nav_search" ).length > 0){
        $( "#nav_search" ).autocomplete({
            source: function(request, response) {
                $.ajax({
                    type: "POST",
                    url: "/search",
                    dataType: "json",
                    data: {
                        format: "json",
                        term: request.term,
                        area: $("input[type='radio'][name='area']:checked").val()
                    },
                    success: function(data) {
                        response(data);
                    }
                });
            },
            minLength: 3
        })
            // with extra value returned to jsp
            .data( "ui-autocomplete" )._renderItem = function( ul, item ) {
            return $( "<li></li>" )
                .append( "<a><strong>" + item.value + "</strong> - " + item.desc + "</a>" ) // json:[{"value":"burger","desc":"doooc"},{}]
                .appendTo( ul );
        };
        // hit enter to submit the search form
        $( "#search_query" ).submit(function(event){

        });
    }

});

