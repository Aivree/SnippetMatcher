$(document).ready(function() {

  // Tag-Vervollständigung
  $(".tagsinput").each(function(e) {
    $(this).tagsInput({
      "autocomplete_url": "/holarse/api/taglist?context=" + $(this).data("context"),
      "defaultText": "Einen Tag eintragen"
    });
  });

  // Such-Vervollständigung
  if ($("#search_q").length > 0) {
    $("#search_q").autocomplete({
      "autoFocus": "false",
      "minLength": 2,
      "source": "/search/suggest.json",
      "select": function(event, ui) {
	$("#search_q").val(ui.item.title);
	window.location = ui.item.url;
	return true;
      }}).data("ui-autocomplete")._renderItem = function(ul, item) {
	console.debug(item);
	return $("<li></li>")
	  .data("item.autocomplete", item)
	  .append($.Mustache.render("autocomplete-item-template", item))
	  .appendTo(ul);
	};
  }
});
