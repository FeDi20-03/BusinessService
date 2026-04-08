package com.tunisales.business.service;

import com.tunisales.business.domain.Mission;
import com.tunisales.business.repository.MissionRepository;
import com.tunisales.business.service.dto.MissionDTO;
import com.tunisales.business.service.mapper.MissionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Mission}.
 */
@Service
@Transactional
public class MissionService {

    private final Logger log = LoggerFactory.getLogger(MissionService.class);

    private final MissionRepository missionRepository;

    private final MissionMapper missionMapper;

    public MissionService(MissionRepository missionRepository, MissionMapper missionMapper) {
        this.missionRepository = missionRepository;
        this.missionMapper = missionMapper;
    }

    /**
     * Save a mission.
     *
     * @param missionDTO the entity to save.
     * @return the persisted entity.
     */
    public MissionDTO save(MissionDTO missionDTO) {
        log.debug("Request to save Mission : {}", missionDTO);
        Mission mission = missionMapper.toEntity(missionDTO);
        mission = missionRepository.save(mission);
        return missionMapper.toDto(mission);
    }

    /**
     * Update a mission.
     *
     * @param missionDTO the entity to save.
     * @return the persisted entity.
     */
    public MissionDTO update(MissionDTO missionDTO) {
        log.debug("Request to update Mission : {}", missionDTO);
        Mission mission = missionMapper.toEntity(missionDTO);
        mission = missionRepository.save(mission);
        return missionMapper.toDto(mission);
    }

    /**
     * Partially update a mission.
     *
     * @param missionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MissionDTO> partialUpdate(MissionDTO missionDTO) {
        log.debug("Request to partially update Mission : {}", missionDTO);

        return missionRepository
            .findById(missionDTO.getId())
            .map(existingMission -> {
                missionMapper.partialUpdate(existingMission, missionDTO);

                return existingMission;
            })
            .map(missionRepository::save)
            .map(missionMapper::toDto);
    }

    /**
     * Get all the missions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<MissionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Missions");
        return missionRepository.findAll(pageable).map(missionMapper::toDto);
    }

    /**
     * Get one mission by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MissionDTO> findOne(Long id) {
        log.debug("Request to get Mission : {}", id);
        return missionRepository.findById(id).map(missionMapper::toDto);
    }

    /**
     * Delete the mission by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Mission : {}", id);
        missionRepository.deleteById(id);
    }
}
