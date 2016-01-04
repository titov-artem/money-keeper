function getSelectedCategories() {
    var categories = [];
    var selectedRows = $('.category-union-checkbox input[type="checkbox"]:checked');
    selectedRows.each(function () {
        categories.push(currentRow(this).prop('source'));
    });
    return categories;
}

function removeSelectedCategories() {
    $('.category-union-checkbox input[type="checkbox"]:checked').each(function () {
        currentRow(this).remove();
    });
}

function resetUnionControls() {
    $('.category-union-name-error').addClass('hidden');
    $('.category-union-checkbox').addClass('hidden');
    $('.category-union-control').addClass('hidden');
    $('.category-control').removeClass('hidden');
    $('.category-union-checkbox input[type="checkbox"]').each(function () {
        $(this).prop('checked', false);
    });
    $('.category-union-no-selection-error').addClass('hidden');
    $('.category-union-name').val('');
}

$(document).ready(function () {
    $('body').on('click', '.category-union', function () {
        $('.category-union-checkbox').removeClass('hidden');
        $('.category-union-control').removeClass('hidden');
        $('.category-control').addClass('hidden');
    }).on('click', '.category-union-apply', function () {
        var categories = getSelectedCategories();
        var name = $('.category-union-name').val();
        var nameCorrect = (endpoint.post("/category/check/name", [name, JSON.stringify(categories.map(function (e) {
            return e.name;
        }))]) === 'true');
        if (!nameCorrect) {
            $('.category-union-name-error').removeClass('hidden');
            return;
        } else {
            $('.category-union-name-error').addClass('hidden');
        }
        var unionCategory = JSON.parse(endpoint.post("/category/union", [name, JSON.stringify(categories)]));
        var unionCategoryRow = buildRow(unionCategory);
        removeSelectedCategories();
        var inserted = false;
        $('.category-row').each(function () {
            if (!inserted && $(this).prop('source').name.localeCompare(unionCategory.name) > 0) {
                $(this).before(unionCategoryRow);
                inserted = true;
            }
        });
        if (!inserted) {
            $('#categories').prepend(unionCategoryRow);
        }
        resetUnionControls();
        $('.category-union-modal').modal('hide');
    }).on('click', '.category-union-cancel', function () {
        resetUnionControls();
    });
    $('.category-union-modal').on('show.bs.modal', function (e) {
        var button = e.relatedTarget;
        if ($(button).hasClass('category-union-open-modal')) {
            var categories = getSelectedCategories();
            var selectionErrorAlert = $('.category-union-no-selection-error');
            if (categories.length < 2) {
                selectionErrorAlert.removeClass('hidden');
                e.stopPropagation();
                return false;
            } else {
                selectionErrorAlert.addClass('hidden');
            }
        }
    });
});