package com.example.photogallery;

import android.content.Context;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class PhotoComments {

    private static Document getCommentsXML(Context context) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        FileInputStream fis = new FileInputStream(new File(context.getFilesDir(), "PhotoComments.xml"));//context.getFilesDir(), "photos" + File.separator + "PhotoComments.xml"
        return builder.parse(fis);
    }

    public static String getComments(Context context, String photoName) {
        String text = null;
        Document comments = null;

        try {
            comments = getCommentsXML(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (comments != null) {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            XPathExpression xPathExpr = null;
            try {
                xPathExpr = xpath.compile("//comment[@fileName = '" + photoName + "']/text()");
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }

            try {
                if (xPathExpr != null)
                    text = (String) xPathExpr.evaluate(comments, XPathConstants.STRING);
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }
        }

        return text;
    }

}
