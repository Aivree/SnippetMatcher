	function GetUrlValue(VarSearch){  
			  var SearchString = window.location.search.substring(1);  
			  var VariableArray =SearchString.split('&');  
			  for(var i = 0; i < VariableArray.length;i++){      
			  var KeyValuePair =VariableArray[i].split('=');    
			    if(KeyValuePair[0] == VarSearch){         
			  return KeyValuePair[1];    
			    }  
			  }
			}


//example in javascript
//alert(GetUrlValue('yoururlvariablename'));
//or
//var variable1 =  GetUrlValue('yoururlvariablename');
