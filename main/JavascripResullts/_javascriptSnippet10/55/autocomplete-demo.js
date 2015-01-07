$(function() {
    'use strict';

    var categories = [{
        value: 1,
        label: "Vehicles",
        desc: "(Root category)"
    }, {
        value: 2,
        label: "Massive Transports",
        desc: "&#9658; Vahicles"
    }, {
        value: 3,
        label: "Personal",
        desc: "&#9658; Vahicles"
    }, {
        value: 4,
        label: "BMW",
        desc: "&#9658; Vahicles &#9658; Personal"
    }, {
        value: 5,
        label: "BMW",
        desc: "&#9658; Vahicles &#9658; Massive Transports"
    }, {
        value: 6,
        label: "Electronics",
        desc: "(Root category)"
    }, {
        value: 7,
        label: "Personal",
        desc: "&#9658; Electronics"
    }];

    var categoryInput = $('#category-input');
    categoryInput.autocomplete({
        minLength: 0,
        source: categories,
        focus: function (event, ui) {
            //categoryInput.val(ui.item.label);
            return false;
        },
        select: function(event, ui) {
            
            categoryInput.val(ui.item.label);
            $("#category-id").val(ui.item.value);

            return false;
        }
        }).data('autocomplete')._renderItem = function(ul, item) {
            var itemMarkup = '<a href="#">' + item.label + '<br/><span class="ui-autocomplete-item-desc">' + item.desc + '</span></a>';
            return $('<li></li>')
                .data('item.autocomplete', item)
                .append(itemMarkup)
                .appendTo(ul);
    };
});