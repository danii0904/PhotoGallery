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

public class PhotoComments {

    private static Document getCommentsDocument(Context context) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        FileInputStream fis = new FileInputStream(new File(context.getFilesDir(), "photos" + File.separator + "PhotoComments.xml"));
        return builder.parse(fis);
    }

}
