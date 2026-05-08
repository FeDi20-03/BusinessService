package com.tunisales.business.web.rest;

import com.tunisales.business.service.ComplaintService;
import com.tunisales.business.service.dto.ComplaintDTO;
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

/** Sub-step 2.10 — REST controller for complaints. */
@RestController
@RequestMapping("/api")
public class ComplaintResource {

    private final Logger log = LoggerFactory.getLogger(ComplaintResource.class);

    private final ComplaintService complaintService;

    public ComplaintResource(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping("/complaints")
    public ResponseEntity<ComplaintDTO> createComplaint(@Valid @RequestBody ComplaintDTO dto) throws URISyntaxException {
        log.debug("REST request to save Complaint: {}", dto);
        ComplaintDTO result = complaintService.create(dto);
        return ResponseEntity.created(new URI("/api/complaints/" + result.getId())).body(result);
    }

    @PutMapping("/complaints/{id}")
    public ResponseEntity<ComplaintDTO> updateComplaint(@PathVariable Long id, @Valid @RequestBody ComplaintDTO dto) {
        log.debug("REST request to update Complaint {}: {}", id, dto);
        dto.setId(id);
        return ResponseEntity.ok(complaintService.update(dto));
    }

    @PostMapping("/complaints/{id}/resolve")
    @PreAuthorize("hasAuthority('ROLE_ADMIN_COMMERCIAL')")
    public ResponseEntity<ComplaintDTO> resolveComplaint(@PathVariable Long id) {
        log.debug("REST request to resolve Complaint {}", id);
        return ResponseEntity.ok(complaintService.resolve(id));
    }

    @GetMapping("/complaints")
    public ResponseEntity<List<ComplaintDTO>> getAllComplaints(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        Page<ComplaintDTO> page = complaintService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/complaints/{id}")
    public ResponseEntity<ComplaintDTO> getComplaint(@PathVariable Long id) {
        return ResponseUtil.wrapOrNotFound(complaintService.findOne(id));
    }

    @DeleteMapping("/complaints/{id}")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Long id) {
        complaintService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
