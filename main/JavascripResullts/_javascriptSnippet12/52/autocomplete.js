// Auto suggest customization
$.widget("custom.catAutocomplete", $.ui.autocomplete, {
    _renderMenu: function(ul, items) {
        var that = this, 
            currentCategory = "";
        $.each(items, function(index, item) {
            if (item.category != currentCategory) {
                ul.append("<li class='ui-autocomplete-category'>"+item.category+"</li>");
                currentCategory = item.category;
            }
            that._renderItemData(ul, item);
        });
    },

    _renderItem: function(ul, item){
        var re = new RegExp("^" + this.term, "i") ;
        var t = item.label.replace(re,"<span style='color:#b00;'>"+this.term+"</span>");
        return $( "<li></li>" )
                                .data( "item.autocomplete", item )
                                .append( "<a>" + t + "</a>" )
                                .appendTo( ul );
    }
});

// Initialize autocomplete with categories
$('#search_input').catAutocomplete({
    
    minLength: 1,
    
    // Remote source definition
    source: function(request, response) {
        // console.log(request)
        $.ajax({
            type:'GET',
            // url: 'http://localhost:8080/get_stocks.php?q='+$('#search_div').children('input').val(),
            // url: '../get_stocks.php?q='+$('#search_div').children('input').val(),
            url: '../get_stocks.php?q='+request.term,
            delay: 1
        })
        .done ( function(data){
            // console.log(data)
            response($.map(JSON.parse(data), function(item) {
                return {
                    label: item.symbol,
                    category: 'Symbol'
                }
            }));
        })
    }
});