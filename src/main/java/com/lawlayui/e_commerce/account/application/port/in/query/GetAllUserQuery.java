package com.lawlayui.e_commerce.account.application.port.in.query;

public class GetAllUserQuery {
    private int page; 
    private int pageSize; 

    public GetAllUserQuery(int page, int pageSize) {
        this.page = page; 
        this.pageSize = pageSize;
    }

    public void setPage(int page) {
        this.page = page;
    }
    public int getPage() {
        return page;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    public int getPageSize() {
        return pageSize;
    }
}
