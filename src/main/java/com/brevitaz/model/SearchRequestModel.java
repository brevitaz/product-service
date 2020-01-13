package com.brevitaz.model;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@EntityScan
public class SearchRequestModel
{

    @NotNull(message = "Please provide page number.")
    @Min(value = 1, message = "Page number should not be less than 1")
    private Integer page;

    @NotNull(message = "Please provide page size.")
    @Min(value = 1, message = "Page size should not be less than 1")
    private Integer pageSize;

    private String searchText;

    public Integer getPage()
    {
        return page;
    }

    public void setPage(Integer page)
    {
        this.page = page;
    }

    public Integer getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(Integer pageSize)
    {
        this.pageSize = pageSize;
    }

    public String getSearchText()
    {
        return searchText;
    }

    public void setSearchText(String searchText)
    {
        this.searchText = searchText;
    }
}
