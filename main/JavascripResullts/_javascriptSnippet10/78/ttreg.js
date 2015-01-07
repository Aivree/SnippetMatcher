(function($){        // for a random reason autocomplete does not work in this setup (undefined)
  $(document).ready(function(){
    $("input.partner").autocomplete({
        minLength: 2,
        source: "/auto.js",
        select: select_player
    })
    .each(function() {
        $(this).data( "ui-autocomplete" )._renderItem = player_formatter;
    });
  });
})(jQuery);

function select_player( event, ui) {
    event.target.value=player_desc(ui.item.player);
    ser_id=event.target.id+"_id"
    $("#"+ser_id).val(ui.item.player.id)
    return false;
}

function player_formatter( ul, item ) {
    return $( "<li>" )
        .data( "item.autocomplete", item )
        .append( "<a>" + player_desc(item.player) + "</a>")
        .appendTo( ul );
}

function player_desc(player) {
    if (player.woman_ranking != null) {
        ranking = " (" + player.ranking + "/" + player.woman_ranking + ")"
    } else {
        ranking = " (" + player.ranking + ")"
    }
    return player.name + " " + player.first_name + "; " + player.club + ranking
}

function sel_item(theUrl) {document.location.href = theUrl;}