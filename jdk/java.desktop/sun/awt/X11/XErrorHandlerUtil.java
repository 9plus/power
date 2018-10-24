/*
 * Copyright (c) 2013, 2014, Oracle and/or its affiliates. All rights reserved.
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
package sun.awt.X11;

import java.security.AccessController;
import sun.awt.SunToolkit;
import sun.security.action.GetBooleanAction;
import sun.util.logging.PlatformLogger;

/**
 * This class contains code of the global toolkit error handler, exposes static
 * methods which allow to set and unset synthetic error handlers.
 */
public final class XErrorHandlerUtil {
    private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11.XErrorHandlerUtil");

    /**
     * The connection to X11 window server.
     */
    private static long display;

    /**
     * Error handler at the moment of {@code XErrorHandlerUtil} initialization.
     */
    private static long saved_error_handler;

    /**
     * XErrorEvent being handled.
     */
    static volatile XErrorEvent saved_error;

    /**
     * Current error handler or null if no error handler is set.
     */
    private static XErrorHandler current_error_handler;

    /**
     * Value of sun.awt.noisyerrorhandler system property.
     */
    private static boolean noisyAwtHandler = AccessController.doPrivileged(
        new GetBooleanAction("sun.awt.noisyerrorhandler"));

    /**
     * The flag indicating that {@code init} was called already.
     */
    private static boolean initPassed;

    /**
     * Guarantees that no instance of this class can be created.
     */
    private XErrorHandlerUtil() {}

    /**
     * Sets the toolkit global error handler, stores the connection to X11 server,
     * which will be used during an error handling process. This method is called
     * once from {@code awt_init_Display} function defined in {@code awt_GraphicsEnv.c}
     * file immediately after the connection to X11 window server is opened.
     * @param display the connection to X11 server which should be stored
     */
    private static void init(long display) {
        SunToolkit.awtLock();
        try {
            if (!initPassed) {
                XErrorHandlerUtil.display = display;
                saved_error_handler = XlibWrapper.SetToolkitErrorHandler();
                initPassed = true;
            }
        } finally {
            SunToolkit.awtUnlock();
        }
    }

    /**
     * Sets a synthetic error handler. Must be called with the acquired AWT lock.
     * @param handler the synthetic error handler to set
     */
    public static void WITH_XERROR_HANDLER(XErrorHandler handler) {
        XSync();
        saved_error = null;
        current_error_handler = handler;
    }

    /**
     * Unsets a current synthetic error handler. Must be called with the acquired AWT lock.
     */
    public static void RESTORE_XERROR_HANDLER() {
        // Wait until all requests are processed by the X server
        // and only then uninstall the error handler.
        XSync();
        current_error_handler = null;
    }

    /**
     * Should be called under LOCK.
     */
    public static int SAVED_XERROR_HANDLER(long display, XErrorEvent error) {
        if (saved_error_handler != 0) {
            // Default XErrorHandler may just terminate the process. Don't call it.
            // return XlibWrapper.CallErrorHandler(saved_error_handler, display, error.pData);
        }
        if (log.isLoggable(PlatformLogger.Level.FINE)) {
            log.fine("Unhandled XErrorEvent: " +
                "id=" + error.get_resourceid() + ", " +
                "serial=" + error.get_serial() + ", " +
                "ec=" + error.get_error_code() + ", " +
                "rc=" + error.get_request_code() + ", " +
                "mc=" + error.get_minor_code());
        }
        return 0;
    }

    /**
     * Called from the native code when an error occurs.
     */
    private static int globalErrorHandler(long display, long event_ptr) {
        if (noisyAwtHandler) {
            XlibWrapper.PrintXErrorEvent(display, event_ptr);
        }
        XErrorEvent event = new XErrorEvent(event_ptr);
        saved_error = event;
        try {
            if (current_error_handler != null) {
                return current_error_handler.handleError(display, event);
            } else {
                return SAVED_XERROR_HANDLER(display, event);
            }
        } catch (Throwable z) {
            log.fine("Error in GlobalErrorHandler", z);
        }
        return 0;
    }

    private static void XSync() {
        SunToolkit.awtLock();
        try {
            XlibWrapper.XSync(display, 0);
        } finally {
            SunToolkit.awtUnlock();
        }
    }
}
