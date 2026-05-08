---
name: Respect JHipster scaffolding pattern
description: User insists on strict adherence to the JHipster pattern when extending generated entities/services/resources — preserve needle comments, mirror existing class structure, keep DTO/Mapper/Repository/Resource quartet aligned.
type: feedback
---

Rule: when extending a JHipster-generated module, follow the existing scaffolding precisely:
- Add new fields to `Entity` AND `EntityDTO` AND (if filtering is needed) `EntityCriteria`.
- Liquibase: incremental changesets go in `src/main/resources/config/liquibase/changelog/`, named `<timestamp>_<description>.xml`, included in `master.xml` **before** the `jhipster-needle-liquibase-add-incremental-changelog` needle line. Use `<addColumn>` for incremental column additions, never modify the original `_added_entity_*.xml` file.
- Preserve all `// jhipster-needle-*` and `<!-- jhipster-needle-* -->` comments — they let the JHipster generator re-run incrementally.
- For business actions on an entity (workflow transitions), add methods to the generated `XxxService` (don't create a parallel `XxxWorkflowService`) and endpoints to `XxxResource` (don't create a parallel `XxxWorkflowResource`).
- Test classes: keep the generated `*ResourceIT` for CRUD; add a separate `*WorkflowResourceIT` only for workflow-specific endpoints. Unit tests for service methods use Mockito with `lenient()` stubs in `@BeforeEach` because Mockito strict mode complains when negative-path tests don't reach the stubbed call.

Why: the user has chosen JHipster as a productivity foundation. Diverging from its conventions would forfeit code-generation benefits and confuse future maintainers (and the user) who expect the JHipster layout.
How to apply: before adding new files, look for the JHipster-generated counterpart and extend it. Before refactoring, check if the change can be expressed as an additive scaffolding extension first.
