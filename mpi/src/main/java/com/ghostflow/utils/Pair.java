package com.ghostflow.utils;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Pair<T, V> {
    private final T fst;
    private final V snd;

    public Pair(T fst, V snd) {
        this.fst = fst;
        this.snd = snd;
    }
}
