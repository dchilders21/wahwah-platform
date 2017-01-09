package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.DemandSource;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jhaygood on 1/27/16.
 */

@WebSafeModel
public class DemandSourceListModel {

    private List<DemandSourceModel> demandSourceModelList = new ArrayList<>();

    private int currentPage;
    private int currentPageSize;
    private int numberOfPages;

    private Map<Integer,DemandSourceModel> demandSourceModelMap = new HashMap<>();

    public DemandSourceListModel(List<DemandSourceModel> demandSourceModels){
        demandSourceModels.forEach((DemandSourceModel demandSourceModel) -> {
            add(demandSourceModel);
        });

        setCurrentPage(0);
        setCurrentPageSize(demandSourceModels.size());
        setNumberOfPages(1);
    }

    public void setPageable(Pageable pageable){

        int startIndex = pageable.getPageNumber() * pageable.getPageSize();
        int endIndex = startIndex + pageable.getPageSize();

        if(startIndex >= demandSourceModelList.size() || startIndex < 0){
            demandSourceModelList = new ArrayList<>();
        }

        if(endIndex >= demandSourceModelList.size()){
            endIndex = demandSourceModelList.size();
        }

        int numOfPages = (int)Math.ceil((double)demandSourceModelList.size() / (double)pageable.getPageSize());

        demandSourceModelList = demandSourceModelList.subList(startIndex,endIndex);

        setCurrentPage(pageable.getPageNumber());
        setCurrentPageSize(pageable.getPageSize());
        setNumberOfPages(numOfPages);
    }

    @JsonProperty("demand_sources")
    public List<DemandSourceModel> getDemandSources() {
        return demandSourceModelList;
    }

    private void add(DemandSource demandSource){
        DemandSourceModel demandSourceModel = new DemandSourceModel(demandSource);
        add(demandSourceModel);
    }

    private void add(DemandSourceModel demandSourceModel){
        demandSourceModelList.add(demandSourceModel);
        demandSourceModelMap.put(demandSourceModel.getDemandSourceId(),demandSourceModel);
    }

    public DemandSourceModel getModelForId(int id){
        return demandSourceModelMap.get(id);
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
