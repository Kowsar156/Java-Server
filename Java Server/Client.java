import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 5075);
        System.out.println("Connection established");
        //System.out.println("Remote port: " + socket.getPort());
        //System.out.println("Local port: " + socket.getLocalPort());
        Scanner scn = new Scanner(System.in);
        int number_of_files;
        String fileName;

        System.out.println("How many files do you want to upload?");
        number_of_files = scn.nextInt();
        scn.nextLine();
        System.out.println("Please give file names one by one");
        for (int i = 0 ; i < number_of_files ; i++){
            fileName = scn.nextLine();
            File file = new File(fileName);
            String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
            if (file.exists()){
                Thread uploader = new UploadHandler(socket, file, fileName, fileType);
                uploader.start();
            }
            else {
                System.out.println("No such file!");
            }
        }
    }
}
