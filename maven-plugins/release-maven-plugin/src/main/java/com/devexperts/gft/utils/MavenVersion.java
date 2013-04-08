package com.devexperts.gft.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MavenVersion {

    private static final Pattern TRUNK_VERSION_PATTERN = Pattern.compile("^(?:R[\\-])?(\\d+)(?:\\-SNAPSHOT)?$");
    private static final Pattern BRANCH_VERSION_PATTERN = Pattern.compile("^(?:R[\\-])?(\\d+)\\-(\\d+)(?:\\-SNAPSHOT)?$");
    private static final Pattern COMP_VERSION_PATTERN = Pattern.compile("^(?:R[\\-])?((?:\\d+\\-)+)(\\d+)\\-(\\d+)(?:\\-SNAPSHOT)?$");

    private static final String SNAPSHOT = "-SNAPSHOT";

    public static MavenVersion fromString(String version) {
        if (version == null || "".equals(version)) {
            throw new IllegalArgumentException("version cannot be empty, was " + version + ";");
        }
        String trimmedVersion = version.trim();
        Matcher matcher = COMP_VERSION_PATTERN.matcher(trimmedVersion);

        if (matcher.matches()) {
            return createComplMavenVersion(trimmedVersion, matcher);
        } else {
            matcher = BRANCH_VERSION_PATTERN.matcher(trimmedVersion);
            if (matcher.matches()) {
                return createBranchMavenVersion(trimmedVersion, matcher);
            } else {
                matcher = TRUNK_VERSION_PATTERN.matcher(trimmedVersion);
                if (matcher.matches()) {
                    return createTrunkMavenVersion(trimmedVersion, matcher);
                } else {
                    throw new IllegalArgumentException("Invalid version format: " + version);
                }
            }
        }
    }

    private static MavenVersion createTrunkMavenVersion(String trimmedVersion, Matcher matcher) {
        try {
            int version = Integer.parseInt(matcher.group(1));
            return new TrunkMavenVersion(version);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid version format: " + trimmedVersion);
        }
    }

    private static MavenVersion createBranchMavenVersion(String trimmedVersion, Matcher matcher) {
        try {
            int branchVersion = Integer.parseInt(matcher.group(1));
            int version = Integer.parseInt(matcher.group(2));
            return new BranchMavenVersion(branchVersion, version);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid version format: " + trimmedVersion);
        }
    }

    private static MavenVersion createComplMavenVersion(String trimmedVersion, Matcher matcher) {
        try {
            String majorVersion = matcher.group(1);
            int branchVersion = Integer.parseInt(matcher.group(2));
            int version = Integer.parseInt(matcher.group(3));
            return new CompMavenVersion(majorVersion, branchVersion, version);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid version format: " + trimmedVersion);
        }
    }

    public abstract String getCurrentReleaseVersion();

    public abstract String getCurrentSnapshotVersion();

    public abstract String getNextReleaseVersion();

    public abstract String getNextSnapshotVersion();

    public abstract String getPreviousReleaseVersion();

    public abstract String getNextBranchSnapshotVersion();

    private static class CompMavenVersion extends MavenVersion {
        private final String majorVersion;
        private final int branchVersion;
        private final int version;

        public CompMavenVersion(String majorVersion, int branchVersion, int version) {
            this.majorVersion = majorVersion;
            this.branchVersion = branchVersion;
            this.version = version;
        }

        public String getCurrentReleaseVersion() {
            return majorVersion + String.valueOf(branchVersion) + "-" + String.valueOf(version);
        }

        public String getCurrentSnapshotVersion() {
            return majorVersion + String.valueOf(branchVersion) + "-" + String.valueOf(version) + SNAPSHOT;
        }

        public String getNextReleaseVersion() {
            return majorVersion + String.valueOf(branchVersion) + "-" + String.valueOf(version + 1);
        }

        public String getNextSnapshotVersion() {
            return majorVersion + String.valueOf(branchVersion) + "-" + String.valueOf(version + 1) + SNAPSHOT;
        }

        public String toString() {
            return "CompMavenVersion{" +
                    ", majorVersion=" + majorVersion +
                    ", branchVersion=" + branchVersion +
                    ", version=" + version +
                    '}';
        }

        public String getPreviousReleaseVersion() {
            if (version - 1 == 0) {
                return String.valueOf(majorVersion) + String.valueOf(branchVersion);
            } else {
                return String.valueOf(majorVersion) + String.valueOf(branchVersion) + "-" + String.valueOf(version - 1);
            }
        }

        public String getNextBranchSnapshotVersion() {
            return getCurrentReleaseVersion() + "-1" + SNAPSHOT;
        }

    }

    private static class BranchMavenVersion extends MavenVersion {
        private final int version;
        private final int branchVersion;

        public BranchMavenVersion(int branchVersion, int version) {
            this.version = version;
            this.branchVersion = branchVersion;
        }

        public String getCurrentReleaseVersion() {
            return String.valueOf(branchVersion) + "-" + String.valueOf(version);
        }

        public String getCurrentSnapshotVersion() {
            return String.valueOf(branchVersion) + "-" + String.valueOf(version) + SNAPSHOT;
        }

        public String getNextReleaseVersion() {
            return String.valueOf(branchVersion) + "-" + String.valueOf(version + 1);
        }

        public String getNextSnapshotVersion() {
            return String.valueOf(branchVersion) + "-" + String.valueOf(version + 1) + SNAPSHOT;
        }

        public String getPreviousReleaseVersion() {
            if (version - 1 == 0) {
                return String.valueOf(branchVersion);
            } else {
                return String.valueOf(branchVersion) + "-" + String.valueOf(version - 1);
            }
        }

        public String getNextBranchSnapshotVersion() {
            return getCurrentReleaseVersion() + "-1" + SNAPSHOT;
        }

    }

    private static class TrunkMavenVersion extends MavenVersion {
        private final int version;

        public TrunkMavenVersion(int version) {
            this.version = version;
        }

        public String getCurrentReleaseVersion() {
            return String.valueOf(version);
        }

        public String getCurrentSnapshotVersion() {
            return String.valueOf(version) + SNAPSHOT;
        }

        public String getNextReleaseVersion() {
            return String.valueOf(version + 1);
        }

        public String getNextSnapshotVersion() {
            return String.valueOf(version + 1) + SNAPSHOT;
        }

        public String getPreviousReleaseVersion() {
            return String.valueOf(version - 1);
        }

        public String getNextBranchSnapshotVersion() {
            return getCurrentReleaseVersion() + "-1" + SNAPSHOT;
        }

    }

}