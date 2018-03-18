import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.util.Collections.sort;


// A class that takes the imagelist from a html file, and retrieve each of the embedded images in this list.
public class ImageRetriever {

    //Retrieve all images embedded in the html.
    public static void retrieveImages(List<String> imageList, String currentHost) throws IOException {

        Path path;
        String workingDir =System.getProperty("user.dir");

        List<String> internalImages = getInternalLinkList(imageList);
        List<String> externalImages = getExternalLinkList(imageList);


        downloadInternalImages(currentHost, workingDir, internalImages);
        System.out.println("finished downloading internal files\n");
        downloadExternalImages(workingDir, externalImages);


    }

    private static void downloadExternalImages(String workingDir, List<String> externalImages) throws IOException {
        Path path;
        //sort on host name:
        //the same host names will come in front
        //TODO check if the same hostname is still active (via URL.gethost)
        Collections.sort(externalImages);
        for (String imageURI: externalImages){
            System.out.println("current image URL: " + imageURI);
            URL imageUrl = new URL(imageURI);
            String externalHost = (imageUrl.getHost());
            Socket socket = new Socket(externalHost, 80);

            System.out.println("sending request");
            PrintWriter outStream
                    = new PrintWriter(socket.getOutputStream());

            outStream.println("GET /" +imageURI.split(externalHost +"/")[1] +" HTTP/1.1");
            outStream.println("Host: " + externalHost);
            outStream.println();
            outStream.flush();

            System.out.println("Request sent");
            DataInputStream inputStream
                    = new DataInputStream(socket.getInputStream());
            Path downloadPath = Paths.get(workingDir,"RequestedPageCache", "imageCache", imageUrl.getPath());

            System.out.println("Start download");
            File downloadedFile = downLoadImage(inputStream, downloadPath);
            System.out.println("Download finished");
            System.out.println();

            inputStream.close();
            outStream.close();
            socket.close();
            String fileNameUri = imageUrl.getPath();
            removeHeader(workingDir, fileNameUri, downloadedFile );
        }
    }

//            InputStream fileStream = com.google.common.io.Files.asByteSource(file).openStream();
//            path = Paths.get(workingDir, "imageCache", "cleanedFiles",imageUrl.getPath());
//
//            File cleanedFile = new File(path.toUri());
//            cleanedFile.getParentFile().mkdirs();
//            cleanedFile.createNewFile();
//            OutputStream headerRemove = new FileOutputStream(cleanedFile);
//            int count, offset;
//            byte[] cleanerBuffer = new byte[2048];
//            boolean eohFound = false;
//            while ((count = fileStream.read(cleanerBuffer)) != -1)
//            {
//                offset = 0;
//                if(!eohFound){
//                    String string = new String(cleanerBuffer, 0, count);
//                    int indexOfEOH = string.indexOf("\r\n\r\n");
//                    if(indexOfEOH != -1) {
//                        count = count-indexOfEOH-4;
//                        offset = indexOfEOH+4;
//                        eohFound = true;
//                    } else {
//                        count = 0;
//                    }
//                }
//                headerRemove.write(cleanerBuffer, offset, count);
//                headerRemove.flush();
//            }
//            fileStream.close();
//            headerRemove.close();


    private static void downloadInternalImages(String currentHost, String workingDir, List<String> internalImages) throws IOException {
        Path path;
        Socket socket = new Socket(currentHost, 80);
        PrintWriter outStream
                = new PrintWriter(socket.getOutputStream());
        //create a new input stream
        InputStream inputStream = socket.getInputStream();

        for (String imageURI: internalImages){
            //send the get request

            System.out.println("Querying image: " + imageURI);
            outStream.println("GET /" + imageURI +" HTTP/1.1");
            outStream.println("Host: " + currentHost);
            outStream.println();
            outStream.flush();

            System.out.println("Request Sent");
            //create new data input stream
            DataInputStream dataInputStream
                    = new DataInputStream(inputStream);
            //get the path and create a file based on that
            Path downloadPath = Paths.get(workingDir,"RequestedPageCache","imageCache", imageURI);
            //created file based on the uri
            System.out.println("downloading file");
            File downloadFile = downLoadImage(dataInputStream, downloadPath);
            System.out.println("download finished");
            System.out.println();

            removeHeader(workingDir, imageURI, downloadFile);

        }

        inputStream.close();
        outStream.close();
        socket.close();
    }

    private static File downLoadImage(DataInputStream dataInputStream, Path downloadPath) throws IOException {
        File downloadFile = new File(downloadPath.toUri());
        downloadFile.getParentFile().mkdirs();
        downloadFile.createNewFile();
        DataOutputStream fileDataOutputStream = new DataOutputStream(new FileOutputStream(downloadFile));
        int size;
        //the buffer size to write bytes to the file
        byte[] buffer = new byte[8192];
        //read the data from the connection
        while ((size = dataInputStream.read(buffer)) > 0){
            fileDataOutputStream.write(buffer, 0, size);
            fileDataOutputStream.flush();
        }
        //the file is written
        fileDataOutputStream.close();
        return downloadFile;
    }

    private static void removeHeader(String workingDir, String imageURI, File downloadFile) throws IOException {
        //create the cleaned files path
        Path cleanFilePath = Paths.get(workingDir ,"RequestedPageCache","imageCache", "cleanedFiles" ,imageURI);

        //Remove HTTP Header from file to make it readable
        InputStream fileStream = com.google.common.io.Files.asByteSource(downloadFile).openStream();
        //create the file based on the uri
        File cleanedFile = new File(cleanFilePath.toUri());

        //create the dirs to avoid issues with the filesystem
        cleanedFile.getParentFile().mkdirs();
        cleanedFile.createNewFile();

        //create header removal output stream to the new file
        OutputStream headerRemove = new FileOutputStream(cleanedFile);
        //initiate the cleaner parameters
        int count, offset;
        byte[] cleanerBuffer = new byte[2048];
        boolean endOfHeaderFound = false;
        while ((count = fileStream.read(cleanerBuffer)) != -1)
        {
            offset = 0;
            if(!endOfHeaderFound){
                //create string from the buffer
                String string = new String(cleanerBuffer, 0, count);
                //search for the header in the string
                int indexOfEOH = string.indexOf("\r\n\r\n");
                //if found
                if(indexOfEOH != -1) {
                    //the count of the bytes to be transferred is the amount read minus the header
                    //bytes minus the CRLF characters to indicate the end of the header
                    count = count-indexOfEOH-4;
                    //the offset for the end of header, is four characters long
                    //so we need to jump over them
                    offset = indexOfEOH+4;
                    endOfHeaderFound = true;
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
    private static List<String> getInternalLinkList(List<String> imageList){
        List<String> result = new ArrayList<String>();
        for (String imageURI: imageList){
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
                    newList.add(url.getPath());
                    result.put(host, newList);
                }else{
                    result.get(host).add(imageURI);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
