package org.ldp4j.generic.http;

import org.ldp4j.generic.ldp.model.Preference;

public class RepresentationPreferenceBuilder {

    private RepresentationPreference contentPreferences;

    public RepresentationPreferenceBuilder() {
        this.contentPreferences=new RepresentationPreference();
    }

    public RepresentationPreferenceBuilder withInclude(Preference preference) {
        this.contentPreferences.include(normalize(preference));
        return this;
    }

    public RepresentationPreferenceBuilder withOmit(Preference preference) {
        this.contentPreferences.omit(normalize(preference));
        return this;
    }

    public RepresentationPreference build() {
        return this.contentPreferences;
    }

    private static Preference normalize(Preference preference) {
        Preference result=preference;
        if(Preference.EMPTY_CONTAINER.equals(result)) {
            result=Preference.MINIMAL_CONTAINER;
        }
        return result;
    }
}
