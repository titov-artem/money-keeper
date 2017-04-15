package com.github.money.keeper.parser;

import com.github.money.keeper.model.core.Account;
import com.github.money.keeper.util.io.DigestingInputStream;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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

@Component
public class RaiffeisenTransactionParser extends AbstractTransactionParser {
    private static final Logger log = LoggerFactory.getLogger(RaiffeisenTransactionParser.class);

    @Override
    public ParsingResult doParse(Account account, InputStream source) throws IOException {
        Preconditions.checkNotNull(source, "Can't parse transactions from null stream");

        List<ParsedTransaction> transactions = Lists.newArrayList();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new DigestingInputStream(source)))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (isBlank(line)) continue;
                String[] split = line.split(";");
                String rawDate = split[0];
                String rawSpName = split[3];
                String rawSpDescription = split[2];
                String rawAmount = split[9];
                try {
                    Optional<ParsedTransaction> transaction = buildTransaction(account, rawDate, rawSpName, rawSpDescription, rawAmount);
                    if (transaction.isPresent()) {
                        transactions.add(transaction.get());
                    }
                } catch (Exception e) {
                    log.error("Failed to parse line " + line, e);
                }
            }
        }
        return new ParsingResult(null, transactions);
    }

    @Override protected ParserType getParserType() {
        return ParserType.RAIFFEISEN_CARD;
    }

    private Optional<ParsedTransaction> buildTransaction(Account account,
                                                         String rawDate,
                                                         String rawSpName,
                                                         String rawSpDescription,
                                                         String rawAmount) {
        if (!checkField(rawDate) || !checkField(rawSpName) || !checkField(rawSpDescription) || !checkField(rawAmount)) {
            return Optional.empty();
        }
        LocalDate date = LocalDate.from(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").parse(unwrapField(rawDate)));
        String spName = unwrapField(rawSpName);
        String spDescription = unwrapField(rawSpDescription);
        BigDecimal amount = new BigDecimal(fixNumbers(unwrapField(rawAmount))).negate();
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return Optional.empty();
        }
        if (spDescription.equals("Fin.Inst. - Merchandise")) {
            return Optional.empty();
        }
        return Optional.of(ParsedTransaction.create(
                account.getId(),
                date,
                spName,
                spDescription,
                amount)
        );
    }

    private String fixNumbers(String s) {
        return s.replaceAll(" ", "");
    }

    private boolean checkField(String field) {
        return !isBlank(field);
    }

    private String unwrapField(String field) {
        field = StringUtils.strip(field);
        if (field.startsWith("\"") && field.endsWith("\"")) {
            return StringUtils.strip(field.substring(1, field.length() - 1));
        } else {
            return field;
        }
    }
}
