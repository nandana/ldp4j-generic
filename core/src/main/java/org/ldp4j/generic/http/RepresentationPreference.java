package org.ldp4j.generic.http;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import org.ldp4j.generic.ldp.model.Preference;

import java.util.Set;

/**
 * Created by nandana on 2/25/15.
 */
public class RepresentationPreference {

    private final Set<Preference> include;
    private final Set<Preference> omit;

    public RepresentationPreference() {
        this.include= Sets.newTreeSet();
        this.omit=Sets.newTreeSet();
    }

    public void include(Preference preference) {
        if(preference!=null) {
            this.include.add(preference);
            this.omit.remove(preference);
        }
    }

    public void omit(Preference preference) {
        if(preference!=null) {
            this.include.remove(preference);
            this.omit.add(preference);
        }
    }

    public boolean isMinimalInclusionRequired() {
        return this.include.contains(Preference.MINIMAL_CONTAINER);
    }

    public boolean isOmissiontRequired(Preference preference) {
        return this.omit.contains(preference);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.include, this.omit);
    }

    @Override
    public boolean equals(Object obj) {
        boolean result=false;
        if(obj!=null && obj.getClass()==this.getClass()) {
            RepresentationPreference that=(RepresentationPreference)obj;
            result=
                    Objects.equal(this.include,that.include) &&
                            Objects.equal(this.omit,that.omit);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ContentPreferences [");
        builder.append("include=").append(this.include).append(", ");
        builder.append("omit=").append(this.omit);
        builder.append("]");
        return builder.toString();
    }

    public static RepresentationPreference defaultPreferences() {
        RepresentationPreference tmp = new RepresentationPreference();
        tmp.include(Preference.CONTAINMENT_TRIPLES);
        tmp.include(Preference.MEMBERSHIP_TRIPLES);
        return tmp;
    }

    public boolean mayInclude(Preference preference) {
        Preference tmp = normalize(preference);
        return this.include.contains(tmp) || (!isOmissiontRequired(tmp) && !isMinimalInclusionRequired());
    }

    private static Preference normalize(Preference preference) {
        Preference result=preference;
        if(Preference.EMPTY_CONTAINER.equals(result)) {
            result=Preference.MINIMAL_CONTAINER;
        }
        return result;
    }

    public static RepresentationPreferenceBuilder builder() {
        return new RepresentationPreferenceBuilder();
    }

}
