package com.devexperts.logvinov.qd;

import com.devexperts.qd.DataIntField;
import com.devexperts.qd.DataObjField;
import com.devexperts.qd.DataRecord;
import com.devexperts.qd.kit.*;

/**
 * QD scheme representation. Use {@link #getInstance()} to obtain instance of scheme.
 */
public final class Scheme extends DefaultScheme {

    private static final Scheme instance = new Scheme();

    private Scheme() {
        super(new PentaCodec(), new DataRecord[]{
                createQuoteRecord()
        });
    }

    public static Scheme getInstance() {
        return instance;
    }

    // --------------- Scheme descriptors ---------------

    private static QuoteDescriptor quoteDescriptor;

    public static QuoteDescriptor getQuoteDescriptor() {
        return quoteDescriptor;
    }

    // --------------- Scheme records ---------------

    public static final int QUOTE_RECORD_ID = 0;
    public static final String QUOTE_RECORD_NAME = "Quote";

    private static DataRecord createQuoteRecord() {
        QuoteDescriptor descriptor = new QuoteDescriptor(
                // DataObjFields
                new StringField(0, QUOTE_RECORD_NAME + ".Symbol"),
                // DataIntFields
                new CompactIntField(0, QUOTE_RECORD_NAME + ".TimeHi"),
                new CompactIntField(1, QUOTE_RECORD_NAME + ".TimeLo"),
                new DecimalField(2, QUOTE_RECORD_NAME + ".Bid"),
                new DecimalField(3, QUOTE_RECORD_NAME + ".Ask")
        );

        DataIntField[] intFields = new DataIntField[]{
                descriptor.getTimeHi(), descriptor.getTimeLo(),
                descriptor.getBid(), descriptor.getAsk()
        };
        DataObjField[] objFields = new DataObjField[]{
                descriptor.getSymbol()
        };

        DataRecord record = new DefaultRecord(QUOTE_RECORD_ID, QUOTE_RECORD_NAME, true, intFields, objFields);
        descriptor.setRecord(record);

        quoteDescriptor = descriptor;

        return record;
    }
}