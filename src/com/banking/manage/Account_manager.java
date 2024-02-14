package com.banking.manage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class Account_manager {

    private final Connection connection;
    private final Scanner scanner;

    public Account_manager(Connection connection, Scanner scanner) {              //constructor for Account_manager class to initialize connection and scanner
        this.connection = connection;
        this.scanner = scanner;
    }
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public void debit(String email, BigDecimal debitAmount) {
        //check user pin
        System.out.println("Enter your password: ");
        String password = null;
        try {
            password = br.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String login_query = "SELECT * FROM account WHERE email = ? AND security_pin = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(login_query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                //check balance
                String check_balance_query = "SELECT balance FROM account WHERE email = ?";
                PreparedStatement preparedStatement1 = connection.prepareStatement(check_balance_query);
                preparedStatement1.setString(1, email);
                var resultSet1 = preparedStatement1.executeQuery();
                if (resultSet1.next()) {
                    BigDecimal balance = resultSet1.getBigDecimal("balance");
                    if (balance.compareTo(debitAmount) >= 0) {
                        //debit
                        String debit_query = "UPDATE account SET balance = balance - ? WHERE email = ?";
                        PreparedStatement preparedStatement2 = connection.prepareStatement(debit_query);
                        preparedStatement2.setBigDecimal(1, debitAmount);
                        preparedStatement2.setString(2, email);
                        preparedStatement2.executeUpdate();
                        System.out.println("Amount debited successfully");
                    } else {
                        System.out.println("Insufficient Balance");
                    }
                }
            } else {
                System.out.println("Invalid Password");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void credit(String email, BigDecimal creditAmount) {
        //check user password
          System.out.println("Enter your password: ");
        String password = null;
        try {
            password = br.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String login_query = "SELECT * FROM account WHERE email = ? AND security_pin = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(login_query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                //credit
                String credit_query = "UPDATE Account SET balance = balance + ? WHERE email = ?";
                PreparedStatement preparedStatement2 = connection.prepareStatement(credit_query);
                preparedStatement2.setBigDecimal(1, creditAmount);
                preparedStatement2.setString(2, email);
                preparedStatement2.executeUpdate();
                System.out.println("Amount credited successfully");
            } else {
                System.out.println("Invalid Password");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void transfer(String email, long accountNumber, BigDecimal transferAmount) {
        //check user password
        System.out.println("Enter your password: ");
        String password = null;
        try {
            password = br.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String login_query = "SELECT * FROM account WHERE email = ? AND security_pin = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(login_query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                //check balance
                String check_balance_query = "SELECT balance FROM Account WHERE email = ?";
                PreparedStatement preparedStatement1 = connection.prepareStatement(check_balance_query);
                preparedStatement1.setString(1, email);
                var resultSet1 = preparedStatement1.executeQuery();
                if (resultSet1.next()) {
                    BigDecimal balance = resultSet1.getBigDecimal("balance");
                    if (balance.compareTo(transferAmount) >= 0) {
                        //debit
                        String debit_query = "UPDATE Account SET balance = balance - ? WHERE email = ?";
                        PreparedStatement preparedStatement2 = connection.prepareStatement(debit_query);
                        preparedStatement2.setBigDecimal(1, transferAmount);
                        preparedStatement2.setString(2, email);
                        preparedStatement2.executeUpdate();
                        System.out.println("Amount debited successfully");

                        //credit
                        String credit_query = "UPDATE Account SET balance = balance + ? WHERE account_no = ?";
                        PreparedStatement preparedStatement3 = connection.prepareStatement(credit_query);
                        preparedStatement3.setBigDecimal(1, transferAmount);
                        preparedStatement3.setLong(2, accountNumber);
                        preparedStatement3.executeUpdate();
                        System.out.println("Amount credited successfully");
                    } else {
                        System.out.println("Insufficient Balance");
                    }
                }
            } else {
                System.out.println("Invalid Password");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void checkBalance(String email) {
        //check user password
        System.out.println("Enter your password: ");
        String password = null;
        try {
            password = br.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String login_query = "SELECT * FROM account WHERE email = ? AND security_pin = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(login_query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                //check balance
                String check_balance_query = "SELECT balance FROM Account WHERE email = ?";
                PreparedStatement preparedStatement1 = connection.prepareStatement(check_balance_query);
                preparedStatement1.setString(1, email);
                var resultSet1 = preparedStatement1.executeQuery();
                if (resultSet1.next()) {
                    BigDecimal balance = resultSet1.getBigDecimal("balance");
                    System.out.println("Your balance is: " + balance);
                }
            } else {
                System.out.println("Invalid Password");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void resetPin(String email, String newPin) {
        //check user password
        System.out.println("Enter your password: ");
        String password = null;
        try {
            password = br.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String login_query = "SELECT * FROM account WHERE email = ? AND security_pin = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(login_query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                //reset pin
                String reset_pin_query = "UPDATE Account SET security_pin = ? WHERE email = ?";
                PreparedStatement preparedStatement2 = connection.prepareStatement(reset_pin_query);
                preparedStatement2.setString(1, newPin);
                preparedStatement2.setString(2, email);
                preparedStatement2.executeUpdate();
                System.out.println("Pin reset successfully");
            } else {
                System.out.println("Invalid Password");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
