$(document).ready(function() {
  var availableTags = ["c++", "java", "php", "coldfusion", "javascript", "asp", "ruby", "python", "c", "scala", "groovy", "haskell", "perl"];
  $("#box input[type='text']").autocomplete({
    source: "/courses.json",
    minLength: 2,
    select: function(event, ui) {
      window.location = "/courses/"+ui.item.id+"/exams/"
    }
  }).data("autocomplete")._renderItem = function(ul, item) {
    item.label = item.label.replace(eval('/('+this.term+')/gi'),'<span class="hilight">$1</span>');
    return $("<li></li>")
         .data( "item.autocomplete", item)
         .append( "<a>" + item.label + "</a>")
         .appendTo(ul);
  };
});
