prev_search_string = '';

function remove_recipient(link) {
  $(link).closest('li').find("input[type=hidden]").first().remove();
  $(link).closest('li').fadeOut();  
  return false;
}

function recipients_autocomplete_desktop(recipient_fields_tpl) {
  $.widget( 'ui.autocomplete', $.ui.autocomplete, {
    _renderMenu: function( ul, items ) {
      var self = this,
        currentCategory = "";
      $.each( items, function( index, item ) {
        if ( item.category != currentCategory ) {
          ul.append( "<li class='ui-autocomplete-category'>" + item.category + "</li>" );
          currentCategory = item.category;
        }
        self._renderItem( ul, item );
      });
    }
  });

  var regexp_recipient = new RegExp("new_recipient", "g");

  $( '#recipient' ).autocomplete({
    source: '/search/audio_recipients',
    minLength: 2,
    select: function(event, ui) {
      var new_id = new Date().getTime();
      
      $('ul#recipients').append(recipient_fields_tpl.replace(regexp_recipient, new_id));
      $('li.recipient-'+new_id+' span.name').text(ui.item.name);
      $('li.recipient-'+new_id+' span.category').text(ui.item.category);
      $('li.recipient-'+new_id+' input.recipient_id').val(ui.item.id);
      $('li.recipient-'+new_id+' img').attr("src", ui.item.image);
    }
  })
  .data('ui-autocomplete')._renderItem = function( ul, item ) {
    return $('<li></li>')
      .data('ui-autocomplete-item', item)
      .append('<a class="pick-member"><img src="'+item.image+'"/><span>'+item.name+'</span><br/>'+item.email+'</a>')
      .appendTo(ul);
  };
};


