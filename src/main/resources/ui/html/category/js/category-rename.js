function getRenameModal() {
    return $('.category-rename-modal');
}

$(document).ready(function () {
    $('body').on('click', '.category-rename', function () {
        var row = currentRow(this);
        var modal = getRenameModal();
        modal.prop('row', row);
        modal.find('input.category-rename-name').val(row.prop('source').name);
        modal.modal('show');
    }).on('click', '.category-rename-apply', function () {
        var modal = getRenameModal();
        var row = modal.prop('row');
        var oldName = row.prop('source').name;
        var newName = modal.find('input.category-rename-name').val();
        var nameCorrect = (endpoint.post("/category/check/name", [newName, JSON.stringify([oldName])]) === 'true');
        if (!nameCorrect) {
            showAlert2(modal.find('.alerts'), 3000, 'danger', 'Bad category name (must be not empty and unique)', 'category-wrong-name');
            return;
        }
        endpoint.post("/category/rename", [oldName, newName]);
        reloadPage();
    });
});