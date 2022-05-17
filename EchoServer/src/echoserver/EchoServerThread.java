/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echoserver;

import java.net.*;
import java.io.*;
import java.util.*;


class users {

  String Name;
  String Surname;
  long PESEL;
  int AccNum;
  double Balance;

}



public class EchoServerThread implements Runnable {
  protected Socket socket;

  public EchoServerThread(Socket clientSocket) {
    this.socket = clientSocket;
  }

  public void run() {
    //Deklaracje zmiennych
    BufferedReader brinp = null;
    DataOutputStream out = null;
    String threadName = Thread.currentThread().getName();

    //inicjalizacja strumieni
    try {
      brinp = new BufferedReader(
              new InputStreamReader(
                      socket.getInputStream()
              )
      );
      out = new DataOutputStream(socket.getOutputStream());
    } catch (IOException e) {
      System.out.println(threadName + "| Błąd przy tworzeniu strumieni " + e);
      return;
    }
    String line = null;

    //pętla główna
    while (true) {
      try {

        String login = "Podaj swój login";
        out.writeBytes(login + "\n");
        out.flush();
        login = brinp.readLine();
        System.out.println(login);

        if ("Admin".equals(login)) {
          String wtd = "Witaj w panelu administacyjnym. Co chcesz zrobić?";
          out.writeBytes(wtd + "\n\r");
          out.flush();
          brinp.readLine();

        } else {

          String accnum = "Podaj nr.konta";
          out.writeBytes(accnum + "\n\r");
          out.flush();
          accnum = brinp.readLine();

          String tast = "Co chcesz zrobic: 1. Sprawdzić stan konta 2. Wypłacić środki 3. Wpłacić środki  4. zrobić przelew ";
          out.writeBytes(tast + "\n\r");
          out.flush();
          tast = brinp.readLine();

          Scanner scanner = new Scanner(new File("users.txt"));
          int l = 1;

          ArrayList<users> users = new ArrayList<>();

          while (scanner.hasNext()) {

            users user = new users();

            user.Name = scanner.next();
            user.Surname = scanner.next();
            user.PESEL = scanner.nextLong();
            user.AccNum = scanner.nextInt();
            user.Balance = Double.parseDouble((scanner.next()));

            users.add(user);
          }


/*                users user = new users();
                user.Name = "Mateusz";
                user.Surname = "Sierski";
                user.PESEL = 002202200;
                user.AccNum = 8888;
                user.Balance = 2000;

                users.add(user); */

          System.out.println(accnum);
          int AccNumb = Integer.parseInt(accnum);



          if ("1".equals(tast)) {

            for (int i = 0; i < users.size(); i++) {

              if (users.get(i).AccNum == AccNumb) {
                System.out.println("Stan konta " + users.get(i).Balance);
                String money = "Stan konta " + users.get(i).Balance;
                out.writeBytes(money + "\n");
                out.flush();
                break;

              }
            }
          } else if ("2".equals(tast)) {

            String moneyT = "Ile pieniędzy wypłacić?";
            out.writeBytes(moneyT + '\n');
            out.flush();
            moneyT = brinp.readLine();

            double toWithdraw = Double.parseDouble(moneyT);

            for (int i = 0; i < users.size(); i++) {

              if (users.get(i).AccNum == AccNumb) {
                double tmp = users.get(i).Balance;
                tmp = tmp - toWithdraw;
                users.get(i).Balance = tmp;
                break;

              }
            }

          } else if ("3".equals(tast)) {

            String moneyF = "Ile pieniędzy wpłacić?";
            out.writeBytes(moneyF + '\n');
            out.flush();
            moneyF = brinp.readLine();

            double toDeposit = Double.parseDouble(moneyF);

            for (int i = 0; i < users.size(); i++) {

              if (users.get(i).AccNum == AccNumb) {
                double tmp = users.get(i).Balance;
                tmp = tmp + toDeposit;
                users.get(i).Balance = tmp;
                break;
              }
            }


          } else if ("4".equals(tast)) {

            String przelew = "Ile pieniędzy przelać?";
            out.writeBytes(przelew + "\n\r");
            out.flush();
            przelew = brinp.readLine();
            String komu = "Ile pieniędzy przelać (nr.konta) ?";
            out.writeBytes(komu + "\n\r");
            out.flush();
            komu = brinp.readLine();

            int To = Integer.parseInt(komu);
            double transfer = Double.parseDouble(przelew);

            for (int i = 0; i < users.size(); i++) {

              if (users.get(i).AccNum == AccNumb) {
                double tmp = users.get(i).Balance;
                tmp = tmp - transfer;
                users.get(i).Balance = tmp;
              } else if (users.get(i).AccNum == To) {
                double tmp = users.get(i).Balance;
                tmp = tmp + transfer;
                users.get(i).Balance = tmp;
              }


            }

          }

          FileOutputStream stream = new FileOutputStream("users.txt", false);
          PrintWriter save = new PrintWriter(stream);

          for (int i = 0; i < users.size(); i++) {
            save.print(users.get(i).Name + " ");
            save.print(users.get(i).Surname + " ");
            save.print(users.get(i).PESEL + " ");
            save.print(users.get(i).AccNum + " ");
            save.println(users.get(i).Balance + " ");
          }
          save.close();


          try {
            System.out.println("Odczytano linię: " + tast);
            if (tast == null || "quit".equals(tast)) {
              socket.close();
              System.out.println("Zakończenie pracy z klientem...");
              break;
            }
          } catch (IOException e) {
            System.out.println("Błąd wejścia-wyjścia: " + e);
            return;

          }
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}