import com.devexperts.gft.utils.MavenVersion;
import org.junit.Assert;
import org.junit.Test;

public class MavenVersionTest {

    public static junit.framework.Test suite() { 
        return new junit.framework.JUnit4TestAdapter(MavenVersionTest.class); 
    }    

    @Test
    public void testSnapshotVersionParsed() {
        MavenVersion version = MavenVersion.fromString("1-4-11-SNAPSHOT");
        Assert.assertEquals("1-4-11", version.getCurrentReleaseVersion());
        Assert.assertEquals("1-4-11-SNAPSHOT", version.getCurrentSnapshotVersion());
        Assert.assertEquals("1-4-12-SNAPSHOT", version.getNextSnapshotVersion());
        Assert.assertEquals("1-4-12", version.getNextReleaseVersion());
        Assert.assertEquals("1-4-10", version.getPreviousReleaseVersion());
        Assert.assertEquals("1-4-11-1-SNAPSHOT", version.getNextBranchSnapshotVersion());
    }

    @Test
    public void testSnapshotParsed_SimpleVersion() {
        MavenVersion version = MavenVersion.fromString("10-SNAPSHOT");
        Assert.assertEquals("10", version.getCurrentReleaseVersion());
        Assert.assertEquals("10-SNAPSHOT", version.getCurrentSnapshotVersion());
        Assert.assertEquals("11-SNAPSHOT", version.getNextSnapshotVersion());
        Assert.assertEquals("11", version.getNextReleaseVersion());
        Assert.assertEquals("9", version.getPreviousReleaseVersion());
        Assert.assertEquals("10-1-SNAPSHOT", version.getNextBranchSnapshotVersion());
    }

    @Test
    public void testSnapshotParsed_SimpleVersionBranch() {
        MavenVersion version = MavenVersion.fromString("10-2-SNAPSHOT");
        Assert.assertEquals("10-2", version.getCurrentReleaseVersion());
        Assert.assertEquals("10-2-SNAPSHOT", version.getCurrentSnapshotVersion());
        Assert.assertEquals("10-3-SNAPSHOT", version.getNextSnapshotVersion());
        Assert.assertEquals("10-3", version.getNextReleaseVersion());
        Assert.assertEquals("10-1", version.getPreviousReleaseVersion());
        Assert.assertEquals("10-2-1-SNAPSHOT", version.getNextBranchSnapshotVersion());
    }

    @Test
    public void testReleaseVersionParsed() {
        MavenVersion version = MavenVersion.fromString("1-4-11");
        Assert.assertEquals("1-4-11", version.getCurrentReleaseVersion());
        Assert.assertEquals("1-4-11-SNAPSHOT", version.getCurrentSnapshotVersion());
        Assert.assertEquals("1-4-12-SNAPSHOT", version.getNextSnapshotVersion());
        Assert.assertEquals("1-4-12", version.getNextReleaseVersion());
        Assert.assertEquals("1-4-10", version.getPreviousReleaseVersion());
        Assert.assertEquals("1-4-11-1-SNAPSHOT", version.getNextBranchSnapshotVersion());
    }

    @Test
    public void testCompReleaseVersionParsed() {
        MavenVersion version = MavenVersion.fromString("1200-7-1-1");
        Assert.assertEquals("1200-7-1-1", version.getCurrentReleaseVersion());
        Assert.assertEquals("1200-7-1-1-SNAPSHOT", version.getCurrentSnapshotVersion());
        Assert.assertEquals("1200-7-1-2-SNAPSHOT", version.getNextSnapshotVersion());
        Assert.assertEquals("1200-7-1-2", version.getNextReleaseVersion());
        Assert.assertEquals("1200-7-1", version.getPreviousReleaseVersion());
        Assert.assertEquals("1200-7-1-1-1-SNAPSHOT", version.getNextBranchSnapshotVersion());
    }

    @Test
    public void testReleaseVersionParsed_Simple() {
        MavenVersion version = MavenVersion.fromString("10");
        Assert.assertEquals("10", version.getCurrentReleaseVersion());
        Assert.assertEquals("10-SNAPSHOT", version.getCurrentSnapshotVersion());
        Assert.assertEquals("11-SNAPSHOT", version.getNextSnapshotVersion());
        Assert.assertEquals("11", version.getNextReleaseVersion());
        Assert.assertEquals("9", version.getPreviousReleaseVersion());
        Assert.assertEquals("10-1-SNAPSHOT", version.getNextBranchSnapshotVersion());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReleaseVersionParsed_IncorrectVersionFormat() {
        MavenVersion.fromString("R-1-4-1x1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReleaseVersionParsed_AbsolutelyIncorrectVersionFormat() {
        MavenVersion.fromString("R141=2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReleaseVersionParsed_IncorrectMiddleFormat() {
        MavenVersion.fromString("R-1-x-11");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReleaseVersionParsed_IncorrectMajorFormat() {
        MavenVersion.fromString("R-x-1-11");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReleaseVersionParsed_IncorrectPrefix() {
        MavenVersion.fromString("H-1-1-11");
    }
}