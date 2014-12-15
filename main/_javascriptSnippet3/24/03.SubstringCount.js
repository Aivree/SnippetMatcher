// 3.Write a JavaScript function that finds how many times a substring is contained
// in a given text (perform case insensitive search).

window.onload = function () {
    document.getElementById("input1").focus();
};

function substringCount(text, substring) {
    var substrings = text.toLowerCase().split(substring.toLowerCase());       //the text is splitted by the search substring
    return substrings.length - 1;                 //the number of pieces after the split minus one are the count of the occurances of the searched substring
}

function substringCountTest() {      //tests substringCount() function
    var text = jsConsole.read("input#input1"),
        substring = jsConsole.read("input#input2");
    jsConsole.writeLine("The substring(case-insensitive search): &quot" + substring + "&quot is found " + substringCount(text, substring) + (substringCount(text, substring) === 1 ? " time." : " times."));
}

function onEnterKeyPressed(event) {
    if (event.keyCode === 13) {
        if (document.activeElement === document.getElementById("input1")) {
            document.getElementById("input2").focus();
        } else {
            substringCountTest();
        }
    }
}