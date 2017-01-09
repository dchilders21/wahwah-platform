package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.DemandSourceConnection;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhaygood on 1/28/16.
 */

@WebSafeModel
public class DemandSourceConnectionListModel {

    private List<DemandSourceConnectionModel> demandSourceConnectionModelList = new ArrayList<>();

    private int currentPage;
    private int currentPageSize;
    private int numberOfPages;

    public DemandSourceConnectionListModel(Page<DemandSourceConnection> demandSourceConnections) throws IOException {

        final IOException exception = new IOException("An exception was thrown while creating this list");

        demandSourceConnections.forEach((DemandSourceConnection demandSourceConnection) -> {
            try {
                add(demandSourceConnection);
            } catch (IOException _exception){
                exception.initCause(_exception);
                exception.setStackTrace(_exception.getStackTrace());
            }
        });

        if(exception.getCause() != null){
            throw exception;
        }

        setCurrentPage(demandSourceConnections.getNumber());
        setCurrentPageSize(demandSourceConnections.getNumberOfElements());
        setNumberOfPages(demandSourceConnections.getTotalPages());
    }

    @JsonProperty("demand_source_connections")
    public List<DemandSourceConnectionModel> getDemandSourceConnections() {
        return demandSourceConnectionModelList;
    }

    private void add(DemandSourceConnection demandSourceConnection) throws IOException {
        DemandSourceConnectionModel demandSourceConnectionModel = new DemandSourceConnectionModel(demandSourceConnection);
        demandSourceConnectionModelList.add(demandSourceConnectionModel);
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
