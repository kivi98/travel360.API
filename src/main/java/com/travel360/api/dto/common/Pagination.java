package com.travel360.api.dto.common;

public record Pagination(
        int page,
        int size,
        long total
) {}
