package com.projet_JVM2_DATA.model;

import java.time.Instant;

public class Crash {
    public long gameId;
    public String version;
    public String support;
    public String errorCode;     // nullable
    public String cause;         // ex "CRASH"
    public String sessionId;     // unique
    public Instant crashDate;
}
