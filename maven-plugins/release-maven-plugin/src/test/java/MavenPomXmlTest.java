import com.devexperts.gft.utils.MavenPomXml;
import org.jdom2.JDOMException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class MavenPomXmlTest {

    private MavenPomXml pomXml;

    @Before
    public void setUp() throws JDOMException, IOException {
        pomXml = new MavenPomXml(new File("src/test/suppl/MavenPomXmlTest.xml"));
    }

    @Test
    public void testIsPropertyPresent() {
        assertEquals(true, pomXml.isPropertyPresent("property_1"));
        assertEquals(false, pomXml.isPropertyPresent("property_3"));
    }

    @Test
    public void testGetPropertyValue_PropertyExists() {
        assertEquals("val1", pomXml.getPropertyValue("property_1"));
        assertEquals("", pomXml.getPropertyValue("property_2"));
    }

    @Test(expected = NullPointerException.class)
    public void testGetPropertyValue_PropertyNotExists() {
        pomXml.getPropertyValue("property_3");
    }

    @Test
    public void testSetPropertyValue_PropertyExists() {
        pomXml.setPropertyValue("property_2", "val2");
        assertEquals("val2", pomXml.getPropertyValue("property_2"));
    }

    @Test(expected = NullPointerException.class)
    public void testSetPropertyValue_PropertyNotExists() {
        pomXml.setPropertyValue("property_3", "val3");
    }

    @Test
    public void testAddPropertyWithValue() {
        pomXml.addPropertyWithValue("property_3", "val3");
        assertEquals("val3", pomXml.getPropertyValue("property_3"));
    }
}