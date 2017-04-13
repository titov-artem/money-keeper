package com.github.money.keeper.storage.jdbc;

import com.github.money.keeper.model.core.SalePoint;
import com.github.money.keeper.storage.SalePointRepo;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.function.Function;

import static com.github.money.keeper.storage.jdbc.generated.Tables.SALE_POINT;

@Repository
public class SalePointJdbcRepo extends AbstractJdbcRepo<Long, SalePoint> implements SalePointRepo {

    private static Function<Record, SalePoint> MAPPER =
            record -> new SalePoint(
                    record.get(SALE_POINT.ID),
                    record.get(SALE_POINT.NAME),
                    record.get(SALE_POINT.RAW_CATEGORY),
                    record.get(SALE_POINT.STORE_ID)
            );

    @Inject
    public SalePointJdbcRepo(JdbcHelper jdbc, TxHelper txHelper) {
        super(
                MAPPER,
                (record, salePoint, withPrimary) -> {
                    if (withPrimary) {
                        record.set(SALE_POINT.ID, salePoint.getId());
                    }
                    record.set(SALE_POINT.NAME, salePoint.getName());
                    record.set(SALE_POINT.RAW_CATEGORY, salePoint.getRawCategory());
                },
                SALE_POINT,
                SALE_POINT.ID,
                jdbc,
                txHelper
        );
    }
}
