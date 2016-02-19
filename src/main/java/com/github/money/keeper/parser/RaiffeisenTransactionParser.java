package com.github.money.keeper.parser;

import com.github.money.keeper.model.RawTransaction;
import com.github.money.keeper.model.SalePoint;
import com.github.money.keeper.util.io.DigestingInputStream;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class RaiffeisenTransactionParser extends AbstractTransactionParser {

    @Override
    public ParsingResult doParse(InputStream source) throws IOException {
        Preconditions.checkNotNull(source, "Can't parse transactions from null stream");

        List<RawTransaction> transactions = Lists.newArrayList();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new DigestingInputStream(source)))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (isBlank(line)) continue;
                String[] split = line.split(";");
                String rawDate = split[0];
                String rawSpName = split[3];
                String rawSpDescription = split[4];
                String rawAmount = split[7];
                Optional<RawTransaction> transaction = buildTransaction(rawDate, rawSpName, rawSpDescription, rawAmount);
                if (transaction.isPresent()) {
                    transactions.add(transaction.get());
                }
            }
        }
        return new ParsingResult(null, transactions);
    }

    private Optional<RawTransaction> buildTransaction(String rawDate,
                                                      String rawSpName,
                                                      String rawSpDescription,
                                                      String rawAmount) {
        if (!checkField(rawDate) || !checkField(rawSpName) || !checkField(rawSpDescription) || !checkField(rawAmount)) {
            return Optional.empty();
        }
        LocalDate date = LocalDate.from(DateTimeFormatter.ofPattern("dd.MM.yyyy").parse(unwrapField(rawDate)));
        String spName = unwrapField(rawSpName);
        String spDescription = unwrapField(rawSpDescription);
        BigDecimal amount = new BigDecimal(unwrapField(rawAmount)).negate();
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return Optional.empty();
        }
        if (spDescription.equals("Fin.Inst. - Merchandise")) {
            return Optional.empty();
        }
        return Optional.of(new RawTransaction(date, new SalePoint(spName, spDescription), amount));
    }

    private boolean checkField(String field) {
        return field.length() >= 2 && field.startsWith("\"") && field.endsWith("\"") && !isBlank(field);
    }

    private String unwrapField(String field) {
        return StringUtils.strip(field.substring(1, field.length() - 1));
    }
}
