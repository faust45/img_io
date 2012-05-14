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
        $("#images").append("<img src='/img/"+id+"?height="+height+"&width="+width+"' />");
    }
}

