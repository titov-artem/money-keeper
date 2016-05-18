function buildRow(category) {
    var row = $(templates.applyTemplate('category-row.ftl', JSON.stringify(category)));
    row.prop('source', category);
    return row;
}

function currentRow(child) {
    return $(child).parents('.category-row');
}

$(document).ready(function () {
    var categories = JSON.parse(endpoint.get("/category", []));
    categories.forEach(function (category) {
        $('#categories').append(buildRow(category));
    });

    $('.show-stores').click(function (e) {
        e.preventDefault();
        var $this = $(this);
        if ($this.prop('closed') === false) {
            $this.text('Show stores »');
            $this.prop('closed', true);
        } else {
            $this.text('Hide stores «');
            $this.prop('closed', false);
        }
        var collapse = $this.parent().find('.stores');
        collapse.collapse('toggle');
    });
});