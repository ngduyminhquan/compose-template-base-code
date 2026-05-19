---
feature_name: <FEATURE_NAME>
created_at: <CREATED_AT>
last_updated: <CREATED_AT>
current_phase: 0
status: in_progress
orchestrator_skill: feature-workflow-orchestrator
code_index_path: docs/code-index/
code_index_last_synced: <CREATED_AT>
---

# Feature Workflow Plan: <FEATURE_NAME>

> Plan này do skill `feature-workflow-orchestrator` quản lý.
> KHÔNG sửa thủ công các trường `Status`, `Completed`, `Outputs` - để orchestrator update.
> Có thể sửa `Notes` hoặc thêm `Re-opened` khi rollback.
> Khi phase quá to (xem ngưỡng trong SKILL.md), orchestrator sẽ thêm bảng `### Sub-phases` ngay trong section phase đó. Phase cha chỉ done khi mọi sub-phase done.

## Phase Overview

| # | Phase | Status | Output |
|---|-------|--------|--------|
| 0 | Initialize + Code Index init/refresh | [ ] | `docs/<FEATURE_NAME>/00-plan.md`, `docs/code-index/` snapshot |
| 1 | SRS | [ ] | `docs/<FEATURE_NAME>/01-srs.md` |
| 2 | Architecture (đối chiếu code index) | [ ] | `docs/<FEATURE_NAME>/02-ui-layer.md`, `docs/<FEATURE_NAME>/03-data-layer.md` |
| 3 | Data Layer Implementation + Index update | [ ] | Source code (Repository / DataSource / model) + `docs/code-index/` refresh |
| 4 | UI Layer Implementation + Index update | [ ] | Source code (Composable / ViewModel / UiState) + `docs/code-index/` refresh |
| 5 | Verify & Finalize + Index final pass | [ ] | Build pass + plan đóng + index đầy đủ |

Status legend: `[ ]` pending, `[~]` in-progress hoặc re-opened, `[x]` done.

---

## Phase 0 - Initialize

- Status: [ ]
- Started: <CREATED_AT>
- Completed:
- Outputs:
  -
- Code index:
  - Mode: (init / refresh)
  - Modules indexed:
  - New issues:
- Notes:

Mục tiêu: chốt feature name, output paths, tạo plan file, init/refresh `docs/code-index/`. Xác nhận thư mục `docs/<FEATURE_NAME>/` không xung đột với feature đã có.

User gate: User reply confirm feature name + paths + index summary.

---

## Phase 1 - SRS

- Status: [ ]
- Skill loaded: `srs-generator`
- Started:
- Completed:
- Outputs:
  -
- Notes:

Mục tiêu: tài liệu chức năng thuần (UI components, user actions, system actions). Không chứa thông tin kỹ thuật.

User gate: User duyệt nội dung `01-srs.md`.

---

## Phase 2 - Architecture

- Status: [ ]
- Skill loaded: `architecture-designer`
- Started:
- Completed:
- Outputs:
  -
  -
- Shared Repository Contract:
- Code index references:
  - Reused symbols (kept as-is):
  - Extended symbols:
  - New symbols planned:
- Notes:

Mục tiêu: tách UI Layer và Data Layer thành 2 file. Chốt Shared Repository Contract làm nguồn sự thật chung. Đối chiếu `docs/code-index/` để tái dùng / mở rộng / thêm mới.

User gate: User duyệt cả `02-ui-layer.md` và `03-data-layer.md`.

---

## Phase 3 - Data Layer Implementation

- Status: [ ]
- Reference rule: `.opencode/skills/architecture-designer/rules/repository-rule.md`
- Started:
- Completed:
- Files created:
  -
- Files modified:
  -
- Hilt bindings added:
  -
- Code index update:
  - Modules touched:
  - Edges added:
  - New issues:
- Notes:

Mục tiêu: implement Repository (interface + impl), DataSource, model theo `03-data-layer.md`. Đăng ký Hilt `@Binds` trong `di/RepositoryModule.kt`. Update `docs/code-index/` ngay sau khi implement xong.

User gate: User duyệt diff + danh sách file + index summary.

---

## Phase 4 - UI Layer Implementation

- Status: [ ]
- Skills loaded: `ui-android-compose`, `android-resource-policy` (khi cần)
- Reference rule: `.opencode/skills/architecture-designer/rules/viewmodel-mvi-rule.md`
- Started:
- Completed:
- Files created:
  -
- Files modified:
  -
- Resources added:
  -
- Navigation entries added:
  -
- Code index update:
  - Screens added:
  - ViewModels added:
  - Edges added (calls / navigation):
  - New issues:
- Notes:

Mục tiêu: implement Route, UiState, Intent, SideEffect, ViewModel (MVI), Screen Composable + Component con + `@Preview`. Đăng ký `entry<Route.X>` trong `AppNavigation.kt`. Update `docs/code-index/` ngay sau khi implement xong.

User gate: User duyệt diff + danh sách file + index summary.

---

## Phase 5 - Verify & Finalize

- Status: [ ]
- Started:
- Completed:
- Build command: `./gradlew.bat assembleDebug`
- Build result:
- Placeholder scan:
- Comment scan:
- Code index final pass:
  - Orphan nodes:
  - Issue count (CRITICAL / WARNING / INFO):
  - `_meta.md` last_updated:
- Notes:

Mục tiêu: build pass, không còn placeholder/comment, code index đồng bộ với code thực tế, plan đóng.

User gate: User reply done.

---

## Summary

Sẽ được điền sau Phase 5:

- Files created:
- Files modified:
- Resources added:
- Hilt bindings:
- Navigation entries:
- Build status:
- Git branch:
- Code index snapshot: `docs/code-index/_meta.md` (last_updated: )

---

## Rollback Log

Ghi nhận khi phase đã done bị mở lại:

- (chưa có)

---

## Sub-phase Reference (skeleton)

Khi phase chạm ngưỡng break (xem `.opencode/skills/feature-workflow-orchestrator/SKILL.md` mục "Quy tắc Break Phase"), orchestrator copy bảng dưới đây vào ngay sau dòng `Notes:` của phase tương ứng. KHÔNG sửa skeleton này; đây chỉ là khuôn mẫu.

```markdown
### Sub-phases

| ID | Scope | Status | Output | Completed |
|----|-------|--------|--------|-----------|
| P<n>.1 | <mô tả scope ngắn> | [ ] | <file path> | |
| P<n>.2 | <mô tả scope ngắn> | [ ] | <file path> | |

Sub-phase notes:

- P<n>.1: <ghi chú khi cần>
- P<n>.2:
```

Quy ước:

- `<n>` thay bằng số phase cha (3, 4...). Sub-phase đánh số `P<n>.<m>` chạy từ 1.
- Status legend giống phase cha: `[ ]` / `[~]` / `[x]`.
- Phase cha chỉ chuyển `[x]` khi MỌI sub-phase `[x]`.
- Cấm nest tiếp (không có `P3.1.1`).
- Khi rollback một sub-phase, đặt `[~]` và thêm dòng `Re-opened: <ISO date> - lý do: ...` vào `Sub-phase notes`.
