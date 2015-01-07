/**
 * Client side code for username autocomplete.
 */

jQuery(document).ready( function() {

    function format_user( user ) {
	return user.name + ' (' + user.login + ')';
	}

    jQuery( "#username-input" ).autocomplete( {
	minLength : 2,
	source : 'http://' + window.location.hostname + '/autocomplete/username',
	focus : function( event, ui ) {
	    jQuery( "#username-input" ).val( format_user( ui.item ) );
	    return false;
	},
	select : function( event, ui ) {
	    jQuery( "#username-input" ).val( format_user( ui.item ) );
	    jQuery( "#user-id-input" ).val( ui.item.id );
	    return false;
	}
    } )
	.data( "autocomplete" )._renderItem = function( ul, item ) {
	    return $( "<li>" )
		.append( "<a>" + format_user( item ) + "</a>" )
		.appendTo( ul );
	};
} );
	    
