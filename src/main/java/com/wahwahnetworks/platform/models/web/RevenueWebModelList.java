package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.RevenueModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhaygood on 4/8/16.
 */

@WebSafeModel
public class RevenueWebModelList {

    private List<RevenueWebModel> revenueWebModelList = new ArrayList<>();

    private int currentPage;
    private int currentPageSize;
    private int numberOfPages;

    public RevenueWebModelList(Iterable<RevenueModel> revenueModelList){
        for(RevenueModel revenueModel : revenueModelList){
            RevenueWebModel revenueWebModel = new RevenueWebModel(revenueModel);
            revenueWebModelList.add(revenueWebModel);
        }
    }

    @JsonProperty("revenue_models")
    public List<RevenueWebModel> getRevenueModels()
    {
        return revenueWebModelList;
    }

    @JsonProperty("page_current")
    public int getCurrentPage()
    {
        return currentPage;
    }

    public void setCurrentPage(int currentPage)
    {
        this.currentPage = currentPage;
    }

    @JsonProperty("page_size")
    public int getCurrentPageSize()
    {
        return currentPageSize;
    }

    public void setCurrentPageSize(int currentPageSize)
    {
        this.currentPageSize = currentPageSize;
    }

    @JsonProperty("page_totalcount")
    public int getNumberOfPages()
    {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages)
    {
        this.numberOfPages = numberOfPages;
    }

}
