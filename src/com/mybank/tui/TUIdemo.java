package com.mybank.tui;

import com.mybank.data.DataSource;
import com.mybank.domain.*;
import jexer.TAction;
import jexer.TApplication;
import jexer.TField;
import jexer.TText;
import jexer.TWindow;
import jexer.event.TMenuEvent;
import jexer.menu.TMenu;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Alexander 'Taurus' Babich
 */
public class TUIdemo extends TApplication {

    private static final int ABOUT_APP = 2000;
    private static final int CUST_INFO = 2010;
    private static final ThreadLocalRandom RAND = ThreadLocalRandom.current();

    public static void main(String[] args) throws Exception {
        TUIdemo tdemo = new TUIdemo();
        (new Thread(tdemo)).start();
        addCustomers();
    }


    public TUIdemo() throws Exception {
        super(BackendType.SWING);

        addToolMenu();
        //custom 'File' menu
        TMenu fileMenu = addMenu("&File");
        fileMenu.addItem(CUST_INFO, "&Customer Info");
        fileMenu.addDefaultItem(TMenu.MID_SHELL);
        fileMenu.addSeparator();
        fileMenu.addDefaultItem(TMenu.MID_EXIT);
        //end of 'File' menu

        addWindowMenu();

        //custom 'Help' menu
        TMenu helpMenu = addMenu("&Help");
        helpMenu.addItem(ABOUT_APP, "&About...");
        //end of 'Help' menu

        setFocusFollowsMouse(true);
        //Customer window
        ShowCustomerDetails();
    }

    @Override
    protected boolean onMenu(TMenuEvent menu) {
        if (menu.getId() == ABOUT_APP) {
            messageBox("About", "\t\t\t\t\t   Just a simple Jexer demo.\n\nCopyright \u00A9 2019 Alexander \'Taurus\' Babich").show();
            return true;
        }
        if (menu.getId() == CUST_INFO) {
            ShowCustomerDetails();
            return true;
        }
        return super.onMenu(menu);
    }

    private void ShowCustomerDetails() {
        TWindow custWin = addWindow("Customer Window", 2, 1, 40, 13, TWindow.NOZOOMBOX);
        custWin.newStatusBar("Enter valid customer number and press Show...");

        custWin.addLabel("Enter customer number: ", 2, 2);
        TField custNo = custWin.addField(24, 2, 3, false);
        TText details = custWin.addText("Owner Name: \nAccount Type: \nAccount Balance: ", 2, 4, 38, 30);
        custWin.addButton("&Show", 28, 2, new TAction() {

            // ============== показує клієнта банку по ID =======================
            @Override
            public void DO() {
                try {
                    // details about customer with index==custNum
                    int custNum = Integer.parseInt(custNo.getText());
                    details.setText(getCustomerInfo(custNum));

                } catch (Exception e) {
                    messageBox("Error", "You must provide a valid customer number!").show();
                }
            }
        });
    }
    private String getCustomerInfo(int index){
        Customer customer = Bank.getCustomer(index);
        Account account = customer.getAccount(0);

        // вивід інформації про клієнта
        return new StringBuilder()
                .append("First name: " + customer.getFirstName())
                .append("\nLast name: " + customer.getLastName())
                .append("\nID: " + index)
                // перевіряємо чи є рахунок відповідає типу SavingsAcount, якщо ні то значить це Checking рахунок
                .append("\nAccount Type: " + (account instanceof SavingsAccount ? "Saving" : "Checking"))
                .append("\nAccount Balance: " + account.getBalance())
                .toString();
    }

    private static void addCustomers(){
        // додамо декілька клієнтів (покупців?)
        Bank.addCustomer("Michael", "Townley");
        Bank.addCustomer("Trevor", "Philips");
        Bank.addCustomer("Franklin", "Clinton");

        for (int i = 0; i < Bank.getNumberOfCustomers(); i++) {
            Customer customer = Bank.getCustomer(i);

            // додамо рахунки для вище згаданих клієнтів, оберемо випадковий баланс і тип
            int balance = i*i+i;
            if (i%2 == 0){
                customer.addAccount(new CheckingAccount(balance));
            }
            else{
                customer.addAccount(new SavingsAccount(balance, i*i/2));
            }
        }

        // читання файлу
        try {
            DataSource dataSrc = new DataSource("C:\\Users\\User\\Desktop\\OOP-JAVA\\src\\com\\mybank\\data\\test.dat");
            dataSrc.loadData();
        } catch (IOException e) {
            System.out.println("File not found");
        }
    }
}