package com.ghostflow.http.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class PaginationParameters {
    public static final String DEFAULT_OFFSET = "0";
    public static final String DEFAULT_LIMIT = "60";

    private final Integer offset;
    private final Integer limit;
}
