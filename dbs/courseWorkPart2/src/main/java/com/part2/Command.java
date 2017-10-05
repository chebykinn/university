package com.part2;

/**
 * Created by ivan on 14.11.16.
 */
public interface Command {
    int run(String[] args) throws IllegalArgumentException;
}
