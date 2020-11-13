package ru.itis.example_1;

import java.io.*;
import java.net.Socket;

public class Client {

    //Клиентский сокет
    private static Socket clientSocket;

    private static BufferedReader reader;
// нам нужен ридер читающий с консоли, иначе как


    /** Поток для чтения данных */
    private static BufferedReader in;

    /** Поток для записи данных */
    private static BufferedWriter out;


    public static void main(String[] args) {
        try {
            try {

                //Подключаем к серверу с портом 4004 (таким же как у сервера)
                clientSocket = new Socket("localhost", 4004);

                //Устанавливаем поток чтения из консоли
                reader = new BufferedReader(new InputStreamReader(System.in));

                //Устанавливаем потоки, для записи и чтения от клиентсокета
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                System.out.println("Вы что-то хотели сказать? Введите это здесь:");

                //Записаем некоторую строку из консоли в переменную
                String word = reader.readLine();

                //Записаем строку в поток записи
                out.write(word + "\n");

                //Принудительно отправляем данные, уоторый лежат в потоке записи
                out.flush();

                //Читаем то, что написал нам сервер
                String serverWord = in.readLine();

                System.out.println(serverWord);

            } finally {

                System.out.println("Клиент был закрыт...");

                //В любом случаем закрываем все потоки
                clientSocket.close();
                in.close();
                out.close();
            }
        } catch (IOException e) {
           e.printStackTrace();
        }
    }
}