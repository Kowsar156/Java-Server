import java.io.*;
import java.net.Socket;
import java.util.Date;

public class RequestHandler extends Thread{
    Socket socket;
    int response_number;

    public RequestHandler(Socket socket, int response_number){
        this.socket = socket;
        this.response_number = response_number;
    }

    public void writeLog(String log) throws Exception {
        String name = "log" + this.response_number + ".txt";
        File file = new File(name);
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.write(log);
        writer.close();
    }

    public String readFileData(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];

        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } finally {
            if (fileIn != null)
                fileIn.close();
        }

        return String.valueOf(fileData);
    }

    public String readTextFile(File file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String fileData = "";
        String demo;

        while ((demo = br.readLine()) != null){
            fileData += demo + "\n";
        }
        return fileData;
    }

    public String htmlContentMaker(String path) {
        File directoryPath = new File(path);
        String[] contents = directoryPath.list();
//        for (int i = 0 ; i < contents.length ; i++){
//            System.out.println(contents[i]);
//        }
        String content = "<html>\n" +
                "<head>\n" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                "</head>\n" +
                "<body>\n";
        String filePath = path + "/";
        String tempPath = null;



        for (int i = 0 ; i < contents.length ; i++){
            tempPath = filePath + contents[i];
            //File file = new File(tempPath);
            //if (file.isDirectory()){
            content += "<a href = \"/" + tempPath + "\">" + contents[i] + "</a><br>\n";
            //}
        }

        content += "</body>\n" + "</html>\n";

//        FileInputStream fis = new FileInputStream(file);
//        BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
//        StringBuilder sb = new StringBuilder();
//        String line;
//        while(( line = br.readLine()) != null ) {
//            sb.append( line );
//            sb.append( '\n' );
//        }
//
//        String content = sb.toString();
        //System.out.println(content);
        return content;
    }

//    public String imageHTMLContentMaker(String path){
//        String content = "<html>\n" +
//                "<head>\n" +
//                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
//                "</head>\n" +
//                "<body>\n";
//        content += "<img src = \"/" + path + "\">\n";
//        return content;
//    }

    public void sendFile(File file, Socket socket) throws Exception {
        System.out.println("Send File method initialized");
        int bytes = 0;
        int packet = 1;
        //File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        System.out.println("File created");


        OutputStream dos = socket.getOutputStream();
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

    public void receiveFile(String fileName, Socket socket) throws Exception {
        System.out.println("Receive File method initialized");
        int bytes = 0;
        int packet = 1;

        File file = new File("uploaded/"+fileName);
        FileOutputStream fos = new FileOutputStream(file);
        System.out.println("File created");

        DataInputStream dis = new DataInputStream(socket.getInputStream());
        System.out.println("Input stream created");

        System.out.println("Inside Receive File");
        byte[] buffer = new byte[4096];
        while ((bytes = dis.read(buffer, 0, buffer.length)) != -1) {
            fos.write(buffer);
            fos.flush();
            System.out.println("Packet " + packet + "received!");
            packet++;
        }
        dis.close();
        fos.close();
    }

    public void run(){
        String defaultContent = "<html>\n" +
                "\t<head>\n" +
                "\t\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                "\t</head>\n" +
                "\t<body>\n" +
                "\t\t<h1> Welcome to CSE 322 Offline 1 </h1> <br>\n" +
                "\t\t<a href=\"/root\">root directory</a>\n" +
                "\t</body>\n" +
                "</html>";
        try{
            //while (true){
                //String defaultPathName = "/root";
//                File directoryPath = new File(defaultPathName);
//                String[] contents = directoryPath.list();
                BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                PrintWriter pr = new PrintWriter(this.socket.getOutputStream());
                String input = in.readLine();
                System.out.println("input : "+input);

                // String content = "<html>Hello</html>";
                String response_message = input + "\n\n";
                if(input == null){
                    socket.close();
                    //break;
                    //continue;
                }
                else if(input.length() > 0) {
                    String[] inputString = input.split(" ");
                    String pathName = inputString[1];
                    String fileName = pathName.substring(1);
                    //System.out.println("FileName: " + fileName + "\n");
                    File targetFile = new File(fileName);
                    //String[] demo = fileName.split(".");
                    //System.out.println("demo length: " + demo.length + "\n");
                    String fileType = null;
                    if (fileName.lastIndexOf(".") != -1){
                        fileType = fileName.substring(fileName.lastIndexOf(".")+1);
                    }
                    String content = null;
                    if(input.startsWith("GET"))
                    {
                        if (fileName == ""){
                            content = defaultContent;
                            pr.write("HTTP/1.1 200 OK\r\n");
                            pr.write("Server: Java HTTP Server: 1.0\r\n");
                            pr.write("Date: " + new Date() + "\r\n");
                            pr.write("Content-Type: text/html\r\n");
                            pr.write("Content-Length: " + content.length() + "\r\n");
                            pr.write("\r\n");
                            pr.write(content);
                            response_message += "HTTP/1.1 200 OK\n" + "Server: Java HTTP Server: 1.0\n" + "Date: " + new Date() + "\n" + "Content-Type: text/html\n" + "Content-Length: " + content.length() + "\n";
                            writeLog(response_message);
                        }
                        else if (!targetFile.exists()){
                            content = "<html>\\n\" +\n" +
                                    "                \"\\t<head>\\n\" +\n" +
                                    "                \"\\t\\t<meta http-equiv=\\\"Content-Type\\\" content=\\\"text/html; charset=UTF-8\\\">\\n\" +\n" +
                                    "                \"\\t</head>\\n\" +\n" +
                                    "                \"\\t<body>\\n\" +\n" +
                                    "                \"\\t\\t<h1> Error! Path not found! </h1> <br>\\n\" +\n" +
                                    "                \"\\t</body>\\n\" +\n" +
                                    "                \"</html>";
                            pr.write("HTTP/1.1 404 Not found\r\n");
                            pr.write("Server: Java HTTP Server: 1.0\r\n");
                            pr.write("Date: " + new Date() + "\r\n");
                            pr.write("Content-Type: text/html\r\n");
                            pr.write("Content-Length: " + content.length() + "\r\n");
                            pr.write("\r\n");
                            pr.write(content);
                            response_message += "HTTP/1.1 404 Not found\n" + "Server: Java HTTP Server: 1.0\n" + "Date: " + new Date() + "\n" + "Content-Type: text/html\n" + "Content-Length: " + content.length() + "\n";
                            writeLog(response_message);
                        }
                        else if (targetFile.isDirectory()){
                            content = htmlContentMaker(fileName);
                            pr.write("HTTP/1.1 200 OK\r\n");
                            pr.write("Server: Java HTTP Server: 1.0\r\n");
                            pr.write("Date: " + new Date() + "\r\n");
                            pr.write("Content-Type: text/html\r\n");
                            pr.write("Content-Length: " + content.length() + "\r\n");
                            pr.write("\r\n");
                            pr.write(content);
                            response_message += "HTTP/1.1 200 OK\n" + "Server: Java HTTP Server: 1.0\n" + "Date: " + new Date() + "\n" + "Content-Type: text/html\n" + "Content-Length: " + content.length() + "\n";
                            writeLog(response_message);
                         }
                        else if (fileType.equalsIgnoreCase("jpg") || fileType.equalsIgnoreCase("jpeg") || fileType.equalsIgnoreCase("png") || fileType.equalsIgnoreCase("bmp") || fileType.equalsIgnoreCase("gif")) {
                            //content = imageHTMLContentMaker(fileName);
                            File newFile = new File(fileName);
                            pr.write("HTTP/1.1 200 OK\r\n");
                            pr.write("Server: Java HTTP Server: 1.0\r\n");
                            pr.write("Date: " + new Date() + "\r\n");
                            pr.write("Content-Type: image/jpg\r\n");
                            pr.write("Content-Length: " + newFile.length() + "\r\n");
                            pr.write("\r\n");
                            pr.flush();
                            sendFile(newFile, this.socket);
                            response_message += "HTTP/1.1 200 OK\n" + "Server: Java HTTP Server: 1.0\n" + "Date: " + new Date() + "\n" + "Content-Type: image/jpg\n" + "Content-Length: " + newFile.length() + "\n";
                            writeLog(response_message);
                        }
                        else if (fileType.equalsIgnoreCase("txt")) {
                            File newFile = new File(fileName);
                            String text = readTextFile(newFile);
                            String textContent = "<html>\n" +
                                    "<head>\n" +
                                    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                                    "</head>\n" +
                                    "<body>\n" + text + "\n</body>\n" + "</html>\n";
                            pr.write("HTTP/1.1 200 OK\r\n");
                            pr.write("Server: Java HTTP Server: 1.0\r\n");
                            pr.write("Date: " + new Date() + "\r\n");
                            pr.write("Content-Type: text/html\r\n");
                            pr.write("Content-Length: " + textContent.length() + "\r\n");
                            pr.write("\r\n");
                            pr.write(textContent);
                            response_message += "HTTP/1.1 200 OK\n" + "Server: Java HTTP Server: 1.0\n" + "Date: " + new Date() + "\n" + "Content-Type: text/html\n" + "Content-Length: " + content.length() + "\n";
                            writeLog(response_message);
                        }
                        else{
                            File newFile = new File(fileName);
                            pr.write("HTTP/1.1 200 OK\r\n");
                            pr.write("Server: Java HTTP Server: 1.0\r\n");
                            pr.write("Date: " + new Date() + "\r\n");
                            pr.write("Content-Type: application/x-force-download\r\n");
                            pr.write("Content-Length: " + newFile.length() + "\r\n");
                            pr.write("\r\n");
                            pr.flush();
                            sendFile(newFile, this.socket);
                            response_message += "HTTP/1.1 200 OK\n" + "Server: Java HTTP Server: 1.0\n" + "Date: " + new Date() + "\n" + "Content-Type: application/x-force-download\n" + "Content-Length: " + newFile.length() + "\n";
                            writeLog(response_message);
                        }
                        pr.flush();
                    }
                    else if (input.startsWith("UPLOAD")) {
                        String[] receiveInputString = input.split(" ");
                        String receiveFileName = inputString[1];
                        receiveFile(receiveFileName, this.socket);
                    }

//                else
//                {
//
//                }
                }
                //pr.close();
            //}
        } catch (Exception e){
//            e.printStackTrace();
//            try {
//                this.socket.close();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
        }
        if (!this.socket.isClosed()){
            this.response_number++;
            Thread requestHandler = new RequestHandler(this.socket, this.response_number);
            requestHandler.start();
        }
    }
}
