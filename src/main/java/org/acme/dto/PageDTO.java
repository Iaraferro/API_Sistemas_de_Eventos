package org.acme.dto;

import java.util.List;

public class PageDTO<T> {
    public List<T> content;
    public int page;
    public int size;
    public long totalElements;
    public int totalPages;
}
