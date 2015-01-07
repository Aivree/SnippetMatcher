.data( "custom-catcomplete" )._renderItem = function( ul, item ) {
  return $( "<li>" )
    .data( "ui-autocomplete-item", item )
    .append( "<a>" + item.label + "<br>" + item.desc + "</a>" )
    .appendTo( ul );
};