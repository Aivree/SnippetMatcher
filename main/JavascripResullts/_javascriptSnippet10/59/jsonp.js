function addMeshSearch() {
	var source = function(request, response) {
			$.ajax({
				url: "http://192.168.8.20:3000/jsonp/" + request.term,
				dataType: "jsonp",
				success: function(data) {
					response($.map(data && data.data || [], function(item) {
						var label = item.DescriptorName.String + '[' + (item.zh && item.zh.String) + ']';
						var tree = '';
						for(var i = 0; i < item.TreeNumberList.length; i++) {
							tree = tree + '<br>' + item.TreeNumberList[i] + ' '
						}
						return {
							label: label,
							value: item.DescriptorName.String,
							desc: tree
						}
					}));
				}
			});
		}
	$('#jsonp').autocomplete({
		source: source,
		minLength: 2,
		select: function(event, ui) {
			$('a').html(ui.item.value).attr('href','tree/' + ui.item.value);
		}
	});
	/*.data( "autocomplete" )._renderItem = function( ul, item ) {
				return $( "<li></li>" )
					.data( "item.autocomplete", item )
					.append( "<a>" + item.label + "<span style=\"color:orange;\">" + item.desc + "</span></a>" )
					.appendTo( ul );
			};*/
}
$(function() {
	addMeshSearch();
});