$(function() {

	$("#main-nav li a, .date").append("<span>&nbsp;</span>");
	
	$("#main-nav li a").hover(function(e) {
	   $(this)
	       .hoverFlow(e.type, { width: "230px" }, 200)
	       .css('overflow', 'visible')
	       .find("span")
	       .hoverFlow(e.type, { width: "15px" }, 200)
	       
	}, function(e) {
	   $(this)
	       .hoverFlow(e.type, { width: "215px" }, 200)
	       .css('overflow', 'visible')
	       .find("span")
	       .hoverFlow(e.type, { width: "20px" }, 200)
	});
	
	var projects = [
		{
			value: 'Mega Sprocketizer',
			label: 'Mega Sprocketizer',
			desc: 'The biggest spocket of all',
			icon: '/product-images/prod-sprocketizer-mini.png'
		},
		{
			value: 'Super Sprocket 1000',
			label: 'Super Sprocket 1000',
			desc: 'Our bestseller',
			icon: '/product-images/prod-sprunkler-mini.png'
		},
		{
			value: 'Triple Sprunkler',
			label: 'Triple Sprunkler',
			desc: 'When you need three.',
			icon: '/product-images/prod-supersprocket-mini.png'
		}
	];
		
	$('#s').autocomplete({
		minLength: 0,
		source: projects
	})
	.data( "autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li></li>" )
			.data( "item.autocomplete", item )
			.append( "<a><img class='mini' src='" + item.icon + "' />" + item.label + "<br /><small>" + item.desc + "</small></a>" )
			.appendTo( ul );
	};

});
