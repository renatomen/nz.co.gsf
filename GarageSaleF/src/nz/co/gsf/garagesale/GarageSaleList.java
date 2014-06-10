/*
 * ISCG7424 – Mobile Software Development
 * Assignment 3: Garage Sales Finder
 * Parts: All
 * Student: Paul Shalley		(ID:1402195)
 * Student: Renato De Mendonca	(ID:1422497)
 * Student: Sergey Seriakov 	(ID:1405156)
 * Teacher: Dr. John Casey
 * 2014.
 */

package nz.co.gsf.garagesale;

import android.content.Context;
import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class GarageSaleList {

    private Context context = null;
    private ArrayList<GarageSale> list;

    public GarageSaleList(Context c) {
        this.context = c;
        this.list = new ArrayList<GarageSale>(0);
    }

    int add(GarageSale garageSale) {
        this.list.add(garageSale);
        return this.list.size();
    }

    GarageSale get(int location) {
        return this.list.get(location);
    }

    public ArrayList<GarageSale> getAll() {
        return this.list;
    }

    public int getCount() {
        return this.list.size();
    }

    void replace(GarageSale sale) {
        try {
            GarageSaleList newlist = new GarageSaleList(this.context);
            for (int i = 0; i < getCount(); i++) {
                GarageSale je = get(i);
                if (je.getId().equals(sale.getId())) {
                    Log.d("GFS", "Replacing Sale");
                    newlist.add(sale);
                } else {
                    newlist.add(je);
                }
            }
            this.list = newlist.list;
            persist();
        } catch (Exception e) {

        }
    }

    public void persist() {
        try {
            FileOutputStream fos = this.context.openFileOutput("GarageSale.xml", Context.MODE_PRIVATE);
            fos.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n".getBytes());
            fos.write("<garagesalelist>\n".getBytes());
            for (int i = 0; i < getCount(); i++) {
                GarageSale garageSale = get(i);
                fos.write(garageSale.toXMLString().getBytes());
            }
            fos.write("</garagesalelist>\n".getBytes());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Log.d("GFS", "Failed to write out file?" + e.getMessage());
        }
    }

    public static GarageSaleList parse(Context context) {
        try {
            FileInputStream fis = context.openFileInput("GarageSale.xml");

            if (fis == null) {
                return null;
            }
            // we need an input source for the sax parser
            InputSource is = new InputSource(fis);

            // create the factory
            SAXParserFactory factory = SAXParserFactory.newInstance();

            // create a parser
            SAXParser parser = factory.newSAXParser();

            // create the reader (scanner)
            XMLReader xmlreader = parser.getXMLReader();

            // instantiate our handler
            GarageSaleListHandler garageSaleListHandler = new GarageSaleListHandler(context, null /*
                                                                         * no progress updates when
                                                                         * reading file
                                                                         */);

            // assign our handler
            xmlreader.setContentHandler(garageSaleListHandler);

            // perform the synchronous parse
            xmlreader.parse(is);

            // clean up
            fis.close();

            // return our new garageSalelist
            return garageSaleListHandler.getList();
        } catch (Exception e) {
            Log.d("GFS-", "Error parsing garageSale garageSaleList xml file : " + e.getMessage());
            return null;
        }
    }

}


