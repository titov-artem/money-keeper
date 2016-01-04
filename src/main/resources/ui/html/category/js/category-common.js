function buildRow(category) {
    var row = $(templates.applyTemplate('category-row.ftl', JSON.stringify({
        name: category.name,
        alternatives: category.alternatives
    })));
    row.prop('source', category);
    return row;
}

function currentRow(child) {
    return $(child).parents('.category-row');
}

$(document).ready(function () {
    var categories = JSON.parse(endpoint.get("/category", []));
    categories.forEach(function (category) {
        $('#categories').append(buildRow(category))
    });
});