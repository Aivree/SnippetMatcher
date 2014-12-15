/**
 * Created with IntelliJ IDEA.
 * User: gregorytkach
 * Date: 9/4/13
 * Time: 12:37 PM
 * To change this template use File | Settings | File Templates.
 */

function getParameters()
{
    var result = {};

    var searchString = window.location.search.substring(1);
    var params = searchString.split("&");

    if (searchString != "")
    {
        for (var i = 0; i < params.length; i++)
        {
            var val = params[i].split("=");
            result[decodeURIComponent(val[0])] = decodeURIComponent(val[1]);
        }
    }

    return result;
}