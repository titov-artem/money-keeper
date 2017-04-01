<!--
<#function tableRowClass index>
    <#if (index % 2) == 0>
        <#return "odd" />
    <#else>
        <#return "even" />
    </#if>
</#function>
-->
<!DOCTYPE HTML>
<html>
<head>
    <meta charset="utf-8">

    <script language="JavaScript" src="http://code.jquery.com/jquery-2.1.4.min.js"></script>

    <title>Expense report</title>
    <style>
        .category-line {
            width: 800px;
            padding: 10px;

            /*border: solid 1px red;*/
        }

        .category-name {
            width: 500px;
            float: left;

            /*border: solid 1px blue;*/
        }

        .category-percentage {
            float: right;
            width: 150px;
            height: 50px;

            /*border: solid 1px green;*/
        }

        .category-percentage span {
            display: table-cell;
            width: 150px;
            height: 50px;
            text-align: center;
            vertical-align: middle;

            font-size: x-large;
        }

        .category-amount {
            clear: left;
            width: 500px;
            padding-top: 10px;

            /*border: 1px solid black;*/
        }

        .category-transactions-wrapper {
            clear: both;
            margin-top: 20px;
            margin-bottom: 20px;

            /*height: 1px;*/
            /*border: 1px solid aqua;*/
        }

        .category-transactions {
            display: none;
        }

        .category-transaction td {
            padding: 5px;
        }

        .transaction-date {
            width: 100px;
        }

        .transaction-point {
            width: 400px;
        }

        .transaction-amount {
            width: 100px;
            text-align: right;
        }

        .odd {
            background-color: #f0f0ff;
        }

        .even {
            background-color: white;
        }
    </style>
</head>
<body>
<script language="JavaScript">
    $(document).ready(function () {
        $('.category-line').click(function () {
            $(this).find('.category-transactions').toggle();
        })
    })
</script>
<#list report.reports as cReport>
<div class="category-line ${tableRowClass(cReport_index)}">
    <div class="category-name">${cReport.category.name}</div>
    <div class="category-percentage"><span>${cReport.percentage}%</span></div>
    <div class="category-amount">${cReport.amount?string.currency}</div>
    <div class="category-transactions-wrapper">
        <div class="category-transactions">
            <table border="0" cellpadding="0" cellspacing="0">
                <#list cReport.transactions as transaction>
                    <tr class="category-transaction ${tableRowClass(transaction_index)}">
                        <td class="transaction-date">${transaction.date}</td>
                        <td class="transaction-point">${transaction.salePoint.name}</td>
                        <td class="transaction-amount">${transaction.amount?string.currency}</td>
                    </tr>
                </#list>
            </table>
        </div>
    </div>
</div>
</#list>
<div class="total">Total: ${report.total}</div>
</body>
</html>