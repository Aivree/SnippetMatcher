$(function() {

    var projects = [
      {
        value: "pvalue",
        label: "plabel",
        desc: "pdollo",
        icon: "pollo.jpg"
      },
      {
        value: "pvcebolla",
        label: "plcebolla",
        desc: "pdcebolla",
        icon: "cebolla.jpg"
      },
      {
        value: "pvajo",
        label: "plajo",
        desc: "pdajo",
        icon: "ajo.jpg"
      }
    ];
 
    $( "#project" ).autocomplete({
      minLength: 0,
      source: projects,
      focus: function( event, ui ) {
        $( "#project" ).val( ui.item.label );
        return false;
      },
      select: function( event, ui ) {
        $( "#project" ).val( ui.item.label );
        $( "#project-description" ).html( $( "#project-description" ).html() + " <br> " + ui.item.label );
        return false;
      }
    })
    .data( "ui-autocomplete" )._renderItem = function( ul, item ) {
      return $( "<li>" )
        .append( "<div class='item'>" )
        .append( "<a>" + '<img class="icono" width="32px" height="32px" src="img/'+item.icon+'"/>' + item.label + "</a>" )
        .append( "</div>" )
        .appendTo( ul );
    };
    
	$("#clearAutocomplete").click(function() {
		$('#project-description').html('');
	});
    
  });