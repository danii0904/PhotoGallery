package com.example.photogallery;

import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class PhotoComments {

    private static Document getCommentsXML(Context context) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        FileInputStream fis = new FileInputStream(new File(context.getFilesDir(), "photos" + File.separator + "PhotoComments.xml"));
        return builder.parse(fis);
    }

    private static Transformer transformerXML() {
        Transformer transformer = null;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        return transformer;
    }

    private static void updateXML(File xml, Document document, Transformer transformer) {
        try {
            StreamResult result = new StreamResult(new PrintWriter(new FileOutputStream(xml, false)));
            Source source = new DOMSource(document);
            transformer.transform(source, result);
        }catch (TransformerException e){

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    public static void commentsToXML(Context context, String name, String photoComment) {
        Document commentsDoc = null;
        try {
            commentsDoc = getCommentsXML(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (commentsDoc != null) {
            try {
                XPath xPath = XPathFactory.newInstance().newXPath();
                Node node = (Node) xPath.compile(name).evaluate(commentsDoc, XPathConstants.NODE);
                if (node != null)
                    node.setTextContent(photoComment);
                else {
                    Element e = commentsDoc.createElement("photoComment");
                    e.setAttribute(name, name);
                    e.setTextContent(photoComment);
                    Element root = commentsDoc.getDocumentElement();
                    root.appendChild(e);
                }
                Transformer transformer = transformerXML();
                updateXML(new File(context.getFilesDir(), "photos" + File.separator + "PhotoComments.xml"), commentsDoc, transformer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String commentsOnXML(Context context, String name) {
        String text = null;
        Document commentsDoc = null;
        try {
            commentsDoc = getCommentsXML(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (commentsDoc != null) {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            XPathExpression xPathExpr = null;
            try {
                xPathExpr = xpath.compile(name);
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }

            try {
                if (xPathExpr != null)
                    text = (String) xPathExpr.evaluate(commentsDoc, XPathConstants.STRING);
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }
        }
        return text;
    }

}
