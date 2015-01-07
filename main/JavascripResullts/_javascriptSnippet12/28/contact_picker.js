$.fn.extend({
  contactPicker: function() {
    this.autocomplete({
      minLength: 0,
      source: $("input.contact_picker").data('contacts'),
      focus: function (event, ui) {
        $(this).val(ui.item.label);
        $(this).nextAll('input').val(ui.item.value);
        return false;
      },
      select: function(event, ui) {
        $(this).val(ui.item.label);
        $(this).nextAll('input').val(ui.item.value);
        return false;
      }
    }).data("ui-autocomplete")._renderItem = function(ul, item) {
      return $("<li>")
        .append("<a><img class='profile-icon' src='" + item.gravatar_url + "'/>" + item.label + "</a>")
        .appendTo(ul);
    };
  }
});
