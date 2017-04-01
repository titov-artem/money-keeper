<li class="category-row list-group-item">
    <div class="category-controls pull-right">
        <i class="category-rename fa fa-pencil"></i>&nbsp;
        <i class="category-union fa fa-link"></i>&nbsp;
        <i class="category-delete fa fa-trash text-danger"></i>&nbsp;
    </div>
    <h4 class="list-group-item-heading">${name}</h4>
    <div class="list-group-item-text collapse-group">
        <a class="show-stores" href="#">Show stores &raquo;</a>
        <div class="collapse stores">
        <#list stores as store>
            <span class="category-store" store="${store}">
            ${store}&nbsp;&nbsp;
                <span class="category-store-controls">
                    <i class="store-change-category fa fa-chain-broken"></i>
                </span>
            </span><br/>
        </#list>
        </div>
    </div>
</li>