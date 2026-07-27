package com.immiauto.enums;

/**
 * How a {@code CaseFormDraft} artifact was produced.
 */
public enum DraftOrigin {
    /** Auto-filled from an approved mapping into a genuine AcroForm (Milestone 3). */
    GENERATED("Auto-generated"),
    /** Official form filled manually by the consultant (e.g. XFA/certified) and uploaded. */
    UPLOADED("Manually uploaded"),
    /** A mapped-values transcription sheet for forms that cannot be auto-filled (backlog). */
    DATA_SHEET("Data sheet");

    private final String displayName;

    DraftOrigin(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
