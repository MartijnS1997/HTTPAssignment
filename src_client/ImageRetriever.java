import org.apache.commons.io.FileUtils;

import java.io.*;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.*;


// A class that takes the imagelist from a html file, and retrieve each of the embedded images in this list.
public class ImageRetriever {

    //Retrieve all images embedded in the html.
    public static void retrieveImages(List<String> imagelist, String currentHost) throws IOException {





        List<String> internalImages = getInternalLinkList(imagelist);
        List<String> externalImages = getExternalLinkList(imagelist);
        Map<String, List<String>> externalImageMap = sortExternalLinksByHost(externalImages);



        for (String imageURI: internalImages){
            Socket socket = new Socket(currentHost, 80);

            PrintWriter outStream
                    = new PrintWriter(socket.getOutputStream());

            outStream.println("GET /" +imageURI +" HTTP/1.1");
            outStream.println("Host: " + currentHost);
            outStream.println();
            outStream.flush();




            java.io.DataInputStream inputStream
                    = new java.io.DataInputStream(socket.getInputStream());
            String workingDir = System.getProperty("user.dir");
            File file = new File(workingDir +"/imageCache/"+imageURI);
            file.getParentFile().mkdirs();
            file.createNewFile();
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
            int size;
            byte[] buffer = new byte[8192];
            while ((size = inputStream.read(buffer)) > 0){
                dos.write(buffer, 0, size);
                dos.flush();
            }
            dos.close();

            inputStream.close();
            outStream.close();
            socket.close();


        }

        for (String imageURI: externalImages){
            String externalHost = (new URL(imageURI)).getHost();
            System.out.println(externalHost);
            Socket socket = new Socket(externalHost, 80);

            PrintWriter outStream
                    = new PrintWriter(socket.getOutputStream());

            outStream.println("GET /" +imageURI +" HTTP/1.1");
            outStream.println("Host: " + externalHost);
            outStream.println();
            outStream.flush();




            java.io.DataInputStream inputStream
                    = new java.io.DataInputStream(socket.getInputStream());
            String workingDir = System.getProperty("user.dir");
            File file = new File(workingDir +"/imageCache/"+imageURI);
            file.getParentFile().mkdirs();
            file.createNewFile();
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
            int size;
            byte[] buffer = new byte[8192];
            while ((size = inputStream.read(buffer)) > 0){
                System.out.println(size);
                dos.write(buffer, 0, size);
                dos.flush();
            }
            dos.close();

            System.out.println("image transfer done");
            inputStream.close();
            outStream.close();
            socket.close();


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
