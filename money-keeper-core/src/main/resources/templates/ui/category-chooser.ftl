<div class="category-chooser-container form-group">
    <select class="category-chooser selectpicker"
            data-live-search="true"
            <#if multiple??>multiple<#else></#if>
            data-width="<#if width??>${width}<#else>auto</#if>"
    >
    <#list categories as category>
        <option value="${category.name}">${category.name}</option>
    </#list>
    </select>
</div>
