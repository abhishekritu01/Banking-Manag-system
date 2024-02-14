import com.banking.manage.Account_manager;
import com.banking.manage.Database_Connection;
import com.banking.manage.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

        // check database connection
        final Connection ignored = Database_Connection.getConnection();
        System.out.println("Database connected successfully");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String email;

        while (true) {
            System.out.println("***============ WELCOME TO BANKING SYSTEM =========================***");
            System.out.println();
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            try {
                String choiceInput = br.readLine().trim();

                if (choiceInput.isEmpty()) {
                    System.out.println("Please enter a valid choice.");
                    continue;  // Restart the loop
                }

                int choice = Integer.parseInt(choiceInput);

                switch (choice) {
                    case 1:
                        System.out.println("Register by entering your details");
                        User user = new User(ignored, new Scanner(System.in));        //system.in is the input stream which is used to read data from the console
                        user.Register();
                        break;

                    case 2:
                        System.out.println("Login by entering your details");
                        User user1 = new User(ignored, new Scanner(System.in));
                        email = user1.Login();

                        if (email != null) {
                            System.out.println("===========Login Successful===============");
                            System.out.println("Welcome to  " + email );

                            // Check if the user has an account
                            if (isAccountPresent(ignored, email)) {           //ignored is the connection object which is used to connect to the database
                                while (true) {
                                    System.out.println();
                                    System.out.println("1. Debit ");
                                    System.out.println("2. Credit ");
                                    System.out.println("3. Transfer Money");
                                    System.out.println("4. Check Balance");
                                    System.out.println("5. Log Out");
                                    System.out.println("6. Reset PIN");
                                    System.out.println("Enter your choice: ");
                                    int choice2 = Integer.parseInt(br.readLine());


                                    Account_manager accountManager = new Account_manager(ignored, new Scanner(System.in));

                                    switch (choice2) {
                                        case 1:
                                            System.out.println("Enter the amount to be debited: ");
                                            BigDecimal debitAmount = new BigDecimal(br.readLine());
                                            accountManager.debit(email, debitAmount);
                                            break;

                                        case 2:
                                            System.out.println("Enter the amount to be credited: ");
                                            BigDecimal creditAmount = new BigDecimal(br.readLine());
                                            accountManager.credit(email, creditAmount);
                                            break;

                                        case 3:
                                            System.out.println("Enter the amount to be transferred: ");
                                            BigDecimal transferAmount = new BigDecimal(br.readLine());
                                            System.out.println("Enter the account number to which the amount is to be transferred: ");
                                            long accountNumber = Long.parseLong(br.readLine());
                                            accountManager.transfer(email, accountNumber, transferAmount);
                                            break;

                                        case 4:
                                            accountManager.checkBalance(email);
                                            break;

                                        case 6:
                                            System.out.println("Enter your new PIN: ");
                                            String newPin = br.readLine();
                                            accountManager.resetPin(email, newPin);
                                            break;

                                        case 5:
                                            System.out.println("Logging out...");
                                            System.exit(0);
                                            break;

                                        default:
                                            System.out.println("Enter Valid Choice!");
                                            break;
                                    }
                                }
                            } else {
                                System.out.println("No account found for the user.");
                                System.out.println("1. Open a New Account");
                                System.out.println("2. Exit");
                                System.out.println("Enter your choice: ");
                                int choice1 = Integer.parseInt(br.readLine());

                                switch (choice1) {
                                    case 1:
                                        System.out.println("Enter your details to open a new account");
                                        System.out.println("Enter your full name: ");
                                        String full_name = br.readLine();
                                        System.out.println("Enter Initial Deposit: ");
                                        BigDecimal initialDeposit = new BigDecimal(br.readLine());
                                        System.out.println("Enter your password: ");
                                        String password = br.readLine();

                                        // Generate a random 6-digit account number
                                        long accountNumber = generateRandomAccountNumber();

                                        System.out.println("Your new account number is: " + accountNumber);

                                        // Insert the generated account number into the database
                                        String openAccountQuery = "INSERT INTO account(account_no, fullname, balance, email, security_pin) VALUES (?, ?, ?, ?, ?)";
                                        try {
                                            PreparedStatement preparedStatement = ignored.prepareStatement(openAccountQuery);
                                            preparedStatement.setLong(1, accountNumber);
                                            preparedStatement.setString(2, full_name);
                                            preparedStatement.setBigDecimal(3, initialDeposit);
                                            preparedStatement.setString(4, email);
                                            preparedStatement.setInt(5, generateRandomSecurityPin());
                                            preparedStatement.executeUpdate();
                                            System.out.println("Account Opened Successfully");

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        break;

                                    case 2:
                                        System.out.println("Exiting...");
                                        System.exit(0);
                                        break;

                                    default:
                                        System.out.println("Enter Valid Choice!");
                                        break;
                                }
                            }
                        } else {
                            System.out.println("Invalid Credentials");
                        }
                        break;

                    case 3:
                        System.out.println("Exiting...");
                        System.exit(0);
                        break;

                    default:
                        System.out.println("Enter Valid Choice!");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isAccountPresent(Connection connection, String email) {
        String checkAccountQuery = "SELECT COUNT(*) FROM account WHERE email = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(checkAccountQuery);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            return count > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private static long generateRandomAccountNumber() {
        Random rand = new Random();
        int minAccountNumber = 100000;
        int maxAccountNumber = 999999;
        return rand.nextInt((maxAccountNumber - minAccountNumber) + 1) + minAccountNumber;
    }


    private static int generateRandomSecurityPin() {
        Random rand = new Random();
        int minSecurityPin = 1000;
        int maxSecurityPin = 9999;
        return rand.nextInt((maxSecurityPin - minSecurityPin) + 1) + minSecurityPin;
    }
}
