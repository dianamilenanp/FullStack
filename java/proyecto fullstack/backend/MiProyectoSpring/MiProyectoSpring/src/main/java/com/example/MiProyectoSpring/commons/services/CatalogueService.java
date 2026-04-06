package com.ptesa.bmc.coreservice.services;

import com.ptesa.bmc.coreservice.dtos.catalogues.*;
import com.ptesa.bmc.coreservice.dtos.common.BrokerData;
import com.ptesa.bmc.coreservice.models.Profile;
import com.ptesa.bmc.coreservice.repositories.*;
import com.ptesa.bmc.coreservice.utils.common.ObjectMapperUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Services related to catalogues.
 */
@Service
@AllArgsConstructor
public class CatalogueService {

    // Constants


    // Fields

    private final IdentificationTypeRepository identificationTypeRepository;
    private final RoleRepository roleRepository;

    // Logic

    /**
     * Find all identification types.
     * @return All identification types.
     */
    public List<IdentificationTypeData> findIdentificationTypes() {
        return ObjectMapperUtil.mapAll(this.identificationTypeRepository.findAll(), IdentificationTypeData.class);
    }
    /**
     * Find all roles by profile.
     * @param profileId Profile ID.
     * @return All roles by profile.
     */
    public List<RoleData> findRolesByProfile(Long profileId) {
        return ObjectMapperUtil.mapAll(this.roleRepository.findRolesByProfile(Profile.of(profileId)), RoleData.class);
    }

}
