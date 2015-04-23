package org.ldp4j.generic.util;

import com.google.common.base.Preconditions;
import org.ldp4j.generic.http.InvalidPreferenceHeaderException;
import org.ldp4j.generic.http.RepresentationPreference;
import org.ldp4j.generic.ldp.model.Preference;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by nandana on 2/25/15.
 */
public class PreferHeaderUtils {

    private static final String PARAMETER="^\\s*(\\w*)\\s*=\\s*\"([^\"]+)\"\\s*$";

    public static RepresentationPreference parse(String value) {

        Preconditions.checkNotNull(value, "Prefer header should not be null");
        String[] preferenceParts = value.split(";");
        if(preferenceParts.length==1) {
            throw new InvalidPreferenceHeaderException("Could not parse preference ("+value+"): invalid return representation preference configuration");
        }
        validatePrefix(value, preferenceParts[0]);
        return populatePreferences(Arrays.copyOfRange(preferenceParts, 1, preferenceParts.length, String[].class));
    }

    private static void validatePrefix(String value, String prefix) {
        String[] items = prefix.split("=");
        if(items.length!=2) {
            throw new InvalidPreferenceHeaderException("Could not parse preferences ("+value+"): could not find return representation");
        }
        if(!items[0].trim().equals("return")) {
            throw new InvalidPreferenceHeaderException("Could not parse preferences ("+value+"): unexpected token '"+items[0].trim()+"'");
        }
        if(!items[1].trim().equals("representation")) {
            throw new InvalidPreferenceHeaderException("Could not parse preferences ("+value+"): unexpected return type '"+items[1].trim()+"'");
        }
    }

    private static RepresentationPreference populatePreferences(String[] parameters) {

        RepresentationPreference representationPreference = new RepresentationPreference();
        Pattern pattern = Pattern.compile(PARAMETER);
        Set<String> configured=new TreeSet<String>();
        for(String refinement:parameters) {
            Matcher matcher = pattern.matcher(refinement);
            if(!matcher.matches()) {
                throw new InvalidPreferenceHeaderException("Invalid preference refinement '"+refinement+"'");
            }
            String hint=matcher.group(1);
            boolean include=true;
            if(hint.equals("omit")) {
                include=false;
            } else if(hint.equals("include")) {
                include=true;
            } else {
                throw new IllegalArgumentException("Invalid preference hint '"+hint+"'");
            }
            if(configured.contains(hint)) {
                throw new InvalidPreferenceHeaderException("Hint '"+hint+"' has already been configured");
            }
            configured.add(hint);
            for(String rawPreference : matcher.group(2).split("\\s")) {
                Preference preference = Preference.fromString(rawPreference.trim());
                if(preference==null) {
                    throw new InvalidPreferenceHeaderException("Unknown preference '"+rawPreference+"'");
                }
                if(include) {
                    representationPreference.include(preference);
                } else {
                    representationPreference.omit(preference);
                }
            }
        }

        return representationPreference;
    }

    public static String asPreferenceAppliedHeader(RepresentationPreference contentPreferences) {
        checkNotNull("Content preferences cannot be null");
        StringBuilder header=new StringBuilder();
        header.append("return=representation");
        return header.toString();
    }
}
