package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.data.entities.*;
import com.wahwahnetworks.platform.data.entities.enums.AccountType;
import com.wahwahnetworks.platform.data.repos.*;
import com.wahwahnetworks.platform.lib.JsonSerializer;
import com.wahwahnetworks.platform.lib.analytics.AnalyticsRestTemplate;
import com.wahwahnetworks.platform.models.DemandSourceListFilterModel;
import com.wahwahnetworks.platform.models.DemandSourceRequestModel;
import com.wahwahnetworks.platform.models.SessionModel;
import com.wahwahnetworks.platform.models.UserModel;
import com.wahwahnetworks.platform.models.analytics.CustomReportRequestModel;
import com.wahwahnetworks.platform.models.analytics.CustomReportResponseModel;
import com.wahwahnetworks.platform.models.analytics.CustomReportResultCSVModel;
import com.wahwahnetworks.platform.models.analytics.ValidateDemandSourceCredentialsRequestModel;
import com.wahwahnetworks.platform.models.web.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by jhaygood on 1/27/16.
 */

@Service
public class DemandSourceService {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DemandSourceRepository demandSourceRepository;

    @Autowired
    private DemandSourceConnectionTypeRepository demandSourceConnectionTypeRepository;

    @Autowired
    private DemandSourceConnectionRepository demandSourceConnectionRepository;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private LineItemTargetingRepository lineItemTargetingRepository;

    @Autowired
    private LineItemRepository lineItemRepository;

    @Autowired
    private LineItemAdRepository adRepository;

    @Autowired
    private CreativeRepository creativeRepository;

    @Autowired
    private DemandSourcePlacementRepository placementRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private CustomReportService customReportService;

    @Transactional(readOnly = true)
    public DemandSourceListModel getDemandSourcesForAccount(SessionModel sessionModel, Pageable pageable, DemandSourceListFilterModel filterModel) throws Exception {

        Account account = sessionModel.getAccount();

        List<DemandSourceModel> allDemandSourceModels = new ArrayList<>();

        if(sessionModel.getUser().getAccountType() != AccountType.FREE){
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.plusDays(-7);

            if(filterModel.getFilterDimensions().size() > 0 || !startDate.isEqual(filterModel.getStartDate()) || !endDate.isEqual(filterModel.getEndDate())){
                CustomReportRequestModel reportRequestModel = filterModel.toReportRequestModel();
                CustomReportResponseModel reportResponseModel = customReportService.runReport(sessionModel,reportRequestModel);

                if(reportResponseModel instanceof CustomReportResultCSVModel){
                    CustomReportResultCSVModel csvResult = (CustomReportResultCSVModel)reportResponseModel;
                    String csv = csvResult.getResultAsCsv();
                    StringReader csvReader = new StringReader(csv);

                    CSVParser csvParser = CSVFormat.EXCEL.parse(csvReader);

                    List<CSVRecord> csvRecords = csvParser.getRecords();

                    Map<String,Integer> columnMapping = null;

                    for(CSVRecord record : csvRecords) {
                        if (columnMapping == null) {
                            columnMapping = new HashMap<>();

                            for (int i = 0; i < record.size(); i++) {

                                String column = record.get(i);

                                switch(column){
                                    case "Impressions":
                                        column = "Demand Source Impressions";
                                        break;
                                }

                                columnMapping.put(column, i);
                            }

                        } else {
                            Integer demandSourceId = Integer.parseInt(record.get(columnMapping.get("Demand Source ID")));
                            DemandSource demandSource = demandSourceRepository.findOne(demandSourceId);
                            DemandSourceModel demandSourceModel = new DemandSourceModel(demandSource);

                            if (demandSourceModel != null) {
                                demandSourceModel.setSummaryImpressions(Long.parseLong(record.get(columnMapping.get("Demand Source Impressions")).replace(",","")));

                                double ecpm = Double.parseDouble(record.get(columnMapping.get("eCPM")));
                                int ecpmInCents = (int) Math.round(ecpm * 100.0);

                                demandSourceModel.setSummaryECPM(ecpmInCents);

                                if(filterModel.getStartDate().isAfter(LocalDate.of(2015,12,31))){
                                    demandSourceModel.setSummaryFillRate(Integer.parseInt(record.get(columnMapping.get("Fill Rate"))));

                                    double rcpm = Double.parseDouble(record.get(columnMapping.get("rCPM")));
                                    int rcpmInCents = (int) Math.round(rcpm * 100.0);

                                    demandSourceModel.setSummaryRCPM(rcpmInCents);
                                } else {
                                    demandSourceModel.setSummaryFillRate(0);
                                    demandSourceModel.setSummaryRCPM(0);
                                }


                                if(demandSourceModel.getSummaryImpressions() > 0 || filterModel.isShowInactive()){
                                    allDemandSourceModels.add(demandSourceModel);
                                }
                            }
                        }
                    }
                }

            } else {
                Iterable<DemandSource> demandSources = demandSourceRepository.findByAccountOrderByName(account);

                for(DemandSource demandSource : demandSources){

                    DemandSourceModel demandSourceModel = new DemandSourceModel(demandSource);

                    if(demandSourceModel.getSummaryImpressions() > 0 || filterModel.isShowInactive()){
                        allDemandSourceModels.add(demandSourceModel);
                    }
                }
            }
        } else {
            Iterable<DemandSource> demandSources = demandSourceRepository.findByAccountOrderByName(account);

            for(DemandSource demandSource : demandSources){

                DemandSourceModel demandSourceModel = new DemandSourceModel(demandSource);

                if(demandSourceModel.getSummaryImpressions() > 0 || filterModel.isShowInactive()){
                    allDemandSourceModels.add(demandSourceModel);
                }
            }
        }

        DemandSourceListModel demandSourceListModel = new DemandSourceListModel(allDemandSourceModels);

        AccountType accountType = sessionModel.getUser().getAccountType();

        if(accountType == AccountType.NETWORK || accountType == AccountType.ROOT){
            AccountNetwork network = (AccountNetwork)account;
            //demandSourcePriorityService.orderByPriorty(demandSourceListModel,filterModel,network);
        }

        demandSourceListModel.getDemandSources();

        demandSourceListModel.setPageable(pageable);

        return demandSourceListModel;
    }

    public Iterable<DemandSource> getDemandSourcesForAccount(Account account){
        return demandSourceRepository.findByAccountOrderByName(account);
    }

    @Transactional(readOnly = true)
    public DemandSourceModel getDemandSource(int demandSourceId){
        DemandSource demandSource = demandSourceRepository.findOne(demandSourceId);
        return new DemandSourceModel(demandSource);
    }

    @Transactional(readOnly = true)
    public AccountWebModel getAccountForDemandSource(int demandSourceId){
        DemandSource demandSource = demandSourceRepository.findOne(demandSourceId);
        Account account = demandSource.getAccount();

        if(account == null){
            return AccountWebModel.getRootAccount();
        }

        return new AccountWebModel(account);
    }

    @Transactional
    public void updateDemandSource(DemandSourceModel demandSourceModel, UserModel userModel){
        DemandSource demandSource = demandSourceRepository.findOne(demandSourceModel.getDemandSourceId());
        demandSource.setName(demandSourceModel.getName());
        demandSourceRepository.save(demandSource);
    }

    @Transactional
    public void deleteDemandSource(SessionModel sessionModel, int demandSourceId) throws Exception {
        AnalyticsRestTemplate restTemplate = analyticsService.getRestTemplateForUser(sessionModel);
        restTemplate.delete("http://localhost:8080/analytics/api/1.0/demand-partners/platform-" + demandSourceId);

        DemandSource demandSource = demandSourceRepository.findOne(demandSourceId);
        Iterable<DemandSourceConnection> connections = demandSourceConnectionRepository.findByDemandSource(demandSource);

        for(DemandSourceConnection demandSourceConnection : connections){
            demandSourceConnectionRepository.delete(demandSourceConnection);
        }

        Iterable<LineItem> lineItems = lineItemRepository.findByDemandSource(demandSource);

        for(LineItem lineItem : lineItems){

            for(LineItemTargeting targeting : lineItem.getTargeting()){
                lineItemTargetingRepository.delete(targeting);
            }

            lineItem.getTargeting().clear();

            for(LineItemAd ad : lineItem.getAds()){
                adRepository.delete(ad);
            }

            lineItem.getAds().clear();

            lineItemRepository.delete(lineItem);
        }

        Iterable<Creative> creatives = creativeRepository.findByDemandSource(demandSource);

        for(Creative creative : creatives){
            creativeRepository.delete(creative);
        }

        demandSourceRepository.delete(demandSource);
    }

    public void requestDemandSource(UserModel userModel, DemandSourceRequestModel demandSourceRequestModel){

        StringBuilder message = new StringBuilder();
        message.append("Demand Source Information:\r\n");
        message.append("Name: ").append(demandSourceRequestModel.getName()).append("\r\n");
        message.append("Login URL: ").append(demandSourceRequestModel.getLoginUrl()).append("\r\n");
        message.append("Username: ").append(demandSourceRequestModel.getUsername()).append("\r\n");
        message.append("Password: ").append(demandSourceRequestModel.getPassword()).append("\r\n");
        message.append("\r\n");
        message.append("Account Information:\r\n");
        message.append("Account: ").append(userModel.getAccountName()).append("\r\n");
        message.append("Requester: ").append(userModel.getFirstName()).append(" ").append(userModel.getLastName()).append("\r\n");
        message.append("Email Address: ").append(userModel.getEmailAddress()).append("\r\n");
        message.append("Please contact at above email address when request is fulfilled or denied");

        SimpleMailMessage requestMessage = new SimpleMailMessage();
        requestMessage.setFrom("hello@redpandaplatform.com");
        requestMessage.setTo("demand-source-requests@wahwahnetworks.com");
        requestMessage.setReplyTo(userModel.getEmailAddress());
        requestMessage.setSubject("Demand Source Request: " + demandSourceRequestModel.getName());
        requestMessage.setText(message.toString());

        mailSender.send(requestMessage);
    }

    @Transactional(readOnly = true)
    public DemandSourceConnectionListModel getConnectionsForDemandSource(int demandSourceId, Pageable pageable) throws IOException {
        Page<DemandSourceConnection> demandSourceConnections = demandSourceConnectionRepository.findByDemandSourceId(demandSourceId,pageable);
        DemandSourceConnectionListModel demandSourceConnectionListModel = new DemandSourceConnectionListModel(demandSourceConnections);
        return demandSourceConnectionListModel;
    }

    @Transactional(readOnly = true)
    public List<DemandSourceConnectionModel> getConnectionsForDemandSource(int demandSourceId) throws IOException {
        DemandSource demandSource = demandSourceRepository.findOne(demandSourceId);
        Iterable<DemandSourceConnection> demandSourceConnections = demandSourceConnectionRepository.findByDemandSource(demandSource);

        List<DemandSourceConnectionModel> demandSourceConnectionModelList = new ArrayList<>();

        for(DemandSourceConnection demandSourceConnection : demandSourceConnections){
            DemandSourceConnectionModel connectionModel = new DemandSourceConnectionModel(demandSourceConnection);
            demandSourceConnectionModelList.add(connectionModel);
        }

        return demandSourceConnectionModelList;
    }

    @Transactional(readOnly = true)
    public DemandSourceConnectionModel getConnectionForDemandSource(int demandSourceId, int demandSourceConnectionId) throws IOException {
        DemandSourceConnection demandSourceConnection = demandSourceConnectionRepository.findByDemandSourceIdAndId(demandSourceId,demandSourceConnectionId);
        return new DemandSourceConnectionModel(demandSourceConnection);
    }

    @Transactional
    public void updateConnection(DemandSourceConnectionModel demandSourceConnectionModel) throws IOException {
        DemandSourceConnectionType demandSourceConnectionType = demandSourceConnectionTypeRepository.findByTypeKey(demandSourceConnectionModel.getConnectionTypeKey());

        DemandSourceConnection demandSourceConnection = demandSourceConnectionRepository.findOne(demandSourceConnectionModel.getId());
        demandSourceConnection.setDemandSourceConnectionType(demandSourceConnectionType);

        String connectionDetails = JsonSerializer.serialize(demandSourceConnectionModel.getConnectionDetailMap());
        demandSourceConnection.setConnectionDetails(connectionDetails);

        demandSourceConnectionRepository.save(demandSourceConnection);
    }

    @Transactional
    public void deleteConnection(DemandSourceConnectionModel demandSourceConnectionModel){
        DemandSourceConnection demandSourceConnection = demandSourceConnectionRepository.findOne(demandSourceConnectionModel.getId());
        demandSourceConnectionRepository.delete(demandSourceConnection);
    }

    @Transactional(readOnly = true)
    public int getDemandSourceCountForAccountId(int accountId){
        Account account = accountRepository.findOne(accountId);
        return demandSourceRepository.countByAccount(account);
    }

    @Transactional(readOnly = true)
    public DemandSourceConnectionTypeListModel getConnectionTypes(){
        List<DemandSourceConnectionType> demandSourceConnectionTypeList = demandSourceConnectionTypeRepository.findAll();

        List<DemandSourceConnectionTypeModel> demandSourceConnectionTypeModels = new ArrayList<>();

        for(DemandSourceConnectionType demandSourceConnectionType : demandSourceConnectionTypeList){
            DemandSourceConnectionTypeModel demandSourceConnectionTypeModel = new DemandSourceConnectionTypeModel();
            demandSourceConnectionTypeModel.setName(demandSourceConnectionType.getName());
            demandSourceConnectionTypeModel.setConnectionMetaData(demandSourceConnectionType.getConnectionMetaData());
            demandSourceConnectionTypeModel.setTypeKey(demandSourceConnectionType.getTypeKey());

            demandSourceConnectionTypeModels.add(demandSourceConnectionTypeModel);
        }

        return new DemandSourceConnectionTypeListModel(demandSourceConnectionTypeModels);
    }

    @Transactional(readOnly = true)
    public DemandSourceConnectionType getConnectionTypeForKey(String typeKey){
        return demandSourceConnectionTypeRepository.findByTypeKey(typeKey);
    }

    public PopularDemandSourceListModel getPopularDemandSources() throws IOException {
        Resource popularDemandSourcesResource = applicationContext.getResource("classpath:data/popular_demand_sources.txt");
        List<String> popularDemandSourceList = IOUtils.readLines(popularDemandSourcesResource.getInputStream(), Charset.forName("UTF-8"));

        List<PopularDemandSourceModel> demandSourceModelList = new ArrayList<>();

        for(String demandSourceData : popularDemandSourceList){
            String[] demandSourceDataParts = demandSourceData.split(",");

            String demandSourceName = demandSourceDataParts[0];
            String connectionTypeName = demandSourceDataParts[1];

            PopularDemandSourceModel demandSourceModel = new PopularDemandSourceModel();
            demandSourceModel.setDemandSourceName(demandSourceName);
            demandSourceModel.setConnectionTypeKey(connectionTypeName);

            if(demandSourceDataParts.length == 3){
                String extraData = demandSourceDataParts[2];
                Map<String,String> extraDataMap = JsonSerializer.deserialize(extraData,Map.class);
                demandSourceModel.setExtraData(extraDataMap);
            }

            demandSourceModelList.add(demandSourceModel);
        }

        return new PopularDemandSourceListModel(demandSourceModelList);
    }

    @Transactional
    public DemandSourceModel createDemandSourceFromModel(DemandSourceModel demandSourceModel, Integer accountId){

        Account account = null;

        if(accountId != null){
            account = accountRepository.findOne(accountId);
        }

        DemandSource demandSource = new DemandSource();
        demandSource.setAccount(account);
        demandSource.setName(demandSourceModel.getName());
        demandSource.setDirectAdvertiser(false);
        demandSourceRepository.save(demandSource);

        demandSourceModel.setDemandSourceId(demandSource.getId());

        return demandSourceModel;
    }

    public DemandSourceConnectionModel createDemandSourceConnection(DemandSourceConnectionModel demandSourceConnectionModel, int demandSourceId) throws IOException {

        DemandSource demandSource = demandSourceRepository.findOne(demandSourceId);
        DemandSourceConnectionType demandSourceConnectionType = demandSourceConnectionTypeRepository.findByTypeKey(demandSourceConnectionModel.getConnectionTypeKey());

        DemandSourceConnection demandSourceConnection = new DemandSourceConnection();
        demandSourceConnection.setDemandSource(demandSource);
        demandSourceConnection.setDemandSourceConnectionType(demandSourceConnectionType);

        String connectionDetails = JsonSerializer.serialize(demandSourceConnectionModel.getConnectionDetailMap());
        demandSourceConnection.setConnectionDetails(connectionDetails);

        demandSourceConnectionRepository.save(demandSourceConnection);

        return new DemandSourceConnectionModel(demandSourceConnection);
    }

    public boolean validateCredentials(SessionModel sessionModel, ValidateDemandSourceCredentialsRequestModel validateDemandSourceCredentialsRequestModel) throws Exception {
        AnalyticsRestTemplate restTemplate = analyticsService.getRestTemplateForUser(sessionModel);
        return restTemplate.postForObject("http://localhost:8080/analytics/api/1.0/demand-partners/validate-credentials",validateDemandSourceCredentialsRequestModel,Boolean.class);
    }

    @PostConstruct
    @Transactional
    public void initializeConnectionTypes() throws IOException {
        Resource demandSourceConnectionTypeResource = applicationContext.getResource("classpath:data/demand_source_connection_types.txt");
        List<String> demandSourceConnectionTypeList = IOUtils.readLines(demandSourceConnectionTypeResource.getInputStream(), Charset.forName("UTF-8"));

        for(String demandSourceConnectionType : demandSourceConnectionTypeList){
            String[] demandSourceConnectionTypeParts = demandSourceConnectionType.split(",");

            String typeKey = demandSourceConnectionTypeParts[0];
            String name = demandSourceConnectionTypeParts[1];

            String[] metadataParts = Arrays.copyOfRange(demandSourceConnectionTypeParts,2,demandSourceConnectionTypeParts.length);
            String metadata = StringUtils.join(metadataParts,",");

            DemandSourceConnectionType connectionType = demandSourceConnectionTypeRepository.findByTypeKey(typeKey);

            if(connectionType == null){
                connectionType = new DemandSourceConnectionType();
            }

            connectionType.setTypeKey(typeKey);
            connectionType.setName(name);
            connectionType.setConnectionMetaData(metadata);

            demandSourceConnectionTypeRepository.save(connectionType);
        }
    }

    @Transactional(readOnly = true)
    public List<DemandSourcePlacementModel> getPlacementsForDemandSourceId(int demandSourceId){
        DemandSource demandSource = demandSourceRepository.findOne(demandSourceId);
        Iterable<DemandSourcePlacement> placements = placementRepository.findByDemandSource(demandSource);

        List<DemandSourcePlacementModel> placementModelList = new ArrayList<>();

        for(DemandSourcePlacement placement : placements){
            DemandSourcePlacementModel placementModel = new DemandSourcePlacementModel(placement);
            placementModelList.add(placementModel);
        }

        return placementModelList;
    }

    @Transactional
    public void updatePlacements(List<DemandSourcePlacementModel> placementModelList){
        for(DemandSourcePlacementModel placementModel : placementModelList){
            DemandSourcePlacement placement = placementRepository.findOne(placementModel.getId());

            List<Site> sites = new ArrayList<>();

            for(int siteId : placementModel.getTargetedSiteIds()){
                Site site = siteRepository.findOne(siteId);
                sites.add(site);
            }

            placement.setSites(sites);

            placementRepository.save(placement);
        }
    }
}
