<div class="category-line">
    <div class="category-merge-checkbox">
        <input type="checkbox"/>
    </div>
    <div class="category-control">
        <div class="category-menu">
            <input class="category-rename" type="button" value="Rename">
            <input class="category-union" type="button" value="Union...">
        </div>
        <div class="category-save-control hidden">
            <input class="category-save" type="button" value="Save">
            <input class="category-cancel" type="button" value="Cancel">
        </div>
    </div>
    <div class="category-name-wrapper">
        <div class="category-name">${name}</div>
        <input class="category-name hidden" type="text" value="${name}">
        <div class="category-alternatives">
        <#list alternatives as alternative>
            ${alternative}
        </#list>
        </div>
    </div>
</div>