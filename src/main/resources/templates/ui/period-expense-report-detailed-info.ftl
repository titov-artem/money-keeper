<script language="JavaScript">
    $(document).ready(function () {
        $('.category-line').click(function () {
            $(this).find('.category-transactions').toggle();
        })
    })
</script>
<div id="report" class="panel panel-default">
    <div class="panel-heading">
        <h4>Detailed info</h4>
    </div>
    <div class="panel-body">
        <div id="detailed-info" class="panel-group">
        <#list report.categoryReports as cReport>
            <div class="panel panel-default">
                <div class="panel-heading" id="heading-${cReport.id}">
                    <h6 class="panel-title">
                        <a name="link-${cReport.id}" data-toggle="collapse" href="#collapse-${cReport.id}">
                        ${cReport.category} - ${cReport.amount?string.currency} (${cReport.percentage}%)
                        </a>
                    </h6>
                </div>
                <div id="collapse-${cReport.id}" class="panel-collapse collapse">
                    <ul class="list-group">
                        <#list cReport.transactions as transaction>
                            <li class="list-group-item">
                                <span>${transaction.date}</span>&nbsp;
                                <span>${transaction.store}</span>
                                <span style="float: right">${transaction.amount?string.currency}</span>
                            </li>
                        </#list>
                    </ul>
                </div>
            </div>
        </#list>
        </div>
        <h5>Total: ${report.total?string.currency}</h5>
    </div>
</div>