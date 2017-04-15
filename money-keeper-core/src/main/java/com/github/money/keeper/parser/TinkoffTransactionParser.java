package com.github.money.keeper.parser;

import com.github.money.keeper.model.core.Account;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class TinkoffTransactionParser extends AbstractTransactionParser {

    @Override
    public ParsingResult doParse(Account account, InputStream source) throws IOException {
        Preconditions.checkNotNull(source, "Can't parse transactions from null stream");

        List<ParsedTransaction> transactions = Lists.newArrayList();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(source, Charset.forName("CP1251")))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (isBlank(line)) continue;
                String[] split = line.split(";");
                String rawDate = split[0];
                String rawAmount = split[4];
                String rawSpDescription = split[8];
                String rawSpName = split[10];
                Optional<ParsedTransaction> transaction = buildTransaction(account, rawDate, rawSpName, rawSpDescription, rawAmount);
                if (transaction.isPresent()) {
                    transactions.add(transaction.get());
                }
            }
        }
        return new ParsingResult(null, transactions);
    }

    @Override protected ParserType getParserType() {
        return ParserType.TINKOFF;
    }

    private Optional<ParsedTransaction> buildTransaction(Account account,
                                                         String rawDate,
                                                         String rawSpName,
                                                         String rawSpDescription,
                                                         String rawAmount) {
        if (!checkField(rawDate) || !checkField(rawSpName) || !checkField(rawSpDescription) || !checkField(rawAmount)) {
            return Optional.empty();
        }
        LocalDate date = LocalDate.from(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").parse(unwrapField(rawDate)));
        String spName = unwrapField(rawSpName);
        String spDescription = unwrapField(rawSpDescription);
        BigDecimal amount = new BigDecimal(unwrapField(rawAmount.replace(",", "."))).negate();
        if (spName.equals("Перевод c карты другого банка")) {
            return Optional.empty();
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return Optional.empty();
        }
        return Optional.of(ParsedTransaction.create(account.getId(), date, spName, spDescription, amount));
    }

    private boolean checkField(String field) {
        return field.length() >= 2 && field.startsWith("\"") && field.endsWith("\"") && !isBlank(field);
    }

    private String unwrapField(String field) {
        return StringUtils.strip(field.substring(1, field.length() - 1));
    }
}
