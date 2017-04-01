function getChangeCategoryModal() {
    return $('.store-change-category-modal');
}

function currentStore(child) {
    return $(child).parents('.category-store').attr('store');
}

$(document).ready(function () {
    $('body').on('click', '.store-change-category', function () {
        var categories = $('#categories').prop('source');
        var row = currentRow(this);
        var categoryName = row.prop('source').name;
        var modal = getChangeCategoryModal();
        modal.prop('store', currentStore(this));
        modal.find('.category-chooser-container').remove();
        modal.find('.modal-body').append($(templates.applyTemplate('category-chooser.ftl',
            JSON.stringify({
                categories: categories,
                width: '100%'
            })
        )));
        var categoryChooser = modal.find('.category-chooser');
        categoryChooser.selectpicker('render');
        categoryChooser.selectpicker('val', [categoryName]);
        modal.modal('show');
    }).on('click', '.store-change-category-apply', function () {
        var modal = getChangeCategoryModal();
        var category = modal.find('.category-chooser').selectpicker('val');
        endpoint.post('/store/category', [modal.prop('store'), category]);
        reloadPage();
    });
});