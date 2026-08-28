# Working Agreements & Guidelines for HomeSlot (KGSS 607)

## Engineering & Scope Discipline
1. **Strict Scope Discipline**: Build only what belongs to the active phase specified in [PHASE_STATUS.md](file:///d:/KGS/KGS%20Real%20estate%20project%20code/PHASE_STATUS.md). Keep entity models extensible for future phases, but do NOT introduce database fields, endpoints, or UI for future phases prematurely.
2. **Server-Side Security First**: UI restrictions and route guards are purely advisory. Every protected endpoint must enforce Role-Based Access Control (RBAC) at the Spring Security layer.
3. **Authentication & Token Policy**:
   - Passwords must be hashed using BCrypt with cost factor 12.
   - JWT tokens: 15-minute expiration for access tokens, 7-day expiration for refresh tokens. Refresh token rotation must be enforced upon usage.
   - OTP: 6-digit numeric, stored in Redis with a 5-minute Time-To-Live (TTL), maximum 3 failed attempts per OTP.
   - Rate limiting: Implemented via Redis bucket/counter on all auth endpoints (registration, login, OTP request/verify, forgot password).
4. **Commit Standard**: Follow Conventional Commits format (`feat(auth): ...`, `fix(auth): ...`, `docs(phase2): ...`).

## Reporting & Rituals
1. **Weekly Status Reports**: Generated at the end of each phase week using the format established in `docs/reference/Weekly Status Report-Template (KGS) 1.docx` and modeled after `docs/phase1/Weekly_Status_Report_Week1_Mihir_Dadwal.docx`.
2. **Screenshots & Artifacts**: Store visual proof of UI screens, API responses (Postman/Swagger), and DB tables under `docs/phaseN/screenshots/`.
3. **Monthly Consolidation**: Consolidate every 4 weekly reports into a monthly report in `docs/monthly/` using `docs/reference/Monthly Status Report template KGS.docx`.
