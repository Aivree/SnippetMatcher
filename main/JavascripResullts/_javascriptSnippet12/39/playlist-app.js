$(document).ready(function () {
    $('input[type="text"]')
        .focus()
        .on('keypress', function (e) {
            if (e.which !== 0) {
                $('#background-text').hide();
            }
        })
        .on('keyup', function (e) {
            if ($(this).val() == '') {
                $('#background-text').show();
            }
        })
        .autocomplete({
            source: 'http://localhost:51788/search',
            minLength: 2
        })
        .data("ui-autocomplete")._renderItem = function (ul, item) {
            
            return $('<li>').append('<li>' + item.track[0].title + '</li>').appendTo(ul);
        };
    $('input[type="submit"]')
        .on('mouseenter', function (event) {
            $(this).addClass('focus');
        })
        .on('mouseleave', function (event) {
            $(this).removeClass('focus');
        });

    $("#jquery_jplayer_1").jPlayer();
});