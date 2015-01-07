$(document).ready(function() {
    $("#cities").autocomplete({ 
        delay: 0,
          select: function( event, ui ) {
            console.log(ui.item);
            $( "#cities" ).val( ui.item.label );
            $( "#results" ).html( ui.item.desc );
     
            return false;
          },
          focus: function( event, ui ) {
            $( "#results" ).html("");
            return false;
          },
          source: function(request, response) {
            var base = 'search/' + encodeURI($("#cities").val());
            //var base = 'search/'' + $("#cities").val()';
            console.log(base);

                $.ajax({
                    dataType: "json",
                    type : 'Get',
                    url: base,
                    success: function(data) {
                        // console.log(data);

                        response(data.map(function(d) {
                            return {label: (d.city + ", " + d.state), desc: d.pop};//(d.city + ', ' + d.state + ': ' + d.pop);
                        }));
                    },
                    error: function(data) {
                        console.log("ERROR");
                    }
                });
        }
    }).autocomplete( "instance" )._renderItem = function( ul, item ) {

    return $( "<li>" )
        .append( "<a>" + item.label + "<br>" + item.desc + "</a>" )
        .appendTo( ul );
    };
    ;
    });