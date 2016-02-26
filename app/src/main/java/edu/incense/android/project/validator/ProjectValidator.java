package edu.incense.android.project.validator;

import edu.incense.android.project.ProjectSignature;

public interface ProjectValidator {
    public boolean isValid(ProjectSignature projectSignature);
}
