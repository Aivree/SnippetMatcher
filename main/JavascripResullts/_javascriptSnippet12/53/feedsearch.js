google.load("feeds", "1");

results = [];
function findFeed(query){
	google.feeds.findFeeds(query, findSuccess);
	return results;
}

function findSuccess(result){
	results = [];
	if (result.entries){
		for (var i = 0; i < result.entries.length; i++){
			var entry = result.entries[i];
			results.push({
				label: entry.title,
				value: entry.url
			});
		}
	}
	$("search_query").autocomplete("search", "");
}

function initAutocomplete(){
	$("#search_query").autocomplete({
		source: function( request, response ){
			response(results);
		},
		delay: 1000
	})
	.data("ui-autocomplete")._renderItem = function( ul, item ){
		return $( "<li>" )
			.append("<a class='search_result'>" + item.label + "</a>")
			.appendTo(ul);
	};
	var find = function() { findFeed($("#search_query").val()); };
	$("#search_query").on('keyup input', _.debounce(find , 100));
}