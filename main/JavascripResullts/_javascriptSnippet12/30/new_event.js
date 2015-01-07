$(document).ready(function(){
  $('#events_forms').tabs();
  $('#organizations_autocomplete').autocomplete({
    source: autocomplete_organizations_path,
    select: function(event, ui){
      $('#new_organization_relations').trigger('click');
      $('.organization_name:last').html(ui.item.label);
      $('.organization_relation_id:last').val(ui.item.value.id);
      $('#organizations_autocomplete').val(ui.item.value);
      return false;
    },
    close: function(event, ui){
      $('#organizations_autocomplete').val('');
    },
    focus: function(event, ui){
      $('#organizations_autocomplete').val(ui.item.label);
      return false;
    }
  }).data( "autocomplete" )._renderItem = function( ul, item ) {
    return $( "<li></li>" )
    .data( "item.autocomplete", item )
    .append( "<a><b>" + item.label + "</b><br><small>" + item.value.full_name + "</small></a>" )
    .appendTo( ul );
  };
  
  $('.addresses_autocomplete').autocomplete({
    source: autocomplete_addresses_path,
    select: function(event, ui){
      var input = $(event.target);
      console.log()
      input.parent().find('input.address_object_id').val(ui.item.value.id);
      input.parent().find('input.address_object_type').val(ui.item.value.class_name);
      input.val(ui.item.label);
      input.parent().find('.address').html(ui.item.label);
      show_address(input);
      return false;
    },
    close: function(event, ui){
      $(event.target).val('');
    },
    focus: function(event, ui){
      $(event.target).val(ui.item.label);
      return false;
    }
  }).data( "autocomplete" )._renderItem = function( ul, item ) {
    return $( "<li></li>" )
    .data( "item.autocomplete", item )
    .append( "<a>" + item.label + "</a>" )
    .appendTo( ul );
  };
  
  $('#clear_address').click(clear_address);
});

function show_address(input){
  input.hide();
  input.parent().find('.address, .clear_address').show();
}

function clear_address(){
  var parent = $(this).parent()
  parent.find('.addresses_autocomplete').val('').show();
  parent.find('.address_object_id').val('');
  parent.find('.address_object_type').val('');
  parent.find('.address, .clear_address').hide();
  return false;
}