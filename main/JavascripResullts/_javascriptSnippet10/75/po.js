$(document).ready(function() {
    alert("sucks");
    $('#suppliercode').autocomplete({
        source: '<g:createLink controller="supplier" action="ajaxSupplier"/>',
        focus: function( event, ui ) {
        $( "#project" ).val( ui.item.suppliercode );
        return false;
        },
        select: function( event, ui ) {
        $( "#suppliercode" ).val( ui.item.suppliercode );
        $( "#suppliername" ).val( ui.item.suppliername );
        var prodrow = ui.item.product.length;
        var prod = ui.item.product;
        var htm="";
        for(var i=0;
            i<prodrow;
            i++) {
        htm+='<tr><td id="plu'+i+'">'+prod[i].plu+'</td><td id="desc'+i+'">'+prod[i].desc+'</td><td><g:field type="number" id="qty'+i+'" name="cost'+i+'" class="qty" onchange="filltcost(\'+i+\')" /></td><td id="cost'+i+'">'+prod[i].cost+'</td><td id="tcost'+i+'"></td></tr>';
        }
        $("#orderdetail-tbody").html(htm);
        return false;
        }
        }).data( "autocomplete" )._renderItem = function( ul, item ) {
        return $( "<li></li>" )
        .data( "item.autocomplete", item )
        .append( "<a>" + item.suppliercode + " - " + item.suppliername + "</a>" )
        .appendTo( ul );
    };
}); 
                
function filltcost(parms) {
    $("#tcost"+parms).text(($("#qty"+parms).val())*($("#cost"+parms).text()));
}
 


