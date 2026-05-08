package com.tunisales.business.web.rest;

import com.tunisales.business.service.ZoneService;
import com.tunisales.business.service.dto.ZoneDTO;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/** Sub-step 2.8 — REST controller for zones. */
@RestController
@RequestMapping("/api")
public class ZoneResource {

    private final Logger log = LoggerFactory.getLogger(ZoneResource.class);

    private final ZoneService zoneService;

    public ZoneResource(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @PostMapping("/zones")
    public ResponseEntity<ZoneDTO> createZone(@Valid @RequestBody ZoneDTO dto) throws URISyntaxException {
        log.debug("REST request to save Zone: {}", dto);
        ZoneDTO result = zoneService.save(dto);
        return ResponseEntity.created(new URI("/api/zones/" + result.getId())).body(result);
    }

    @PutMapping("/zones/{id}")
    public ResponseEntity<ZoneDTO> updateZone(@PathVariable Long id, @Valid @RequestBody ZoneDTO dto) {
        log.debug("REST request to update Zone {}: {}", id, dto);
        dto.setId(id);
        return ResponseEntity.ok(zoneService.update(dto));
    }

    @GetMapping("/zones")
    public ResponseEntity<List<ZoneDTO>> getAllZones(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        Page<ZoneDTO> page = zoneService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/zones/{id}")
    public ResponseEntity<ZoneDTO> getZone(@PathVariable Long id) {
        return ResponseUtil.wrapOrNotFound(zoneService.findOne(id));
    }

    @DeleteMapping("/zones/{id}")
    public ResponseEntity<Void> deleteZone(@PathVariable Long id) {
        zoneService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
