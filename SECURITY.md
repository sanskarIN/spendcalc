# Security Policy

## Supported versions

Security fixes are applied to the latest maintained release on the default branch. Older releases may not receive fixes unless a release note explicitly says otherwise.

## Reporting a vulnerability

Please do not publish exploitable security details in a public issue before a fix is available.

Report suspected vulnerabilities to:

- `supportramsandesh@gmail.com`
- `sanskarin@outlook.in`

Include the affected version/commit, reproduction conditions, impact, and the minimum information needed to validate the issue. Do not include real user data, credentials, signing keys, or other secrets.

## Security model

SpendCalc is designed as an offline-first Android application:

- core calculations do not require network access;
- calculation history and templates are stored in the app's local Room database;
- preferences are stored with Android DataStore;
- export files are written only to the app cache and shared through a non-exported `FileProvider` with temporary read permission;
- no authentication token, payment credential, or remote API key is required by the app;
- user-provided receipt labels are treated as data, not executable content;
- CSV export neutralizes common spreadsheet-formula prefixes in text cells.

## Maintainer checklist

For security-sensitive changes:

1. validate untrusted input at the boundary;
2. avoid logging calculation contents unless explicitly needed for debugging and redacted;
3. minimize Android permissions;
4. keep secrets out of source control and workflow logs;
5. prefer maintained platform/library APIs over custom cryptography;
6. add a regression test for each confirmed security defect;
7. run dependency review, CodeQL, and secret-scanning checks where available.

## Disclosure

After a fix is available, maintainers may publish a short advisory describing affected versions, impact, mitigation, and upgrade guidance without exposing unnecessary user information.
