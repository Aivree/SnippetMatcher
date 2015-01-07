/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
        $(function() {
		function log( message ) {

                        $( "#log" ).val( message  );
		}

    $("#surname_surname").click(function () {
        $( "#searchinput" ).val("");
        log ("");
        $( "#pgo-id" ).val(-1)
        tt();
    });

    $("#surname_address").click(function () {
        $( "#searchinput" ).val("");
        log ("");
        $( "#pgo-id" ).val(-1)
        tt();
    });

    $("#surname_number").click(function () {
        $( "#searchinput" ).val("");
        log ("");
        $( "#pgo-id" ).val(-1)
        tt();
    });
    
   $(tt = function() {
      var rSur = $('#surname_surname').is(':checked');
      var rAdre = $('#surname_address').is(':checked');
      var rPgo = $('#surname_number').is(':checked');

 if (rSur) {
		$( "#searchinput" ).autocomplete({
                                 minLength: 2,
             delay: 400,
                        search: function(event, ui) { 
                        $( "#pgo-id" ).val(-1)
                    
                },   
			source: function( request, response ) {
				$.ajax({
					url: "/allpgo/searchsur",
					dataType: "jsonp",
					data: { query: request.term,
		
						maxRows: 12,
						name_startsWith: request.term
					},
					success: function( data ) {
                                      if ( $.isEmptyObject(data)) {log ("Жодний запис не містить вашого запросу")}
                                      else {log ("")};
                                     

						response( $.map( data, function( item ) {
							return {
								label: item.surname +" " + item.name +" " +item.midname,
								value: item.surname +" " + item.name +" " +item.midname,
                                                                desc:  item.fulladdres + ", " + item.num_1  + "-" + item.num_2,
                                                                id: item.id
							}
						}));
					}
				});
			},
			minLength: 2,
			select: function( event, ui ) {
                            $( "#pgo-id" ).val( ui.item.id );

			},
			open: function() {
                            
				$( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
                                
			},
			close: function( event, ui) {
				
                              if ($( "#pgo-id" ).val() == "-1") 
                                  { log ("Жодний запис не обрано")
                                  }
                                  else 
                                  { log ("") } ;
                               $( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
			}
		})    		.data( "autocomplete" )._renderItem = function( ul, item ) {
			return $( "<li></li>" )
				.data( "item.autocomplete", item )
				.append( "<a>" + item.label + "<div class='comment'>" + item.desc + "</div></a>" )
				.appendTo( ul );
		};    
      
                
                
	};
   
   
   if (rAdre) {
       
		$( "#searchinput" ).autocomplete({
                                 minLength: 4,
             delay: 400,
                        search: function(event, ui) { 
                        $( "#pgo-id" ).val(-1)
                    
                },  
                source: function( request, response ) {
				$.ajax({
					url: "/allpgo/searchadre",
					dataType: "jsonp",
					data: { query: request.term,
		
						maxRows: 12,
						name_startsWith: request.term
					},
					success: function( data ) {
                     if ( $.isEmptyObject(data)) {log ("Жодний запис не містить вашого запросу")}
                                      else {log ("")};

						response( $.map( data, function( item ) {
							return {
								label:item.fulladdres ,
								value: item.adrquest ,
                                                                desc: item.surname +" " + item.name +" " +item.midname + ', ' + item.num_1  +"-" + item.num_2 ,
                                                                  id: item.id
							}
						}));
					}
				});
			},
			minLength: 4,
			select: function( event, ui ) {
                            $( "#pgo-id" ).val( ui.item.id );

			},
			open: function() {
				$( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
			},
			close: function() {
                            				
                              if ($( "#pgo-id" ).val() == "-1") 
                                  { log ("Жодний запис не обрано")
                                  }
                                  else 
                                  { log ("") } ;
				$( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
			}
                        
                        
                        
		})
            
    		.data( "autocomplete" )._renderItem = function( ul, item ) {
			return $( "<li></li>" )
				.data( "item.autocomplete", item )
				.append( "<a>" + item.label + "<div class='comment'>" + item.desc + "</div></a>" )
				.appendTo( ul );
		};    
      
                
                
	};

if (rPgo) {
		$( "#searchinput" ).autocomplete({
                                 minLength: 1,
             delay: 400,
                        search: function(event, ui) { 
                        $( "#pgo-id" ).val(-1)
                    
                },               
			source: function( request, response ) {
				$.ajax({
					url: "/allpgo/searchnum",
					dataType: "jsonp",
					data: { query: request.term,
		
						maxRows: 12,
						name_startsWith: request.term
					},
					success: function( data ) {
                     if ( $.isEmptyObject(data)) {log ("Жодний запис не містить вашого запросу")}
                                      else {log ("")};
                                            
						response( $.map( data, function( item ) {
							return {
								label: item.num_1  +"-" + item.num_2, 
								value: item.num_1  +"-" + item.num_2,
                                                                desc: item.surname +" " + item.name +" " +item.midname  + ", "  + item.fulladdres,
                                                                id: item.id                                                                
							}
						}));
					}
				});
			},
			minLength: 1,
			select: function( event, ui ) {
                                   $( "#pgo-id" ).val( ui.item.id );

			},
			open: function() {
				$( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
			},
			close: function() {
                            				
                              if ($( "#pgo-id" ).val() == "-1") 
                                  { log ("Жодний запис не обрано")
                                  }
                                  else 
                                  { log ("") } ;
				$( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
			}
		})    		.data( "autocomplete" )._renderItem = function( ul, item ) {
			return $( "<li></li>" )
				.data( "item.autocomplete", item )
				.append( "<a>" + item.label + "<div class='comment'>" + item.desc + "</div></a>" )
				.appendTo( ul );
		};    
      
                
                
	};
});
        
        });  

