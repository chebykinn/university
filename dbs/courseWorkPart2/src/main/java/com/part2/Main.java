package com.part2;
import java.util.Scanner;
import java.util.logging.LogManager;

public class Main {
    static {
        LogManager.getLogManager().reset();
    }

    public static void main(String[] args) {
        CLI cli = new CLI();
        while (true){
            System.out.print("> ");
            Scanner s = new Scanner(System.in);
            String cmdline = s.nextLine();
            try {
                int rc = cli.readline(cmdline);
                if( rc == -1 ) break;
                if( rc > 0 ) System.out.println(rc);
            }catch (IllegalArgumentException e){
                System.err.println(e.getMessage());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
