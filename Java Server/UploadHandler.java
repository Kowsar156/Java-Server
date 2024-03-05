import java.io.*;
import java.net.Socket;

public class UploadHandler extends Thread{
    Socket socket;
    String fileType;
    File file;
    String fileName;

    UploadHandler(Socket socket, File file, String fileName, String fileType){
        this.socket = socket;
        this.file = file;
        this.fileType = fileType;
        this.fileName = fileName;
    }

    public void sendFile() throws Exception {
        System.out.println("Send File method initialized");
        int bytes = 0;
        int packet = 1;
        //File file = new File(path);
        FileInputStream fis = new FileInputStream(this.file);
        System.out.println("File created");


        DataOutputStream dos = new DataOutputStream(this.socket.getOutputStream());
        System.out.println("Output stream created");
        //dos.write((int) file.length());

        System.out.println("Inside Send File");
        byte[] buffer = new byte[4096];
        while ((bytes = fis.read(buffer)) != -1) {
            dos.write(buffer, 0, bytes);
            dos.flush();
            System.out.println("Packet " + packet + "sent!");
            packet++;
        }
        fis.close();
        dos.close();
    }

    public void run(){
        try {
            PrintWriter pr = new PrintWriter(this.socket.getOutputStream());
            pr.write("UPLOAD " + this.fileName + "\r\n");
            pr.write("\r\n");
            pr.flush();
            sendFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
