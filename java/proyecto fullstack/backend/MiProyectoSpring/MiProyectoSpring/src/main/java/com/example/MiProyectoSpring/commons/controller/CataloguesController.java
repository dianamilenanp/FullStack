package com.ptesa.bmc.coreservice.controllers;

import com.ptesa.bmc.coreservice.dtos.catalogues.*;
import com.ptesa.bmc.coreservice.dtos.common.BrokerData;
import com.ptesa.bmc.coreservice.services.CatalogueService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Manage endpoints for catalogues.
 */
@RestController
@RequestMapping("/catalogues")
@AllArgsConstructor
public class CataloguesController {

    // Fields

    private final CatalogueService catalogueService;

    // Logic

    /**
     * Find all identification types.
     * @return All identification types.
     */
    @Cacheable("identification-types")
    @GetMapping("/identification-types")
    public List<IdentificationTypeData> findIdentificationTypes() {
        return catalogueService.findIdentificationTypes();
    }

}
