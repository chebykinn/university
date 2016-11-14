package com.part2;

import classes.tables.*;
import classes.tables.records.*;
import classes.udt.records.PassportRecord;
import org.jooq.*;
import org.jooq.impl.TableImpl;
import org.jooq.types.Interval;
import org.jooq.util.derby.sys.Sys;
import org.jooq.util.postgres.PostgresDataType;
import org.jooq.util.xml.jaxb.Table;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class Store {
    private DSLContext ctx = null;

    enum CmdType{
        ADD,
        READ,
        UPDATE,
        DELETE,
        FIELDS,
    };

    public Store(){
    }

    public Store(DSLContext ctx){
        this.ctx = ctx;
    }

    public CmdType checkCmd(String[] args) throws IllegalArgumentException{
        if( ctx == null ){
            throw new IllegalArgumentException("not connected");
        }
        if( args.length == 1 ){
            throw new IllegalArgumentException(args[0]+": expected add | read | update | delete | fields");
        }
        CmdType type = CmdType.ADD;
        if( args[1].equals("read") ) type = CmdType.READ;
        if( args[1].equals("update") ) type = CmdType.UPDATE;
        if( args[1].equals("delete") ) type = CmdType.DELETE;
        if( args[1].equals("fields") ) type = CmdType.FIELDS;

        if( type == CmdType.READ && args.length < 4 ){
            throw new IllegalArgumentException("Need field name for read command");
        }

        if( type == CmdType.ADD && args.length < 3 ){
            throw new IllegalArgumentException("Need at least one field");
        }

        if( type == CmdType.UPDATE && args.length < 4 ){
            throw new IllegalArgumentException("Need at least one field and id");
        }

        if( type == CmdType.DELETE && args.length < 3 ){
            throw new IllegalArgumentException("Need id");
        }
        return type;
    }

    public<R extends UpdatableRecord, T extends TableImpl<R>, F extends TableField<R, Integer>>
                                int doCommand(T table, F[] pkey, CmdType type, int[] id, String fieldName, Object[] args, boolean skip_id){
        R record = null;
        if( type == CmdType.ADD || type == CmdType.FIELDS ){
            record = ctx.newRecord(table);
        }
        if( type == CmdType.READ || type == CmdType.UPDATE || type == CmdType.DELETE ){
            if( pkey.length == 1 ) {
                record = ctx.fetchOne(table, pkey[0].equal(id[0]));
            }else{
                record = ctx.fetchOne(table, pkey[0].equal(id[0]).and(pkey[1].equal(id[1])));
            }
            if( record == null ){
                throw new IllegalArgumentException("No such row");
            }
        }
        if( type == CmdType.DELETE ){
            record.delete();
            return 1;
        }

        if( type == CmdType.READ ){
            Object data = record.getValue(fieldName);
            System.out.println(data);
            return 1;
        }

        Field<?>[] fields = record.fields();
        if( type == CmdType.FIELDS ){
            for(Field f : fields){
                System.out.println(f);
            }
            return 1;
        }
        int i = skip_id ? -id.length : 0;
        for(Field f : fields){
            // Skip id
            if( i < 0 ){
                i++;
                continue;
            }
            record.set(f, args[i++]);
            System.out.println(f);
        }
        record.store();
        return (int)record.getValue(0);
    }

    public<R extends UpdatableRecord, T extends TableImpl<R>> Object[]
            prepareArgs(T table, CmdType type, String[] args, int[] ids, String[] field_name, boolean skip_id) throws IllegalArgumentException{

        int argsStart = 2;
        Object[] out_args = null;
        if( type == CmdType.READ || type == CmdType.UPDATE || type == CmdType.DELETE ){
            ids[0] = Integer.parseInt(args[argsStart]);
            if( skip_id ) argsStart++;
            if( ids.length > 1 ){
                ids[1] = Integer.parseInt(args[argsStart]);
                if( skip_id ) argsStart++;
            }
            if( type == CmdType.READ ){
                field_name[0] = args[argsStart++];
            }
        }
        if( type == CmdType.ADD || type == CmdType.UPDATE ) {
            int fields_len = table.fields().length;
            out_args = new Object[fields_len];
            System.arraycopy(args, argsStart, out_args, 0, args.length-argsStart);
        }
        return out_args;
    }

    public int personCmd(String[] args) throws IllegalArgumentException{
        CmdType type = checkCmd(args);
        String[] field_name = new String[1];
        int[] ids = new int[1];
        TableImpl<PersonsRecord> table = Persons.PERSONS;
        TableField<PersonsRecord, Integer>[] pkey = new TableField[1];
        pkey[0] = Persons.PERSONS.PERSON_ID;

        Object[] out_args = prepareArgs(table, type, args, ids, field_name, true);
        if( out_args != null ) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsed;
            try {
                parsed = format.parse(out_args[3].toString());
            } catch (ParseException e) {
                parsed = new java.util.Date();
            }
            out_args[3] = new java.sql.Date(parsed.getTime());

            PassportRecord passport = new PassportRecord();
            passport.setSerialNumber(Integer.parseInt(out_args[8].toString()));
            passport.setId(Integer.parseInt(out_args[9].toString()));
            out_args[8] = null; // photo
            out_args[9] = passport;
        }

        // person add 123 123 123 2016-01-01 M adsads asdasd adasd 5716 478596 1
        // person update 9 123 123 123 2016-01-01 M adsads asdasd adasd 5718 478596 1
        // person delete 36

        return doCommand(table, pkey, type, ids, field_name[0], out_args, true);

    }

    public int positionCmd(String[] args) {
        CmdType type = checkCmd(args);
        String[] field_name = new String[1];
        int[] ids = new int[1];
        TableImpl<PositionsRecord> table = Positions.POSITIONS;
        TableField<PositionsRecord, Integer>[] pkey = new TableField[1];
        pkey[0] = Positions.POSITIONS.POSITION_ID;

        Object[] out_args = prepareArgs(table, type, args, ids, field_name, true);

        // position add name desc
        return doCommand(table, pkey, type, ids, field_name[0], out_args, true);
    }

    public int positionSalaryCmd(String[] args) {
        CmdType type = checkCmd(args);
        String[] field_name = new String[1];
        int[] ids = new int[2];
        TableImpl<PositionSalaryRecord> table = PositionSalary.POSITION_SALARY;
        TableField<PositionSalaryRecord, Integer>[] pkey = new TableField[2];
        pkey[0] = PositionSalary.POSITION_SALARY.POSITION_ID;
        pkey[1] = PositionSalary.POSITION_SALARY.SHOP_ID;

        Object[] out_args = prepareArgs(table, type, args, ids, field_name, true);



        // salary add 1 1 100000
        return doCommand(table, pkey, type, ids, field_name[0], out_args, true);
    }

    public int scheduleCmd(String[] args) {
        CmdType type = checkCmd(args);
        String[] field_name = new String[1];
        int[] ids = new int[1];
        TableImpl<PersonScheduleRecord> table = PersonSchedule.PERSON_SCHEDULE;
        TableField<PersonScheduleRecord, Integer>[] pkey = new TableField[1];
        pkey[0] = PersonSchedule.PERSON_SCHEDULE.SCHED_ID;

        Object[] out_args = prepareArgs(table, type, args, ids, field_name, true);

        // schedule add 9 0
        if( out_args != null ){
            out_args[1] = out_args[1].toString();
            out_args[2] = null;
        }
        return doCommand(table, pkey, type, ids, field_name[0], out_args, true);
    }

    public int personShopCmd(String[] args) {
        CmdType type = checkCmd(args);
        String[] field_name = new String[1];
        int[] ids = new int[1];
        TableImpl<PersonShopRecord> table = PersonShop.PERSON_SHOP;
        TableField<PersonShopRecord, Integer>[] pkey = new TableField[1];
        pkey[0] = PersonShop.PERSON_SHOP.PERSON_ID;

        Object[] out_args = prepareArgs(table, type, args, ids, field_name, false);

        // shop_person add 9 1
        return doCommand(table, pkey, type, ids, field_name[0], out_args, false);
    }

    public int shopCmd(String[] args) {
        CmdType type = checkCmd(args);
        String[] field_name = new String[1];
        int[] ids = new int[1];
        TableImpl<ShopsRecord> table = Shops.SHOPS;
        TableField<ShopsRecord, Integer>[] pkey = new TableField[1];
        pkey[0] = Shops.SHOPS.SHOP_ID;

        Object[] out_args = prepareArgs(table, type, args, ids, field_name, true);

        // shop add testst
        return doCommand(table, pkey, type, ids, field_name[0], out_args, true);
    }

    public int productCmd(String[] args) {
        CmdType type = checkCmd(args);
        String[] field_name = new String[1];
        int[] ids = new int[1];
        TableImpl<ProductsRecord> table = Products.PRODUCTS;
        TableField<ProductsRecord, Integer>[] pkey = new TableField[1];
        pkey[0] = Products.PRODUCTS.PRODUCT_ID;

        Object[] out_args = prepareArgs(table, type, args, ids, field_name, true);

        // product add testproduct 1
        return doCommand(table, pkey, type, ids, field_name[0], out_args, true);
    }

    public int productAmountCmd(String[] args) {
        CmdType type = checkCmd(args);
        String[] field_name = new String[1];
        int[] ids = new int[2];
        TableImpl<ProductAmountsRecord> table = ProductAmounts.PRODUCT_AMOUNTS;
        TableField<ProductAmountsRecord, Integer>[] pkey = new TableField[2];
        pkey[0] = ProductAmounts.PRODUCT_AMOUNTS.PRODUCT_ID;
        pkey[1] = ProductAmounts.PRODUCT_AMOUNTS.SHOP_ID;

        Object[] out_args = prepareArgs(table, type, args, ids, field_name, false);

        // product_amount add 1 1 100
        return doCommand(table, pkey, type, ids, field_name[0], out_args, false);
    }

    public int productPriceCmd(String[] args) {
        CmdType type = checkCmd(args);
        String[] field_name = new String[1];
        int[] ids = new int[2];
        TableImpl<ProductPricesRecord> table = ProductPrices.PRODUCT_PRICES;
        TableField<ProductPricesRecord, Integer>[] pkey = new TableField[2];
        pkey[0] = ProductPrices.PRODUCT_PRICES.PRODUCT_ID;
        pkey[1] = ProductPrices.PRODUCT_PRICES.SHOP_ID;

        Object[] out_args = prepareArgs(table, type, args, ids, field_name, false);

        // product_amount add 1 1 100
        return doCommand(table, pkey, type, ids, field_name[0], out_args, false);
    }

    public int productTypeCmd(String[] args) {
        CmdType type = checkCmd(args);
        String[] field_name = new String[1];
        int[] ids = new int[1];
        TableImpl<ProductTypesRecord> table = ProductTypes.PRODUCT_TYPES;
        TableField<ProductTypesRecord, Integer>[] pkey = new TableField[1];
        pkey[0] = ProductTypes.PRODUCT_TYPES.TYPE_ID;

        Object[] out_args = prepareArgs(table, type, args, ids, field_name, true);

        // product_type add title desc
        return doCommand(table, pkey, type, ids, field_name[0], out_args, true);
    }

    public int sellLogCmd(String[] args) {
        CmdType type = checkCmd(args);
        String[] field_name = new String[1];
        int[] ids = new int[1];
        TableImpl<SellLogRecord> table = SellLog.SELL_LOG;
        TableField<SellLogRecord, Integer>[] pkey = new TableField[1];
        pkey[0] = SellLog.SELL_LOG.LOG_ID;

        Object[] out_args = prepareArgs(table, type, args, ids, field_name, true);

        // sell_log add 9 1 100
        if( out_args != null ){
            if(out_args[3] != null ){
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date parsed;
                try {
                    parsed = format.parse(out_args[3].toString());
                } catch (ParseException e) {
                    parsed = new java.util.Date();
                }
                out_args[3] = new java.sql.Date(parsed.getTime());
            }
        }
        return doCommand(table, pkey, type, ids, field_name[0], out_args, true);
    }
}
