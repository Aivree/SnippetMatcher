/* Main Templates */
Template.layout.rendered = function(){
	if(location.pathname.length < 2)
		Router.go('/chat');
}
Template.desktop.rendered = function(){
	$(document).foundation();

	$('input#s').autocomplete({
		source : _contacts.find({}).map(function(i){return i.name}),
		appendTo : document.getElementById('list')
	}).data("ui-autocomplete")._renderItem = function(ul, item) {

    	var listItem = $("<li></li>")
	        .data("item.autocomplete top reveal-modal", item)
	        .append("<a>" + item.label + "</a>")
	 		.appendTo(ul);
	 	ul.addClass('f-dropdown');

    	return listItem;
	};
}
Template.mobile.rendered = function(){
	$(document).foundation();

	snap = new Snap({element:mobile, disable:'right', hyperextensible:false})
	$('input#s').autocomplete({
		source : _contacts.find({}).map(function(i){return i.name}),
		appendTo: document.getElementById('list')
		}).data("ui-autocomplete")._renderItem = function(ul, item) {

    	var listItem = $("<li></li>")
	        .data("item.autocomplete", item)
	        .append("<a>" + item.label + "</a>")
	        .appendTo(ul);
	    ul.addClass('f-dropdown');
    	
    	return listItem;
	}

}