$(document).ready(function () {
    $('body').on('click', '.category-rename', function () {
        var row = currentRow(this);
        row.find('h4.category-name').addClass('hidden');
        row.find('.category-menu').addClass('hidden');
        row.find('.category-name-form').removeClass('hidden');
        row.find('.category-save-control').removeClass('hidden');
    }).on('click', '.category-save', function () {
        var row = currentRow(this);
        var oldName = JSON.parse(row.attr('source')).name;
        var newName = row.find('input.category-name').val();
        var nameCorrect = (endpoint.post("/category/check/name", [newName, JSON.stringify([oldName])]) === 'true');
        if (!nameCorrect) {
            $('.category-name-form .form-group').addClass('has-error');
            $('.category-save-error-label').removeClass('hidden');
            return;
        }
        var saved = JSON.parse(endpoint.post("/category/rename", [oldName, newName]));
        var newRow = buildRow(saved);
        row.replaceWith(newRow);
    }).on('click', '.category-save-cancel', function () {
        var row = currentRow(this);
        var source = row.attr('source');
        var originalRow = buildRow(JSON.parse(source));
        row.replaceWith(originalRow);
    });
});