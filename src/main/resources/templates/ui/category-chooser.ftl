<div class="form-group">
    <select class="category-chooser selectpicker" data-live-search="true">
    <#list categories as category>
        <option value="${category.name}">${category.name}</option>
    </#list>
    </select>
</div>
