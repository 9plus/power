/*
 * Copyright (c) 2002, 2012, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package sun.net.dns;

import java.util.List;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/*
 * An implementation of ResolverConfiguration for Solaris
 * and Linux.
 */

public class ResolverConfigurationImpl
    extends ResolverConfiguration
{
    // Lock helds whilst loading configuration or checking
    private static Object lock = new Object();

    // Time of last refresh.
    private static long lastRefresh = -1;

    // Cache timeout (300 seconds) - should be converted into property
    // or configured as preference in the future.
    private static final int TIMEOUT = 300000;

    // Resolver options
    private final Options opts;

    // Parse /etc/resolv.conf to get the values for a particular
    // keyword.
    //
    private LinkedList<String> resolvconf(String keyword,
                                          int maxperkeyword,
                                          int maxkeywords)
    {
        LinkedList<String> ll = new LinkedList<>();

        try {
            BufferedReader in =
                new BufferedReader(new FileReader("/etc/resolv.conf"));
            String line;
            while ((line = in.readLine()) != null) {
                int maxvalues = maxperkeyword;
                if (line.length() == 0)
                   continue;
                if (line.charAt(0) == '#' || line.charAt(0) == ';')
                    continue;
                if (!line.startsWith(keyword))
                    continue;
                String value = line.substring(keyword.length());
                if (value.length() == 0)
                    continue;
                if (value.charAt(0) != ' ' && value.charAt(0) != '\t')
                    continue;
                StringTokenizer st = new StringTokenizer(value, " \t");
                while (st.hasMoreTokens()) {
                    String val = st.nextToken();
                    if (val.charAt(0) == '#' || val.charAt(0) == ';') {
                        break;
                    }
                    if ("nameserver".equals(keyword)) {
                        if (val.indexOf(':') >= 0 &&
                            val.indexOf('.') < 0 && // skip for IPv4 literals with port
                            val.indexOf('[') < 0 &&
                            val.indexOf(']') < 0 ) {
                            // IPv6 literal, in non-BSD-style.
                            val = "[" + val + "]";
                        }
                    }
                    ll.add(val);
                    if (--maxvalues == 0) {
                        break;
                    }
                }
                if (--maxkeywords == 0) {
                    break;
                }
            }
            in.close();
        } catch (IOException ioe) {
            // problem reading value
        }

        return ll;
    }

    private LinkedList<String> searchlist;
    private LinkedList<String> nameservers;


    // Load DNS configuration from OS

    private void loadConfig() {
        assert Thread.holdsLock(lock);

        // check if cached settings have expired.
        if (lastRefresh >= 0) {
            long currTime = System.currentTimeMillis();
            if ((currTime - lastRefresh) < TIMEOUT) {
                return;
            }
        }

        // get the name servers from /etc/resolv.conf
        nameservers =
            java.security.AccessController.doPrivileged(
                new java.security.PrivilegedAction<>() {
                    public LinkedList<String> run() {
                        // typically MAXNS is 3 but we've picked 5 here
                        // to allow for additional servers if required.
                        return resolvconf("nameserver", 1, 5);
                    } /* run */
                });

        // get the search list (or domain)
        searchlist = getSearchList();

        // update the timestamp on the configuration
        lastRefresh = System.currentTimeMillis();
    }


    // obtain search list or local domain

    private LinkedList<String> getSearchList() {

        LinkedList<String> sl;

        // first try the search keyword in /etc/resolv.conf

        sl = java.security.AccessController.doPrivileged(
                 new java.security.PrivilegedAction<>() {
                    public LinkedList<String> run() {
                        LinkedList<String> ll;

                        // first try search keyword (max 6 domains)
                        ll = resolvconf("search", 6, 1);
                        if (ll.size() > 0) {
                            return ll;
                        }

                        return null;

                    } /* run */

                });
        if (sl != null) {
            return sl;
        }

        // No search keyword so use local domain


        // LOCALDOMAIN has absolute priority on Solaris

        String localDomain = localDomain0();
        if (localDomain != null && localDomain.length() > 0) {
            sl = new LinkedList<>();
            sl.add(localDomain);
            return sl;
        }

        // try domain keyword in /etc/resolv.conf

        sl = java.security.AccessController.doPrivileged(
                 new java.security.PrivilegedAction<>() {
                    public LinkedList<String> run() {
                        LinkedList<String> ll;

                        ll = resolvconf("domain", 1, 1);
                        if (ll.size() > 0) {
                            return ll;
                        }
                        return null;

                    } /* run */
                });
        if (sl != null) {
            return sl;
        }

        // no local domain so try fallback (RPC) domain or
        // hostName

        sl = new LinkedList<>();
        String domain = fallbackDomain0();
        if (domain != null && domain.length() > 0) {
            sl.add(domain);
        }

        return sl;
    }


    // ----

    ResolverConfigurationImpl() {
        opts = new OptionsImpl();
    }

    @SuppressWarnings("unchecked")
    public List<String> searchlist() {
        synchronized (lock) {
            loadConfig();

            // List is mutable so return a shallow copy
            return (List<String>)searchlist.clone();
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> nameservers() {
        synchronized (lock) {
            loadConfig();

            // List is mutable so return a shallow copy

          return (List<String>)nameservers.clone();

        }
    }

    public Options options() {
        return opts;
    }


    // --- Native methods --

    static native String localDomain0();

    static native String fallbackDomain0();

    static {
        java.security.AccessController.doPrivileged(
            new java.security.PrivilegedAction<>() {
                public Void run() {
                    System.loadLibrary("net");
                    return null;
                }
            });
    }

}

/**
 * Implementation of {@link ResolverConfiguration.Options}
 */
class OptionsImpl extends ResolverConfiguration.Options {
}
