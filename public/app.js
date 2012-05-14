$(function() {
    $.ajax({
        url: "/all",
        type: "GET",
        dataType: "json",
        success: renderImages
    });
});

function renderImages(data, statusText) {
    var height = 200, width = 200;

    for(var i in data) {
        var id = data[i];
        var linkToOriginal = $("<a href='/img/"+id+"'> </a>"),
            thumb = "<img class='thumb' src='/img/"+id+"?height="+height+"&width="+width+"' />";
        linkToOriginal.append(thumb); 
        $("#images").append(linkToOriginal);
    }
}

