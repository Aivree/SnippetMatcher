function autocomplete_select(inputid,rnodeid,namevar,options) {
		$('#'+inputid).autocomplete({
			minLength: 0,
			source: options,
			focus: function(event, ui) {
				$('#'+inputid).val(ui.item.label);
				return false;
			},
			select: function(event, ui) {
				$('#'+inputid).val('');
				//$('#'+rnodeid).append('<div><img src="' + ui.item.icon + '">'+ ui.item.label + ' <input value="'+ ui.item.value +'" type="hidden" name="'+ namevar +'[]" > <a href="#" onclick="$(this.parentNode).remove()">X</a></div>');
				$('#'+rnodeid).append('<div>'+ ui.item.label + ' <input value="'+ ui.item.value +'" type="hidden" name="'+ namevar +'[]" > <a href="#" onclick="$(this.parentNode).remove()">X</a></div>');
				return false;
			}
		})
		.data( "autocomplete" )._renderItem = function( ul, item ) {
			return $( "<li></li>" )
				.data( "item.autocomplete", item )
				.append( "<a><img src='" + item.icon + "'> " + item.label + "</a>" )
				.appendTo( ul );
		};

	};
