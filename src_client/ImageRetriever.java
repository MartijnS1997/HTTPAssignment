import org.apache.commons.io.FileUtils;

import java.io.*;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.UnexpectedException;
import java.util.*;


// A class that takes the imagelist from a html file, and retrieve each of the embedded images in this list.
public class ImageRetriever {

    //Retrieve all images embedded in the html.
    public static void retrieveImages(List<String> imagelist, String currentHost) throws IOException {

        Path path;
        String workingDir = System.getProperty("user.dir");


        List<String> internalImages = getInternalLinkList(imagelist);
        List<String> externalImages = getExternalLinkList(imagelist);




        for (String imageURI: internalImages){

            Socket socket = new Socket(currentHost, 80);

            PrintWriter outStream
                    = new PrintWriter(socket.getOutputStream());

            outStream.println("GET /" +imageURI +" HTTP/1.1");
            outStream.println("Host: " + currentHost);
            outStream.println();
            outStream.flush();

            InputStream inputStream = socket.getInputStream();


            java.io.DataInputStream dataInputStream
                    = new java.io.DataInputStream(inputStream);
            path = Paths.get(workingDir,"imageCache", imageURI);
            File file = new File(path.toUri());
            file.getParentFile().mkdirs();
            file.createNewFile();
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
            int size;
            byte[] buffer = new byte[8192];
            while ((size = dataInputStream.read(buffer)) > 0){
                dos.write(buffer, 0, size);
                dos.flush();
            }
            dos.close();


            inputStream.close();
            outStream.close();
            socket.close();

            path = Paths.get(workingDir ,"imageCache", "cleanedFiles" ,imageURI);

            //Remove HTTP Header from file to make it readable
            InputStream fileStream = com.google.common.io.Files.asByteSource(file).openStream();
            File cleanedFile = new File(path.toUri());

            cleanedFile.getParentFile().mkdirs();
            cleanedFile.createNewFile();
            OutputStream headerRemove = new FileOutputStream(cleanedFile);
            int count, offset;
            byte[] cleanerBuffer = new byte[2048];
            boolean eohFound = false;
            while ((count = fileStream.read(cleanerBuffer)) != -1)
            {
                offset = 0;
                if(!eohFound){
                    String string = new String(cleanerBuffer, 0, count);
                    int indexOfEOH = string.indexOf("\r\n\r\n");
                    if(indexOfEOH != -1) {
                        count = count-indexOfEOH-4;
                        offset = indexOfEOH+4;
                        eohFound = true;
                    } else {
                        count = 0;
                    }
                }
                headerRemove.write(cleanerBuffer, offset, count);
                headerRemove.flush();
            }
            fileStream.close();
            headerRemove.close();


        }

        for (String imageURI: externalImages){
            String externalHost = (new URL(imageURI)).getHost();
            Socket socket = new Socket(externalHost, 80);

            PrintWriter outStream
                    = new PrintWriter(socket.getOutputStream());

            outStream.println("GET /" +imageURI.split(externalHost +"/")[1] +" HTTP/1.1");
            outStream.println("Host: " + externalHost);
            outStream.println();
            outStream.flush();




            java.io.DataInputStream inputStream
                    = new java.io.DataInputStream(socket.getInputStream());
            path = Paths.get(workingDir, "imageCache", imageURI);
            File file = new File(path.toUri());
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

            InputStream fileStream = com.google.common.io.Files.asByteSource(file).openStream();
            path = Paths.get(workingDir, "imageCache", "cleanedFiles",imageURI);

            File cleanedFile = new File(path.toUri());
            cleanedFile.getParentFile().mkdirs();
            cleanedFile.createNewFile();
            OutputStream headerRemove = new FileOutputStream(cleanedFile);
            int count, offset;
            byte[] cleanerBuffer = new byte[2048];
            boolean eohFound = false;
            while ((count = fileStream.read(cleanerBuffer)) != -1)
            {
                offset = 0;
                if(!eohFound){
                    String string = new String(cleanerBuffer, 0, count);
                    int indexOfEOH = string.indexOf("\r\n\r\n");
                    if(indexOfEOH != -1) {
                        count = count-indexOfEOH-4;
                        offset = indexOfEOH+4;
                        eohFound = true;
                    } else {
                        count = 0;
                    }
                }
                headerRemove.write(cleanerBuffer, offset, count);
                headerRemove.flush();
            }
            fileStream.close();
            headerRemove.close();


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
