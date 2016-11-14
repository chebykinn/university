package com.part2;

import classes.tables.*;
import classes.tables.records.*;
import classes.udt.records.PassportRecord;
import org.apache.jackrabbit.webdav.ordering.Position;
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

    Store(){
    }

    Store(DSLContext ctx){
        this.ctx = ctx;
    }

    private CmdType checkCmd(String[] args) throws IllegalArgumentException{
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

    private <R extends UpdatableRecord, T extends TableImpl<R>, F extends TableField<R, Integer>>
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

    private Object[] prepareArgs(int fields_len, CmdType type, String[] args, int[] ids, String[] field_name, boolean skip_id) throws IllegalArgumentException{
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
            out_args = new Object[fields_len];
            System.arraycopy(args, argsStart, out_args, 0, args.length-argsStart);
        }
        return out_args;
    }

    int personCmd(String[] args) throws IllegalArgumentException{
        CmdType type = checkCmd(args);
        String[] field_name = new String[1];
        int[] ids = new int[1];
        TableImpl<PersonsRecord> table = Persons.PERSONS;
        TableField<PersonsRecord, Integer>[] pkey = new TableField[1];
        pkey[0] = Persons.PERSONS.PERSON_ID;

        Object[] out_args = prepareArgs(table.fields().length, type, args, ids, field_name, true);
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

    <R extends UpdatableRecord, F extends TableField<R, Integer>>
            int generalCmd(TableImpl<R> table, F[] pkey, boolean skipId, String[] args){
        CmdType type = checkCmd(args);
        String[] field_name = new String[1];
        int[] ids = new int[pkey.length];

        Object[] out_args = prepareArgs(table.fields().length, type, args, ids, field_name, skipId);

        return doCommand(table, pkey, type, ids, field_name[0], out_args, skipId);
    }

    int positionCmd(String[] args) {
        // position add name desc
        return generalCmd(Positions.POSITIONS, new TableField[]{Positions.POSITIONS.POSITION_ID}, true, args);
    }

    int positionSalaryCmd(String[] args) {
        // salary add 1 1 100000
        return generalCmd(PositionSalary.POSITION_SALARY, new TableField[]{
                PositionSalary.POSITION_SALARY.POSITION_ID,
                PositionSalary.POSITION_SALARY.SHOP_ID,
        }, true, args);
    }

    int scheduleCmd(String[] args) {
        // schedule add 9 0
        return generalCmd(PersonSchedule.PERSON_SCHEDULE, new TableField[]{PersonSchedule.PERSON_SCHEDULE.SCHED_ID}, true, args);
    }

    int personShopCmd(String[] args) {
        // shop_person add 9 1
        return generalCmd(PersonShop.PERSON_SHOP, new TableField[]{PersonShop.PERSON_SHOP.PERSON_ID}, false, args);
    }

    int shopCmd(String[] args) {
        // shop add testst
        return generalCmd(Shops.SHOPS, new TableField[]{Shops.SHOPS.SHOP_ID}, true, args);
    }

    int productCmd(String[] args) {
        // product add testproduct 1
        return generalCmd(Products.PRODUCTS, new TableField[]{Products.PRODUCTS.PRODUCT_ID}, true, args);
    }

    int productAmountCmd(String[] args) {
        // product_amount add 1 1 100
        return generalCmd(ProductAmounts.PRODUCT_AMOUNTS, new TableField[]{
                ProductAmounts.PRODUCT_AMOUNTS.PRODUCT_ID,
                ProductAmounts.PRODUCT_AMOUNTS.SHOP_ID,
        }, false, args);
    }

    int productPriceCmd(String[] args) {
        // product_amount add 1 1 100
        return generalCmd(ProductPrices.PRODUCT_PRICES, new TableField[]{
                ProductPrices.PRODUCT_PRICES.PRODUCT_ID,
                ProductPrices.PRODUCT_PRICES.SHOP_ID,
        }, false, args);
    }

    int productTypeCmd(String[] args) {
        // product_type add title desc
        return generalCmd(ProductTypes.PRODUCT_TYPES, new TableField[]{ProductTypes.PRODUCT_TYPES.TYPE_ID}, true, args);
    }

    int sellLogCmd(String[] args) {
        CmdType type = checkCmd(args);
        String[] field_name = new String[1];
        int[] ids = new int[1];
        TableImpl<SellLogRecord> table = SellLog.SELL_LOG;
        TableField<SellLogRecord, Integer>[] pkey = new TableField[1];
        pkey[0] = SellLog.SELL_LOG.LOG_ID;

        Object[] out_args = prepareArgs(table.fields().length, type, args, ids, field_name, true);

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
