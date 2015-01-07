

$(function() {

   $('input.search').each( function() {
      var $input = $(this);
      var serverUrl = $input.data('url');
       
      $(this).autocomplete({
         source: serverUrl,
               
         focus: function(event, ui) {
            $input.val(ui.item.Title);
            return false;
         },
         select: function(event, ui) {
            $input.val(ui.item.Title);
            document.getElementById("search").childNodes[1].submit();
            //$('form').submit();
            return false;
         }
      }).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
    	  return $( "<li></li>" )
    	  .data( "item.autocomplete", item )
    	  .append('<a><div class="list_item_container"><div class="image"><img id="pic" src="' + item.Image + '"></div><div class="label">' + item.Title + '</div><div class="description">' + item.Country + '</div></div></a>')
    	  .appendTo( ul );
      	};
   });
  
});
