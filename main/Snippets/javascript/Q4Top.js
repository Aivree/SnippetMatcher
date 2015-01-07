.data( "autocomplete" )._renderItem = function( ul, item ) {
  return $( "<li>" )
    .data( "item.autocomplete", item )
    .append( "<a>" + item.label + "<br>" + item.desc + "</a>" )
    .appendTo( ul );
};