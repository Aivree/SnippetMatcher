var table = function() {
  var checkLoad = function() {   
    if (document.readyState !== "complete") {
      setTimeout(checkLoad, 11);
    } else {
      createTable();
      var anchors = document.getElementsByTagName('a');
      document.addEventListener('click', editRowCell, false);
    }
  };  

  var createTable = function(){
    var div = document.getElementById("tableParent"),
        table = document.createElement('table'),
        columnCount = 5,
        rowCount = 5;

    // create TRS
    for (var i = 0; i < rowCount; i++) {
      var row = document.createElement('tr');
      for (var j = 0; j < columnCount; j++) { createRowCell(i, row, j); }
      createTableRow(table, i, row);
    }
    div.appendChild(table);
  };

  var createTableRow = function(table, i, row) {
    table.appendChild(row);
    table.className = "table table-bordered";
  }

  var createRowCell = function(i, row, j) {
    var cell = document.createElement('td');
    var a = document.createElement('a');
    a.innerHTML = j;
    a.href = "#";
    cell.appendChild(a);
    row.appendChild(cell);
  };

  var editRowCell =  function(e){
    // variables
    var text =  e.target.innerHTML;
    var input = document.createElement('input');
    var btn = document.createElement('button');
    btn.className = "save_change";
    var parent = e.target.parentNode;
    // create input with button
    input.setAttribute("type", "text");
    input.setAttribute("value", text);
    // remove a tag (no best solution)
    var a = e.target.parentNode.childNodes[0]
    e.target.parentNode.removeChild(a)
    // replace with input
    parent.appendChild(input);
  };


  // Start App = checkLoad
  checkLoad();  
}

// avoiding polluting global namespace
table();