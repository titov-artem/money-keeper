function getUnionModal() {
    return $('.category-union-modal');
}

function getSelectedCategories() {
    var modal = getUnionModal();
    var curCategoryName = modal.prop('row').prop('source').name;
    var categories = modal.find('.category-chooser').selectpicker('val');
    if (categories == null) return [curCategoryName];
    categories.push(curCategoryName);
    return categories;
}

$(document).ready(function () {
    $('body').on('click', '.category-union', function () {
        var categories = $('#categories').prop('source');
        var row = currentRow(this);
        var categoryName = row.prop('source').name;
        var modal = getUnionModal();
        modal.prop('row', row);
        modal.find('.category-chooser-container').remove();
        modal.find('.modal-body').append($(templates.applyTemplate('category-chooser.ftl',
            JSON.stringify({
                categories: categories,
                multiple: true,
                width: '100%'
            })
        )));
        modal.find('.category-chooser option[value="' + categoryName + '"]').attr('disabled', 'true');
        var categoryChooser = modal.find('.category-chooser');
        categoryChooser.selectpicker('render');
        categoryChooser.selectpicker('val', [categoryName]);
        modal.modal('show');
    }).on('click', '.category-union-apply', function () {
        var modal = getUnionModal();
        var categories = getSelectedCategories();
        if (categories.length < 2) {
            showAlert2(modal.find('.alerts'), 3000, 'danger', 'You must select at least two categories to union', 'category-too-less');
            return;
        }
        var name = $('.category-union-name').val();
        var nameCorrect = (endpoint.post("/category/check/name", [name, JSON.stringify(categories)]) === 'true');
        if (!nameCorrect) {
            showAlert2(modal.find('.alerts'), 3000, 'danger', 'Bad category name (must be not empty and unique)', 'category-wrong-name');
            return;
        }
        JSON.parse(endpoint.post("/category/union", [name, JSON.stringify(categories)]));
        reloadPage();
    });
});