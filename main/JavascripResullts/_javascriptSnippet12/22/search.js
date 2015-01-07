$(function() {
    $('input').autocomplete({
        autoFocus: true,
        source: function(request, response) {
            $.getJSON($SCRIPT_ROOT + '/suggestions/' + request.term, function (data) {
                _.each(data, function(result) {
                    result.value = result['name'];
                });
                response(data);
            });
        },
        select: function(event, ui) {
            window.location.href = $SCRIPT_ROOT + '/show/' + ui.item['id'];
        }
    }).data( "ui-autocomplete")._renderItem = function( ul, item) {
        var innerHtml = item['image_url'] ? '<img src="' + item['image_url'] + '">' : '';
        innerHtml += item['name'];

        return $('<li></li>')
            .data('item.autocomplete', item)
            .append(innerHtml)
            .appendTo(ul);
    };
});