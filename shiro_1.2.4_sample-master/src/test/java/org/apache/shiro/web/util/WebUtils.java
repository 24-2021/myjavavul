package org.apache.shiro.web.util;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebUtils {
    public static final String INCLUDE_CONTEXT_PATH_ATTRIBUTE = "javax.servlet.include.context_path";
    private static final Logger log = LoggerFactory.getLogger(WebUtils.class);
    protected static String determineEncoding(HttpServletRequest request) {
        String enc = request.getCharacterEncoding();
        if (enc == null) {
            enc = "ISO-8859-1";
        }

        return enc;
    }
    public static String decodeRequestString(HttpServletRequest request, String source) {
        String enc = determineEncoding(request);

        try {
            return URLDecoder.decode(source, enc);
        } catch (UnsupportedEncodingException var4) {
            if (log.isWarnEnabled()) {
                log.warn("Could not decode request string [" + source + "] with encoding '" + enc + "': falling back to platform default encoding; exception message: " + var4.getMessage());
            }

            return URLDecoder.decode(source);
        }
    }
    private static String normalize(String path, boolean replaceBackSlash) {
        if (path == null) {
            return null;
        } else {
            String normalized = path;
            if (replaceBackSlash && path.indexOf(92) >= 0) {
                normalized = path.replace('\\', '/');
            }

            if (normalized.equals("/.")) {
                return "/";
            } else {
                if (!normalized.startsWith("/")) {
                    normalized = "/" + normalized;
                }

                while(true) {
                    int index = normalized.indexOf("//");
                    if (index < 0) {
                        while(true) {
                            index = normalized.indexOf("/./");
                            if (index < 0) {
                                while(true) {
                                    index = normalized.indexOf("/../");
                                    if (index < 0) {
                                        return normalized;
                                    }

                                    if (index == 0) {
                                        return null;
                                    }

                                    int index2 = normalized.lastIndexOf(47, index - 1);
                                    normalized = normalized.substring(0, index2) + normalized.substring(index + 3);
                                }
                            }

                            normalized = normalized.substring(0, index) + normalized.substring(index + 2);
                        }
                    }

                    normalized = normalized.substring(0, index) + normalized.substring(index + 1);
                }
            }
        }
    }
    public static String normalize(String path) {
        return normalize(path, true);
    }

    /**
     * 这是补丁最主要的部分
     * @param request
     * @return
     */
    public static String getContextPath(HttpServletRequest request) {
        String contextPath = (String)request.getAttribute("INCLUDE_CONTEXT_PATH_ATTRIBUTE");
        if (contextPath == null) {
            contextPath = request.getContextPath();
        }

        contextPath = normalize(decodeRequestString(request, contextPath));
        if ("/".equals(contextPath)) {
            contextPath = "";
        }


        return contextPath;
    }
}
