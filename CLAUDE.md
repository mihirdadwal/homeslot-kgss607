# HomeSlot – Real Estate Listing & Booking Platform (KGSS 607)

## Who I am
Mihir Dadwal, Software Engineer at KGS Technology Group.
Project Coordinator: Vaishali Pujari.
Stack expertise: Angular 17+ (frontend) + Java Spring Boot (backend).

## Project overview
HomeSlot is a role-based web platform with three actors:
- **Buyer** — searches and books properties
- **Builder** — lists and manages property inventory
- **Admin** — oversees platform operations, moderation, reports

Overall timeline: 09 Aug 2026 – 19 Sep 2026, split into 7 phases
(per my individual assignment; the full project reference has 15
workflow phases which my 7 phases collapse).

## Where I am now
**See `PHASE_STATUS.md` at the project root.** That file is the
source of truth for which phase is complete, which is active, and
what the current week's scope is. Read it at the start of every
session — it changes as the project progresses. This CLAUDE.md
file does NOT track phase state.

## Onboarding sequence for a fresh session
When starting a new session, read files in this order:

1. `PHASE_STATUS.md` — current phase, dates, scope
2. `docs/reference/Task Assignment – Mihir Dadwal.pdf` — my full
   7-phase assignment (skim once, refer back per phase)
3. `docs/reference/KGSS 607 -Homeslot Project Workflow.pdf` — the
   full 15-phase project workflow (reference for what exists across
   the whole platform, not just my current phase)
4. Deliverables from all completed phases (see PHASE_STATUS.md
   for the list) — these carry forward architecture decisions,
   entities, APIs. Everything in later phases builds on them.
5. Any active-phase notes referenced in PHASE_STATUS.md

## Tech stack (locked in during Phase 1 — do not change without discussion)
- **Frontend**: Angular 17+ (standalone components, signals, new
  control-flow syntax), Angular Material + Tailwind utilities
- **Backend**: Java 21, Spring Boot 3.x (Web, Security, Data JPA,
  Validation)
- **Primary DB**: MySQL 8
- **Cache**: Redis (OTP storage with TTL, rate limiting, session data)
- **Auth**: JWT (short-lived access + rotating refresh)
- **Email**: SendGrid
- **SMS/OTP**: Twilio
- **Container**: Docker Compose
- **CI/CD**: GitHub Actions

Full rationale is in the Phase 1 deliverable
(`docs/phase1/Phase1_Technical_Requirement_Analysis_Mihir_Dadwal.docx`).

## Reference documents in docs/reference/

- **`KGSS 607 -Homeslot Project Workflow.pdf`** — the full 15-phase
  project workflow from KGS. Reference for what features exist
  across the whole platform.
- **`Task Assignment – Mihir Dadwal.pdf`** — my individual 7-phase
  assignment. Authoritative source for per-phase scope, dates,
  and deliverables.
- **`Weekly Status Report-Template (KGS).docx`** — KGS template
  that every WEEKLY status report must follow. Submitted at the
  end of each phase week.
- **`Monthly Status Report template KGS.docx`** — KGS template
  for the MONTHLY status report. This report consolidates four
  weekly reports into a single monthly summary.

## Working agreements for this codebase

- **Scope discipline** — only build what belongs in the current phase
  (see PHASE_STATUS.md). Don't build ahead. Keep entities extensible
  but don't add fields for future-phase features yet.
- **RBAC enforced server-side** — UI restrictions are advisory only.
  Every protected endpoint must have a role check.
- **Passwords** — BCrypt, cost factor 12.
- **JWT** — 15-minute access token, 7-day refresh token, rotation on use.
- **OTP** — 6-digit numeric, 5-minute TTL in Redis, max 3 attempts.
- **Rate limiting** — on all auth endpoints.
- **Commits** — Conventional Commits (`feat(auth): ...`, `fix(auth): ...`).
- **Tests** — unit tests alongside services; integration tests for
  end-to-end flows.

## End-of-phase ritual (weekly)

At the end of every phase week:
1. Generate a weekly status report following the exact structure
   in `docs/reference/Weekly Status Report-Template (KGS).docx`.
2. Reference the prior week's report as a style guide (e.g.
   `docs/phase1/Weekly_Status_Report_Week1_Mihir_Dadwal.docx`
   for the Week 2 report).
3. Screenshots for the report are in `docs/phaseN/screenshots/`.
4. Save the completed report and any phase deliverables into
   `docs/phaseN/`.
5. Update `PHASE_STATUS.md` to mark the phase complete and set
   the next phase as active.

## Monthly report ritual

At the end of every 4-week cycle, consolidate the four weekly
reports into a single monthly status report:
1. Follow the structure in
   `docs/reference/Monthly Status Report template KGS.docx`.
2. Source content from the four weekly reports in the relevant
   `docs/phaseN/` folders.
3. Save into `docs/monthly/` as
   `Monthly_Status_Report_MonthN_Mihir_Dadwal.docx`.
4. Note in `PHASE_STATUS.md` under a "Monthly reports submitted"
   section.

## Project cadence
- **Phase weeks**: 7 phases, ~1 week each, 09 Aug – 19 Sep 2026.
- **Weekly reports**: at the end of each phase week (7 total).
- **Monthly reports**: consolidate 4 weekly reports each. The
  project spans roughly 6 weeks, so expect 1 monthly report
  (weeks 1–4) plus a final wrap-up covering weeks 5–7 (or handle
  weeks 5–7 in the handover doc — confirm with Vaishali).