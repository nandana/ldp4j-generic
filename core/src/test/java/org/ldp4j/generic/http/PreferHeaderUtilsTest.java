package org.ldp4j.generic.http;

import org.junit.Test;
import org.ldp4j.generic.ldp.model.Preference;
import org.ldp4j.generic.util.PreferHeaderUtils;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * Created by nandana on 2/25/15.
 */
public class PreferHeaderUtilsTest {

    private static final String MIXED_PREFERENCES_INCLUDE_OMIT = "return=representation; include=\"http://www.w3.org/ns/ldp#PreferMinimalContainer\"; omit=\"http://www.w3.org/ns/ldp#PreferMembership http://www.w3.org/ns/ldp#PreferContainment\"";
    private static final String MIXED_PREFERENCES_OMIT_INCLUDE = "return=representation; omit=\"http://www.w3.org/ns/ldp#PreferMembership http://www.w3.org/ns/ldp#PreferContainment\"; include=\"http://www.w3.org/ns/ldp#PreferMinimalContainer\"";
    private static final String MULTIPLE_OMIT_PREFERENCES = "return=representation; omit=\"http://www.w3.org/ns/ldp#PreferMembership http://www.w3.org/ns/ldp#PreferContainment\"";
    private static final String SINGLE_INCLUDE_PREFERENCE = "return=representation; include=\"http://www.w3.org/ns/ldp#PreferMinimalContainer\"";
    private static final String MULTIPLE_INCLUDE_PREFERENCE = "return=representation; include=\"http://www.w3.org/ns/ldp#PreferContainment http://www.w3.org/ns/ldp#PreferMembership\"";

    private static final RepresentationPreference COMPOSITE_PREFERENCES =
            RepresentationPreference.
                    builder().
                    withOmit(Preference.CONTAINMENT_TRIPLES).
                    withOmit(Preference.MEMBERSHIP_TRIPLES).
                    withInclude(Preference.MINIMAL_CONTAINER).
                    build();

    private static final RepresentationPreference SINGLE_INCLUDE =
            RepresentationPreference.
                    builder().
                    withInclude(Preference.MINIMAL_CONTAINER).
                    build();

    private static final RepresentationPreference MULTIPLE_INCLUDES =
            RepresentationPreference.
                    builder().
                    withInclude(Preference.CONTAINMENT_TRIPLES).
                    withInclude(Preference.MEMBERSHIP_TRIPLES).
                    build();

    private static final RepresentationPreference MULTIPLE_OMITS =
            RepresentationPreference.
                    builder().
                    withOmit(Preference.CONTAINMENT_TRIPLES).
                    withOmit(Preference.MEMBERSHIP_TRIPLES).
                    build();

    @Test
    public void testFromPreferenceHeader$validValues() throws Exception {

        assertThat(PreferHeaderUtils.parse(MIXED_PREFERENCES_INCLUDE_OMIT),equalTo(COMPOSITE_PREFERENCES));
        assertThat(PreferHeaderUtils.parse(MIXED_PREFERENCES_OMIT_INCLUDE),equalTo(COMPOSITE_PREFERENCES));
        assertThat(PreferHeaderUtils.parse(SINGLE_INCLUDE_PREFERENCE),equalTo(SINGLE_INCLUDE));
        assertThat(PreferHeaderUtils.parse(MULTIPLE_INCLUDE_PREFERENCE),equalTo(MULTIPLE_INCLUDES));
        assertThat(PreferHeaderUtils.parse(MULTIPLE_OMIT_PREFERENCES),equalTo(MULTIPLE_OMITS));
    }

    @Test(expected=InvalidPreferenceHeaderException.class)
    public void testFromPreferenceHeader$invalidValues$no_parameter() throws Exception {
        PreferHeaderUtils.parse("return=representation");
    }

    @Test(expected=InvalidPreferenceHeaderException.class)
    public void testFromPreferenceHeader$invalidValues$wrong_parameter() throws Exception {
        PreferHeaderUtils.parse("return=representation; invalidParameter");
    }

    @Test(expected=InvalidPreferenceHeaderException.class)
    public void testFromPreferenceHeader$invalidValues$more_than_two_parameters() throws Exception {
        PreferHeaderUtils.parse("return=representation; invalidParameter; another; third");
    }

    @Test(expected=InvalidPreferenceHeaderException.class)
    public void testFromPreferenceHeader$invalidValues$repeated_include_hints() throws Exception {
        PreferHeaderUtils.parse("return=representation; include=\"http://www.w3.org/ns/ldp#PreferMinimalContainer\"; include=\"http://www.w3.org/ns/ldp#PreferMinimalContainer\"");
    }

    @Test(expected=InvalidPreferenceHeaderException.class)
    public void testFromPreferenceHeader$invalidValues$repeated_omit_hints() throws Exception {
        PreferHeaderUtils.parse("return=representation; omit=\"http://www.w3.org/ns/ldp#PreferMinimalContainer\"; omit=\"http://www.w3.org/ns/ldp#PreferMinimalContainer\"");
    }

    @Test(expected=InvalidPreferenceHeaderException.class)
    public void testFromPreferenceHeader$invalidValues$repeated_unknown_hint_preference$1() throws Exception {
        PreferHeaderUtils.parse("return=representation; omit=\"http://www.w3.org/ns/ldp#Unknown\"");
    }

    @Test(expected=InvalidPreferenceHeaderException.class)
    public void testFromPreferenceHeader$invalidValues$repeated_unknown_hint_preference$2() throws Exception {
        PreferHeaderUtils.parse("return=representation; omit=\"http://www.w3.org/ns/ldp#PreferMinimalContainer http://www.w3.org/ns/ldp#Unknown\"");
    }
}
