<li class="list-group-item category-row">
    <div class="category-union-checkbox hidden">
        <div class="checkbox checkbox-success">
            <input id="${name}" class="styled" type="checkbox">
            <label for="${name}"></label>
        </div>
    </div>
    <div class="category-control">
        <div class="btn-group category-menu">
            <button class="btn btn-sm btn-default category-rename" type="button">Rename</button>
            <button class="btn btn-sm btn-default category-union" type="button">Union...</button>
        </div>
        <div class="category-save-control hidden">
            <button class="btn btn-sm btn-success category-save" type="button">Save</button>
            <button class="btn btn-sm btn-danger category-save-cancel" type="button">Cancel</button>
        </div>
    </div>
    <h4 class="list-group-item-heading category-name">${name}</h4>
    <form class="form-inline category-name-form hidden">
        <div class="form-group">
            <div class="input-group input-group-sm">
                <div class="input-group-addon">Name</div>
                <input type="text" class="error form-control category-name" placeholder="Name" value="${name}">
            </div>
            <span class="control-label category-save-error-label hidden">Category with this name already exists</span>
        </div>
    </form>
    <div class="list-group-item-text category-alternatives">
        <div class="collapse-group">
            <a class="show-stores" href="#">Show stores &raquo;</a>
            <div class="collapse stores">
            <#list stores as store>
            ${store}<br/>
            </#list>
            </div>
        </div>
    </div>
</li>