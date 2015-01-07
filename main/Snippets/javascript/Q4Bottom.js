var autoComplete = input.data("ui-autocomplete");
if(typeof(autoComplete) == "undefined")
     autoComplete = input.data("autocomplete");

autoComplete._renderItem = function(ul, item) {