package org.ldp4j.generic.http;

import org.apache.commons.lang3.StringUtils;
import org.ldp4j.generic.core.LDPContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;

public class HttpUtils {

    public static String getHeaderString(List<String> values) {
        if (values == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < values.size(); i++) {
                String value = values.get(i);
                if (StringUtils.isEmpty(value)) {
                    continue;
                }
                sb.append(value);
                if (i + 1 < values.size()) {
                    sb.append(",");
                }
            }
            return sb.toString();
        }
    }

    public static String getHeaderValue(String header, LDPContext context){

        HttpServletRequest request = context.getServletRequest();

        return request.getHeader(header);

    }

    public static Enumeration<String> getHeaderValues(String header, LDPContext context) {

        HttpServletRequest request = context.getServletRequest();

        return request.getHeaders(header);

    }

}
