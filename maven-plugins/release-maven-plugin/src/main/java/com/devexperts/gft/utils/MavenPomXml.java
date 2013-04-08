package com.devexperts.gft.utils;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Author: Andrew Logvinov
 * Date: 01/12/12
 * Time: 6:44 PM
 */
public class MavenPomXml {

    private final Document document;
    private final Namespace namespace;
    private final Element projectProperties;

    public MavenPomXml(File file) throws JDOMException, IOException {
        document = new SAXBuilder().build(file);
        namespace = document.getRootElement().getNamespace();
        projectProperties = document.getRootElement().getChild("properties", namespace);
    }

    public Document getXmlDocument() {
        return document;
    }

    public boolean isPropertyPresent(String property) {
        return projectProperties.getChild(property, namespace) != null;
    }

    public String getPropertyValue(String property) {
        return projectProperties.getChild(property, namespace).getValue();
    }

    public void setPropertyValue(String property, String value) {
        projectProperties.getChild(property, namespace).setText(value);
    }

    public void addPropertyWithValue(String property, String value) {
        Element element = new Element(property, namespace);
        element.setText(value);
        projectProperties.addContent(element);
    }

    public static void writeXmlDocument(Document document, File file) throws IOException {
        XMLOutputter xmlOut = new XMLOutputter();
        Format rawFormat = Format.getRawFormat();
        rawFormat.setLineSeparator("\n");
        rawFormat.setTextMode(Format.TextMode.PRESERVE);
        xmlOut.setFormat(rawFormat);
        xmlOut.output(document, new FileWriter(file));
    }
}