package com.tunisales.business.web.rest;

import com.tunisales.business.service.VehicleInspectionService;
import com.tunisales.business.service.dto.VehicleInspectionDTO;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * Sub-step 2.13 — REST controller for vehicle inspections.
 *
 * <p>Write operations (POST/PUT) require {@code ROLE_CHEF_PARC}; reads are
 * available to {@code ROLE_CHEF_PARC} and {@code ROLE_ADMIN_COMMERCIAL}.</p>
 */
@RestController
@RequestMapping("/api")
public class VehicleInspectionResource {

    private final Logger log = LoggerFactory.getLogger(VehicleInspectionResource.class);

    private final VehicleInspectionService vehicleInspectionService;

    public VehicleInspectionResource(VehicleInspectionService vehicleInspectionService) {
        this.vehicleInspectionService = vehicleInspectionService;
    }

    @PostMapping("/vehicle-inspections")
    @PreAuthorize("hasAuthority('ROLE_CHEF_PARC')")
    public ResponseEntity<VehicleInspectionDTO> createVehicleInspection(@Valid @RequestBody VehicleInspectionDTO dto)
        throws URISyntaxException {
        log.debug("REST request to save VehicleInspection: {}", dto);
        VehicleInspectionDTO result = vehicleInspectionService.create(dto);
        return ResponseEntity.created(new URI("/api/vehicle-inspections/" + result.getId())).body(result);
    }

    @PutMapping("/vehicle-inspections/{id}")
    @PreAuthorize("hasAuthority('ROLE_CHEF_PARC')")
    public ResponseEntity<VehicleInspectionDTO> updateVehicleInspection(
        @PathVariable Long id,
        @Valid @RequestBody VehicleInspectionDTO dto
    ) {
        log.debug("REST request to update VehicleInspection {}: {}", id, dto);
        dto.setId(id);
        return ResponseEntity.ok(vehicleInspectionService.update(dto));
    }

    @GetMapping("/vehicle-inspections")
    @PreAuthorize("hasAnyAuthority('ROLE_CHEF_PARC','ROLE_ADMIN_COMMERCIAL')")
    public ResponseEntity<List<VehicleInspectionDTO>> getAllVehicleInspections(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        Page<VehicleInspectionDTO> page = vehicleInspectionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/vehicle-inspections/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_CHEF_PARC','ROLE_ADMIN_COMMERCIAL')")
    public ResponseEntity<VehicleInspectionDTO> getVehicleInspection(@PathVariable Long id) {
        return ResponseUtil.wrapOrNotFound(vehicleInspectionService.findOne(id));
    }

    @DeleteMapping("/vehicle-inspections/{id}")
    @PreAuthorize("hasAuthority('ROLE_CHEF_PARC')")
    public ResponseEntity<Void> deleteVehicleInspection(@PathVariable Long id) {
        vehicleInspectionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
