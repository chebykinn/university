package com.ghostflow.http.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class PaginationParameters {
    public static final String DEFAULT_OFFSET;
    public static final String DEFAULT_LIMIT;

    static {
        DEFAULT_OFFSET = "0";
        DEFAULT_LIMIT = "60";
    }

    private final Integer offset;
    private final Integer limit;
}
