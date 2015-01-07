define(
    ["jquery", "module",
        "text!css/third/jquery-ui-1.8.23.custom.css",
        "third/jquery-ui-1.8.23.custom.min"],
    function ($, module, css) {

        var profileUrl = module.config().webappRoot + "profile-{id}.html";

        return {
            init: function (searchInput) {
                $('head').append('<style>'+css+'</style>');
                $(searchInput).autocomplete({
                    source: 'search.json',
                    minLength: 2,
                    select: function(event, ui) {
                        $(location).attr('href',profileUrl.replace('{id}', ui.item.id));
                    }
                })
                .data( "autocomplete" )._renderItem = function( ul, item ) {
                    return $("<li></li>")
                        .data("item.autocomplete", item)
                        .append("<a>" + item.firstName + " " + item.lastName + "</a>")
                        .appendTo(ul);
                };
            }
        }
});