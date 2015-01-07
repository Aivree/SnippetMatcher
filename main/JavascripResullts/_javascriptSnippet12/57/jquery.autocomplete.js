/* jQuery UI Autocomplete Extensions by Scott Gonz‡lez */

(function($) {
	var proto = $.ui.autocomplete.prototype, initSource = proto._initSource;
	function filter(array, term) {
		var matcher = new RegExp($.ui.autocomplete.escapeRegex(term), "i");
		return $.grep(array, function(value) {
			return matcher.test($("<div>").html(value.label||value.value||value).text());
		});
	}
	$.extend(proto, {
		_initSource: function() {
			if (this.options.html && $.isArray(this.options.source)) {
				this.source = function(request, response) {
					response(filter(this.options.source, request.term));
				};
			} else {
				initSource.call(this);
			}
		},
		_renderItem: function(ul, item) {
			return $("<li></li>").data("item.autocomplete", item).append($("<a/>")[this.options.html ? "html" : "text"](item.label)).appendTo(ul);
		}
	});
})(jQuery);

(function($) {
	$(".ui-autocomplete-input").live("autocompleteopen", function() {
		var autocomplete = $(this).data("autocomplete");
		var menu = autocomplete.menu;
		if (!autocomplete.options.selectFirst) {
			return;
		}
		menu.activate($.Event({ type: "mouseenter" }), menu.element.children().first() );
	});
})(jQuery);