import org.apache.commons.io.FileUtils;

import java.io.*;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


// A class that takes the imagelist from a html file, and retrieve each of the embedded images in this list.
public class ImageRetriever {

    //Retrieve all images embedded in the html.
    public static void retrieveImages(List<String> imagelist, DataInputStream currentInStream, PrintWriter currentOutStream, String currentHost){

        List<String> internalImages = getInternalLinkList(imagelist);
        List<String> externalImages = getExternalLinkList(imagelist);
        Map<String, List<String>> externalImageMap = sortExternalLinksByHost(externalImages);



        for (String imageURI: internalImages){
            currentOutStream.println("GET " +imageURI +"HTTP/1.1");
            currentOutStream.println("Host: " + currentHost);
            currentOutStream.println();
            currentOutStream.flush();



            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] byteChunk = new byte[4096];
            try {
                int nb_bytes;
                while( (nb_bytes = currentInStream.read(byteChunk)) > 0  ){
                    baos.write(byteChunk, 0, nb_bytes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] image = baos.toByteArray();


            String workingDir = System.getProperty("user.dir");

//            String test = new String(image,0);
//            System.out.println("image: " + test);

            try {
                FileUtils.writeByteArrayToFile(new File(workingDir +"/imageCache/"+imageURI),image);
            } catch (IOException e) {
                e.printStackTrace();
            }


//            FileOutputStream stream = null;
//
//            try {
//                File f = new File(workingDir +"/imageCache/"+imageURI);
//                f.getParentFile().mkdirs();
//                f.createNewFile();
//                stream = new FileOutputStream(workingDir +"/imageCache/"+imageURI);
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            try {
//                stream.write(image);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    stream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }



    }


    //Checks whether the image URI is internal to the server we're connected to.
    private static boolean isInternalPicture(String toCheck){
        return ! (toCheck.startsWith("https://") || toCheck.startsWith("http://")) ;
    }

    //Returns a list with all the image URI's on external hosts.
    private static List<String> getExternalLinkList(List<String> imagelist){
        List<String> result = new ArrayList<String>();
        for (String imageURI: imagelist){
            if (!isInternalPicture(imageURI)){
                result.add(imageURI);
            }
        }
        return result;
    }

    //Returns a list with all image URI's on the host we're connected to.
    private static List<String> getInternalLinkList(List<String> imagelist){
        List<String> result = new ArrayList<String>();
        for (String imageURI: imagelist){
            if (isInternalPicture(imageURI)){
                result.add(imageURI);
            }
        }
        return result;
    }



    //Returns a Map with all different hosts as keys, and a list of the images on that host as value.
    private static Map<String, List<String>> sortExternalLinksByHost(List<String> imagelist){
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        for (String imageURI: imagelist){
            try {
                URL url = new URL(imageURI);
                String host = url.getHost();
                if (!result.containsKey(host)) {
                    List<String> newList = new ArrayList<String>();
                    result.put(host, newList);
                }
                result.get(host).add(imageURI);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
