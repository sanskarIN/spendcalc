# Room schema snapshots

Room exports versioned database schemas to this directory during compilation through the configured `room.schemaLocation` KSP argument.

Keep released schema JSON snapshots under version control. When the database version changes, add an explicit migration and migration test rather than deleting prior schemas or enabling destructive migration fallback.
