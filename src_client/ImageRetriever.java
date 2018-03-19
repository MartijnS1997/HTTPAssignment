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
    public static void retrieveImages(List<String> imageList, String currentHost,
                                      DataInputStream parentInputStream, DataOutputStream parentOutputStream) throws IOException {

        Path path;
        String workingDir =System.getProperty("user.dir");

        List<String> internalImages = getInternalLinkList(imageList);
        List<String> externalImages = getExternalLinkList(imageList);


        downloadInternalImages(currentHost, workingDir, internalImages,parentInputStream, parentOutputStream);
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

            ClientResponseHeader responseHeader = new ClientResponseHeader();
            responseHeader.readResponseHeader(new DataInputStream(inputStream));
            long downloadSize = responseHeader.getContentLength();

            System.out.println("Start download");
            File downloadedFile = downLoadImage(inputStream, downloadPath, downloadSize);
            System.out.println("Download finished");
            System.out.println();

            inputStream.close();
            outStream.close();
            socket.close();
            String fileNameUri = imageUrl.getPath();
        }
    }


    private static void downloadInternalImages(String currentHost,
                                               String workingDir, List<String> internalImages, DataInputStream parentInputStream, DataOutputStream parentOutputstream) throws IOException {
//        Path path;
//        Socket socket = new Socket(currentHost, 80);
//        PrintWriter outStream
//                = new PrintWriter(socket.getOutputStream());
//        //create a new input stream
//        DataInputStream inputStream = new DataInputStream(socket.getInputStream());

        for (String imageURI: internalImages){
            //send the get request

//            System.out.println("Querying image: " + imageURI);
//            outStream.println("GET /" + imageURI +" HTTP/1.1");
//            System.out.println("GET /" + imageURI +" HTTP/1.1");
//            outStream.println("Host: " + currentHost);
//            System.out.println("Host: " + currentHost);
//            System.out.println("Connection: keep-alive");
//            outStream.println();
//            outStream.flush();
            sendInternalGet(imageURI, currentHost, parentOutputstream);

            while((parentInputStream.available()==0)){
                //do nothing
            }

            ClientResponseHeader responseHeader = new ClientResponseHeader();
            responseHeader.readResponseHeader(new DataInputStream(parentInputStream));
            long downloadSize = responseHeader.getContentLength();
            System.out.println(responseHeader.toString());

            System.out.println("Request Sent");
            //create new data input stream
            DataInputStream dataInputStream
                    = new DataInputStream(parentInputStream);
            //get the path and create a file based on that
            Path downloadPath = Paths.get(workingDir,"RequestedPageCache","imageCache", imageURI);
            //created file based on the uri
            System.out.println("downloading file");
            File downloadFile = downLoadImage(dataInputStream, downloadPath, downloadSize);
            System.out.println("download finished");
            System.out.println();


        }

//        inputStream.close();
//        outStream.close();
//        socket.close();
    }

    private static File downLoadImage(DataInputStream dataInputStream, Path downloadPath, long downloadSize) throws IOException {

        File downloadFile = new File(downloadPath.toUri());
        downloadFile.getParentFile().mkdirs();
        downloadFile.createNewFile();

        DataOutputStream fileDataOutputStream = new DataOutputStream(new FileOutputStream(downloadFile));
        int size;
        //the buffer roughly 1/10th of the size of the file
        byte[] buffer = new byte[Math.toIntExact(downloadSize)/10];
        //read the data from the connection
        //System.out.println("nb of bytes available @Stream: " + dataInputStream.available());
        //start reading the buffer, keep reading until the download size reached zero
        long bytesToDownload = downloadSize;

        while(bytesToDownload != 0){
            size = dataInputStream.read(buffer);
            //System.out.println("Read bytes: " + size);
            bytesToDownload-=size;

            fileDataOutputStream.write(buffer, 0, size);
            fileDataOutputStream.flush();

            //check if the buffer is still larger than the file to download
            if(bytesToDownload < buffer.length){
                //if not adjust the buffer size
                buffer = new byte[Math.toIntExact(bytesToDownload)];
            }
        }
        //the file is written
        fileDataOutputStream.close();

        return downloadFile;
    }

//    private static void removeHeader(String workingDir, String imageURI, File downloadFile) throws IOException {
//        //create the cleaned files path
//        Path cleanFilePath = Paths.get(workingDir ,"RequestedPageCache","imageCache", "cleanedFiles" ,imageURI);
//
//        //Remove HTTP Header from file to make it readable
//        InputStream fileStream = com.google.common.io.Files.asByteSource(downloadFile).openStream();
//        //create the file based on the uri
//        File cleanedFile = new File(cleanFilePath.toUri());
//
//        //create the dirs to avoid issues with the filesystem
//        cleanedFile.getParentFile().mkdirs();
//        cleanedFile.createNewFile();
//
//        //create header removal output stream to the new file
//        OutputStream headerRemove = new FileOutputStream(cleanedFile);
//        //initiate the cleaner parameters
//        int count, offset;
//        byte[] cleanerBuffer = new byte[2048];
//        boolean endOfHeaderFound = false;
//
//
//        fileStream.close();
//        headerRemove.close();
//    }


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

    private static void sendInternalGet(String imageURI, String currentHost, DataOutputStream outputStream){
        List<String> headerLines = new ArrayList<>();
        headerLines.add("GET /" + imageURI +" HTTP/1.1");
        headerLines.add("Host: " + currentHost);
        headerLines.add("Connection: keep-alive");

        System.out.println("Querying image: " + imageURI);
        System.out.println("GET /" + imageURI +" HTTP/1.1");
        System.out.println("Host: " + currentHost);
        System.out.println("Connection: keep-alive");


        HttpRequest.sendRequestHeader(headerLines, outputStream);

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
