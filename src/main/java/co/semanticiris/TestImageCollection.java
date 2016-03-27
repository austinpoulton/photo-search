package co.semanticiris;

import co.semanticiris.model.ImageCollection;

/**
 * Created by austin on 26/03/2016.
 */
public class TestImageCollection {


    public static void main(String args[]) {

        ImageCollection ic = ImageCollection.load("/var/irdata/flickrImageColl.ser");
        System.out.println(" doc count: "+ic.documentCount());
        System.out.println(" term count: "+ic.termCount());
    }
}