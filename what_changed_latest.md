# SpendCalc — Latest Work Continuity

This file is retained only for compatibility with earlier handoffs.

The canonical, current engineering record is [`what_changed.md`](what_changed.md).

The active application release target is `2.15.4` with Android `versionCode` `21504`. Room database and explicit backup schema versions remain `1` unless a real compatibility change requires otherwise. Exact branch-head, workflow, manual-verification, signing, and release truth must be read from the canonical root handoff and [`docs/verification.md`](docs/verification.md).

The active completion/verification path is PR `#12` on `complete/v1-finalization` while that PR remains open. Its title/body and release documentation are retargeted to 2.15.4. GitHub Actions evidence must always be checked against the exact current head because concurrency cancellation intentionally supersedes runs for older commits.

Do not use older PR `#1`, `verify/release-candidate`, historical `1.0.0`, or superseded `2.0.12` candidate details as the current continuation point. Browser-extension work and unrelated major dependency upgrades remain post-release/isolated work rather than part of the Android 2.15.4 blocking gate.
