/**
 * This class is generated by jOOQ
 */
package classes.tables;


import classes.Keys;
import classes.Public;
import classes.tables.records.SellLogRecord;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.8.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SellLog extends TableImpl<SellLogRecord> {

    private static final long serialVersionUID = 1015653966;

    /**
     * The reference instance of <code>public.sell_log</code>
     */
    public static final SellLog SELL_LOG = new SellLog();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SellLogRecord> getRecordType() {
        return SellLogRecord.class;
    }

    /**
     * The column <code>public.sell_log.log_id</code>.
     */
    public final TableField<SellLogRecord, Integer> LOG_ID = createField("log_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('sell_log_log_id_seq'::regclass)", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>public.sell_log.product_id</code>.
     */
    public final TableField<SellLogRecord, Integer> PRODUCT_ID = createField("product_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>public.sell_log.shop_id</code>.
     */
    public final TableField<SellLogRecord, Integer> SHOP_ID = createField("shop_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>public.sell_log.amount</code>.
     */
    public final TableField<SellLogRecord, Integer> AMOUNT = createField("amount", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>public.sell_log.date</code>.
     */
    public final TableField<SellLogRecord, Date> DATE = createField("date", org.jooq.impl.SQLDataType.DATE.defaultValue(org.jooq.impl.DSL.field("date(now())", org.jooq.impl.SQLDataType.DATE)), this, "");

    /**
     * Create a <code>public.sell_log</code> table reference
     */
    public SellLog() {
        this("sell_log", null);
    }

    /**
     * Create an aliased <code>public.sell_log</code> table reference
     */
    public SellLog(String alias) {
        this(alias, SELL_LOG);
    }

    private SellLog(String alias, Table<SellLogRecord> aliased) {
        this(alias, aliased, null);
    }

    private SellLog(String alias, Table<SellLogRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<SellLogRecord, Integer> getIdentity() {
        return Keys.IDENTITY_SELL_LOG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<SellLogRecord> getPrimaryKey() {
        return Keys.NEWTABLE_PK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<SellLogRecord>> getKeys() {
        return Arrays.<UniqueKey<SellLogRecord>>asList(Keys.NEWTABLE_PK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<SellLogRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<SellLogRecord, ?>>asList(Keys.SELL_LOG__FK_PRODUCT_ID, Keys.SELL_LOG__FK_SHOP_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SellLog as(String alias) {
        return new SellLog(alias, this);
    }

    /**
     * Rename this table
     */
    public SellLog rename(String name) {
        return new SellLog(name, null);
    }
}
