package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.data.entities.AccountNetwork;
import com.wahwahnetworks.platform.data.entities.AdUnit;
import com.wahwahnetworks.platform.data.repos.AdUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by jhaygood on 4/27/16.
 * Updated by Brian.Bober 5/9/16
 */

@Service
public class AdUnitService
{

    @Autowired
    private AdUnitRepository adUnitRepository;

    public List<AdUnit> getBackupAdUnits(AccountNetwork accountNetwork)
    {
        Integer accountNetworkId = null;

        if (accountNetwork != null)
        {
            accountNetworkId = accountNetwork.getId();
        }

        return adUnitRepository.findAllBackupAdUnitsByNetworkId(accountNetworkId);
    }

    public List<AdUnit> getVideoAdUnits(AccountNetwork accountNetwork)
    {
        Integer accountNetworkId = null;

        if (accountNetwork != null)
        {
            accountNetworkId = accountNetwork.getId();
        }

        return adUnitRepository.findAllVideoAdUnitsByNetworkId(accountNetworkId);
    }

    public List<AdUnit> getMobileAdUnits(AccountNetwork accountNetwork)
    {
        Integer accountNetworkId = null;

        if (accountNetwork != null)
        {
            accountNetworkId = accountNetwork.getId();
        }

        return adUnitRepository.findAllMobileAdUnitsByNetworkId(accountNetworkId);
    }


}
