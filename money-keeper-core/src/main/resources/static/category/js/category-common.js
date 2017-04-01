function buildRow(category) {
    var row = $(templates.applyTemplate('category-row.ftl', JSON.stringify(category)));
    if (category.stores.length != 0) {
        row.find('.category-delete').addClass('hidden');
    }
    row.prop('source', category);
    return row;
}

function currentRow(child) {
    return $(child).parents('.category-row');
}

$(document).ready(function () {
    var categories = JSON.parse(endpoint.get("/category", []));
    $('#categories').prop('source', categories);
    categories.forEach(function (category) {
        $('#categories').append(buildRow(category));
    });
    $('#categories-count').text(categories.length);

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