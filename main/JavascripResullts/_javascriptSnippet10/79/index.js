module.exports = function( bs ) {
    return ({
        __render: function(app) 
        {
            
        },
        __ready: function( $ )
        {
            var cache = {};

            $( "input[data-search-book]" ).autocomplete
            ({
                minLength: 2,

                source: function( request, response ) 
                {
                    if ( cache[ request.term ] ) 
                    {
                      response( cache[ request.term ] );
                      return;
                    }

                    $.socket.once( 'results', function( data )
                    {
                        response( cache[ request.term ] = data );
                    });

                    $.socket.emit( 'search', request.term );
                },

                select: function( event, ui ) 
                {
                    window.location.href = '/book/' + ui.item.value

                    return false;
                }
            })
            .autocomplete( "instance" )._renderItem = function( ul, item ) 
            {
              return $( "<li>" )
                .append( '<img src="'+item.icon+'"/><a href="javascript:void(0)">' + item.label + '<br><span>' + item.desc + '</span></a>' )
                .appendTo( ul );
            };
        },
        events : 
        ({

            'search'       : function( criteria )
            {
                var query   = bs.mysql.query( 'SELECT book_price AS price, book_isbn AS value, book_title AS label, CONCAT( LEFT( book_description, 50 ), "..." ) AS `desc`, CONCAT( "data:image/jpeg;base64,", book_image ) AS icon FROM v_books WHERE book_isbn like "' + criteria + '%" OR book_title like "%' + criteria + '%" OR book_author like "%' + criteria + '%" OR book_category like "%' + criteria + '%";' );
                
                if( !query.error )
                    this.emit( 'results', query.result );
            }
        })
    })
};
