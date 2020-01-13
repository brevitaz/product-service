package com.brevitaz.model;

import java.util.List;

public class SearchResponseModel<T>
{
    private List<T> result;
    private Integer totalPage;

    public List<T> getResult()
    {
        return result;
    }
    public void setResult(List<T> result)
    {
        this.result = result;
    }

    public Integer getTotalPage() {
        return totalPage;
    }
    public void setTotalPage(Integer totalPage)
    {
        this.totalPage = totalPage;
    }
}
