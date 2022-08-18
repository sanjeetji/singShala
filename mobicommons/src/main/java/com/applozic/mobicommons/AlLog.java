package com.applozic.mobicommons;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Logger for all SDK logs.
 *
 * <p>This class wraps around {@link Log} to provide a log object along with printing the logs.
 * Objects of this class will be passed to {@link AlLoggerListener#onLogged(AlLog)}.</p>
 */
public class AlLog {
    public final String logContext;
    public final String logSubContext;
    public final String logMessage;
    public final Throwable logThrowable;
    public final AlLogType alLogType;

    /**
     * @param logContext The main tag. This might be the enclosing class for the log method call.
     * @param logSubContext The sub tag. This has been added for help with log organisation.
     * @param logMessage The log message.
     * @param logThrowable An exception to log, if present.
     * @param alLogType The type of log message. See {@link AlLogType}.
     */
    public AlLog(@NonNull String logContext, @Nullable String logSubContext, @NonNull String logMessage, @Nullable Throwable logThrowable, @NonNull AlLogType alLogType) {
        this.logContext = logContext;
        this.logSubContext = logSubContext;
        this.logMessage = logMessage;
        this.logThrowable = logThrowable;
        this.alLogType = alLogType;
    }

    private static void printLog(@NonNull AlLog alLog) {
        String logTag = alLog.logContext.trim();
        if (!TextUtils.isEmpty(alLog.logSubContext)){
            logTag = logTag + alLog.logSubContext.trim();
        }

        switch (alLog.alLogType) {
            case WARN:
                Log.w(logTag, alLog.logMessage, alLog.logThrowable);
                break;
            case ERROR:
                Log.e(logTag, alLog.logMessage, alLog.logThrowable);
                break;
            case DEBUG:
                Log.d(logTag, alLog.logMessage, alLog.logThrowable);
                break;
            default:
                Log.i(logTag, alLog.logMessage, alLog.logThrowable);
        }
    }

    public static AlLog i(@NonNull String logContext, @Nullable String logSubContext, @NonNull String logMessage, Throwable logThrowable) {
        AlLog alLog = new AlLog(logContext, logSubContext, logMessage, logThrowable, AlLogType.INFO);
        printLog(alLog);
        return alLog;
    }

    public static AlLog i(@NonNull String logContext, @Nullable String logSubContext, @NonNull String logMessage) {
        return i(logContext, logSubContext, logMessage, null);
    }

    public static AlLog d(@NonNull String logContext, @Nullable String logSubContext, @NonNull String logMessage, Throwable logThrowable) {
        AlLog alLog = new AlLog(logContext, logSubContext, logMessage, logThrowable, AlLogType.DEBUG);
        printLog(alLog);
        return alLog;
    }

    public static AlLog d(@NonNull String logContext, @Nullable String logSubContext, @NonNull String logMessage) {
        return d(logContext, logSubContext, logMessage, null);
    }

    public static AlLog w(@NonNull String logContext, @Nullable String logSubContext, @NonNull String logMessage, Throwable logThrowable) {
        AlLog alLog = new AlLog(logContext, logSubContext, logMessage, logThrowable, AlLogType.WARN);
        printLog(alLog);
        return alLog;
    }

    public static AlLog w(@NonNull String logContext, @Nullable String logSubContext, @NonNull String logMessage) {
        return w(logContext, logSubContext, logMessage, null);
    }

    public static AlLog e(@NonNull String logContext, @Nullable String logSubContext, @NonNull String logMessage, Throwable logThrowable) {
        AlLog alLog = new AlLog(logContext, logSubContext, logMessage, logThrowable, AlLogType.ERROR);
        printLog(alLog);
        return alLog;
    }

    public static AlLog e(@NonNull String logContext, @Nullable String logSubContext, @NonNull String logMessage) {
        return e(logContext, logSubContext, logMessage, null);
    }

    @NonNull
    @Override
    public String toString() {
        final String SEPARATOR = ":";

        StringBuilder stringBuilder = new StringBuilder(alLogType.stringRepresentation).append(SEPARATOR);

        if (!TextUtils.isEmpty(logContext)) {
            stringBuilder.append(logContext).append(SEPARATOR);
        }

        if (!TextUtils.isEmpty(logSubContext)) {
            stringBuilder.append(logSubContext).append(SEPARATOR);
        }

        if (!TextUtils.isEmpty(logMessage)) {
            stringBuilder.append(logMessage);
        }

        if (logThrowable != null) {
            stringBuilder.append("\n").append(Log.getStackTraceString(logThrowable));
        }

        return stringBuilder.toString();
    }

    public enum AlLogType {
        INFO("Info"), DEBUG("Debug"), ERROR("Error"), WARN("Warning");

        public final String stringRepresentation;

        AlLogType(String stringRepresentation) {
            this.stringRepresentation = stringRepresentation;
        }
    }

    public interface AlLoggerListener {
        void onLogged(@NonNull AlLog log);
    }
}
