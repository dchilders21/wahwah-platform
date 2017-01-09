package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.data.entities.Creative;
import com.wahwahnetworks.platform.data.entities.DemandSource;
import com.wahwahnetworks.platform.models.SessionModel;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by jhaygood on 4/14/16.
 */

@Service
public interface ICreativeImportService {
    List<Creative> importCreatives(SessionModel sessionModel, DemandSource demandSource);
}
