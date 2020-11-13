package ru.itis.example_1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    /** Прослушиваемый сокет */
    private static Socket clientSocket;

    /** Серверсокет */
    private static ServerSocket server;

    /** Поток для чтения данных */
    private static BufferedReader in;

    /** Поток для записи данных */
    private static BufferedWriter out;

    public static void main(String[] args) {
        try {
            try {
                //Серверсокет, который прослушивает на порту 4004
                server = new ServerSocket(4004);

                System.out.println("Сервер запущен!");

                //Программа будет ждать пока кто-либо подсоединиться к серверу и вернёт его socket
                clientSocket = server.accept();

                try {
                    //Устанавливаем потоки, для записи и чтения от серверсокета
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                    //Читаем строку, которыую написал нам client
                    String word = in.readLine();

                    System.out.println(word);

                    //Отвечаем client о том, что мы получили сообщение
                    out.write("Привет, это Сервер! Подтверждаю, вы написали : " + word + "\n");

                    //Вытаскиваем все данные, которые лежат в буфере (принудительно)
                    out.flush();

                } finally {

                    //Закрываем серверсокет
                    clientSocket.close();

                    //Закрываем потоки ввода и вывода
                    in.close();
                    out.close();
                }
            } finally {
                System.out.println("Сервер закрыт!");
                server.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}