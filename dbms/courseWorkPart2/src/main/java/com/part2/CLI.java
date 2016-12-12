package com.part2;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by ivan on 14.11.16.
 */
public class CLI {
    private String user = "coursework";
    private String password = "123456789";
    private String url = "jdbc:postgresql://chebykinn.ru:1488/coursework";
    private Connection connection;
    private Jedis jedis;
    private Store store = new Store();
    private HashMap<String, Command> commandList;

    public int createContext(String user, String password) {
        try {
            connection = DriverManager.getConnection(url, user, password);
            DSLContext ctx = DSL.using(connection);
            store = new Store(ctx, jedis);
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int connect(String[] args){
        if( args.length < 3 ){
            jedis = new Jedis("chebykin.org:4242");
            return createContext(user, password);
        }else{
            return createContext(args[1], args[2]);
        }
    }

    public CLI(){
        commandList = new HashMap<>();
        commandList.put("connect", args -> connect(args));
        commandList.put("person", args -> store.personCmd(args));
        commandList.put("position", args -> store.positionCmd(args));
        commandList.put("salary", args -> store.positionSalaryCmd(args));
        commandList.put("schedule", args -> store.scheduleCmd(args));
        commandList.put("shop_person", args -> store.personShopCmd(args));
        commandList.put("shop", args -> store.shopCmd(args));
        commandList.put("product", args -> store.productCmd(args));
        commandList.put("product_amount", args -> store.productAmountCmd(args));
        commandList.put("product_price", args -> store.productPriceCmd(args));
        commandList.put("product_type", args -> store.productTypeCmd(args));
        commandList.put("sell_log", args -> store.sellLogCmd(args));
        commandList.put("help", args -> helpCmd());
        commandList.put("exit", args -> -1);
    }

    private int helpCmd() {
        System.out.println("Command list:");
        for (HashMap.Entry<String, Command> e : commandList.entrySet()) {
            System.out.println(e.getKey());
        }
        return 0;
    }

    public int readline(String cmdline) throws IllegalArgumentException{
        String[] args = cmdline.split("\\s+");
        if( commandList.containsKey(args[0]) ){
            return commandList.get(args[0]).run(args);
        }
        throw new IllegalArgumentException("No such command");
    }
}
