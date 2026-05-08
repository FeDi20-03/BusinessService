package com.tunisales.business.repository;

import com.tunisales.business.domain.CreditNote;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface CreditNoteRepository extends JpaRepository<CreditNote, Long>, JpaSpecificationExecutor<CreditNote> {}
