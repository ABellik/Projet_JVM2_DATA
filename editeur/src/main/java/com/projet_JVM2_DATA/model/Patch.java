package com.projet_JVM2_DATA.model;

import java.time.Instant;

public class Patch {
    public long gameId;
    public String baseVersion;
    public String newVersion;
    public String support;
    public long crashCount;
    public Instant releaseDate;
    public String reason;
}
