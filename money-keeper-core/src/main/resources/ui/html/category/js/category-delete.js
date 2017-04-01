function getDeleteModal() {
    return $('.category-delete-modal');
}

$(document).ready(function () {
    $('body').on('click', '.category-delete', function () {
        var row = currentRow(this);
        var modal = getDeleteModal();
        modal.prop('row', row);
        modal.find('.category-delete-name').text(row.prop('source').name);
        modal.modal('show');
    }).on('click', '.category-delete-apply', function () {
        var modal = getDeleteModal();
        var row = modal.prop('row');
        var name = row.prop('source').name;
        var deleted = (endpoint.delete("/category", [name]) === 'true');
        if (!deleted) {
            showAlert2(modal.find('.alerts'), 3000, 'danger', 'Category must be empty', 'category-not-empty');
            return;
        }
        reloadPage();
    });
});