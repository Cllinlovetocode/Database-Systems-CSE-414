
package scheduler;

import scheduler.db.ConnectionManager;
import scheduler.model.Caregiver;
import scheduler.model.Patient;
import scheduler.model.Vaccine;
import scheduler.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class Scheduler {

    // objects to keep track of the currently logged-in user
    // Note: it is always true that at most one of currentCaregiver and currentPatient is not null
    //       since only one user can be logged-in at a time
    private static Caregiver currentCaregiver = null;
    private static Patient currentPatient = null;

    public static void main(String[] args) {
        // printing greetings text
        System.out.println();
        System.out.println("Welcome to the COVID-19 Vaccine Reservation Scheduling Application!");
        System.out.println("*** Please enter one of the following commands ***");
        System.out.println("> create_patient <username> <password>");  //TODO: implement create_patient (Part 1)
        System.out.println("> create_caregiver <username> <password>");
        System.out.println("> login_patient <username> <password>");  // TODO: implement login_patient (Part 1)
        System.out.println("> login_caregiver <username> <password>");
        System.out.println("> search_caregiver_schedule <date>");  // TODO: implement search_caregiver_schedule (Part 2)
        System.out.println("> reserve <date> <vaccine>");  // TODO: implement reserve (Part 2)
        System.out.println("> upload_availability <date>");
        System.out.println("> SeeAvailabilities");
        System.out.println("> cancel <appointment_id>");  // TODO: implement cancel (extra credit)
        System.out.println("> add_doses <vaccine> <number>");
        System.out.println("> show_appointments");  // TODO: implement show_appointments (Part 2)
        System.out.println("> logout");  // TODO: implement logout (Part 2)
        System.out.println("> quit");
        System.out.println();

        // read input from user
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("> ");
            String response = "";
            try {
                response = r.readLine();
            } catch (IOException e) {
                System.out.println("Please try again!");
            }
            // split the user input by spaces
            String[] tokens = response.split(" ");
            // check if input exists
            if (tokens.length == 0) {
                System.out.println("Please try again!");
                continue;
            }
            // determine which operation to perform
            String operation = tokens[0];
            if (operation.equals("create_patient")) {
                createPatient(tokens);
            } else if (operation.equals("create_caregiver")) {
                createCaregiver(tokens);
            } else if (operation.equals("login_patient")) {
                loginPatient(tokens);
            } else if (operation.equals("login_caregiver")) {
                loginCaregiver(tokens);
            } else if (operation.equals("search_caregiver_schedule")) {
                searchCaregiverSchedule(tokens);
            } else if (operation.equals("reserve")) {
                reserve(tokens);
            } else if (operation.equals("upload_availability")) {
                uploadAvailability(tokens);
            } else if (operation.equals("SeeAvailabilities")) {
                SeeAvailabilities(tokens);
            } else if (operation.equals("cancel")) {
                cancel(tokens);
            } else if (operation.equals("add_doses")) {
                addDoses(tokens);
            } else if (operation.equals("show_appointments")) {
                showAppointments(tokens);
            } else if (operation.equals("logout")) {
                logout(tokens);
            } else if (operation.equals("quit")) {
                System.out.println("Bye!");
                return;
            } else {
                System.out.println("Invalid operation name!");
            }
        }
    }

    private static void createPatient(String[] tokens) {
        // TODO: Part 1
        // create_patient <username> <password>
        // check 1: the length for tokens need to be exactly 3 to include all information (with the operation name)
        if (tokens.length != 3) {
            System.out.println("Failed to create user.");
            return;
        }
        String username = tokens[1];
        String password = tokens[2];
        // check 2: check if the username has been taken already
        if (usernameExistsPatient(username)) {
            System.out.println("Username taken, try again!");
            return;
        }
        byte[] salt = Util.generateSalt();
        byte[] hash = Util.generateHash(password, salt);
        // create the caregiver
        try {
            Patient patient = new Patient.PatientBuilder(username, salt, hash).build();
            // save to caregiver information to our database
            patient.saveToDB();
            System.out.println("Created user " + username);
        } catch (SQLException e) {
            System.out.println("Failed to create user.");
            e.printStackTrace();
        }
    }

    private static boolean usernameExistsPatient(String username) {
        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();

        String selectUsername = "SELECT * FROM Patients WHERE PatientUsername = ?";
        try {
            PreparedStatement statement = con.prepareStatement(selectUsername);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            // returns false if the cursor is not before the first record or if there are no rows in the ResultSet.
            return resultSet.isBeforeFirst();
        } catch (SQLException e) {
            System.out.println("Error occurred when checking username");
            e.printStackTrace();
        } finally {
            cm.closeConnection();
        }
        return true;
    }

    private static void createCaregiver(String[] tokens) {
        // create_caregiver <username> <password>
        // check 1: the length for tokens need to be exactly 3 to include all information (with the operation name)
        if (tokens.length != 3) {
            System.out.println("Failed to create user.");
            return;
        }
        String username = tokens[1];
        String password = tokens[2];
        // check 2: check if the username has been taken already
        if (usernameExistsCaregiver(username)) {
            System.out.println("Username taken, try again!");
            return;
        }
        byte[] salt = Util.generateSalt();
        byte[] hash = Util.generateHash(password, salt);
        // create the caregiver
        try {
            Caregiver caregiver = new Caregiver.CaregiverBuilder(username, salt, hash).build();
            // save to caregiver information to our database
            caregiver.saveToDB();
            System.out.println("Created user " + username);
        } catch (SQLException e) {
            System.out.println("Failed to create user.");
            e.printStackTrace();
        }
    }

    private static boolean usernameExistsCaregiver(String username) {
        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();

        String selectUsername = "SELECT * FROM Caregivers WHERE Username = ?";
        try {
            PreparedStatement statement = con.prepareStatement(selectUsername);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            // returns false if the cursor is not before the first record or if there are no rows in the ResultSet.
            return resultSet.isBeforeFirst();
        } catch (SQLException e) {
            System.out.println("Error occurred when checking username");
            e.printStackTrace();
        } finally {
            cm.closeConnection();
        }
        return true;
    }

    private static void loginPatient(String[] tokens) {
        // TODO: Part 1
        // login_patient <username> <password>
        // check 1: if someone's already logged-in, they need to log out first
        if (currentPatient != null || currentCaregiver != null) {
            System.out.println("User already logged in.");
            return;
        }
        // check 2: the length for tokens need to be exactly 3 to include all information (with the operation name)
        if (tokens.length != 3) {
            System.out.println("Login failed.");
            return;
        }
        String username = tokens[1];
        String password = tokens[2];

        Patient patient = null;
        try {
            patient = new Patient.PatientGetter(username, password).get();
        } catch (SQLException e) {
            System.out.println("Login failed.");
            e.printStackTrace();
        }
        // check if the login was successful
        if (patient == null) {
            System.out.println("Login failed.");
        } else {
            System.out.println("Logged in as: " + username);
            currentPatient = patient;
        }
    }

    private static void loginCaregiver(String[] tokens) {
        // login_caregiver <username> <password>
        // check 1: if someone's already logged-in, they need to log out first
        if (currentCaregiver != null || currentPatient != null) {
            System.out.println("User already logged in.");
            return;
        }
        // check 2: the length for tokens need to be exactly 3 to include all information (with the operation name)
        if (tokens.length != 3) {
            System.out.println("Login failed.");
            return;
        }
        String username = tokens[1];
        String password = tokens[2];

        Caregiver caregiver = null;
        try {
            caregiver = new Caregiver.CaregiverGetter(username, password).get();
        } catch (SQLException e) {
            System.out.println("Login failed.");
            e.printStackTrace();
        }
        // check if the login was successful
        if (caregiver == null) {
            System.out.println("Login failed.");
        } else {
            System.out.println("Logged in as: " + username);
            currentCaregiver = caregiver;
        }
    }

    // try to check the availabilite's date
    private static void SeeAvailabilities(String[] tokens) {
        ConnectionManager cm = new ConnectionManager();

        // Query to select all records from Availabilities table
        String selectAvailabilities = "SELECT * FROM Availabilities;";

        // Use try-with-resources for efficient management of the database connection and PreparedStatement
        try (Connection con = cm.createConnection();
             PreparedStatement statement = con.prepareStatement(selectAvailabilities);
             ResultSet resultSet = statement.executeQuery()) {

            // Check if the ResultSet contains any rows
            if (!resultSet.isBeforeFirst()) {
                System.out.println("No availabilities found.");
                return;
            }

            // Print column headers (optional, can be removed if not needed)
            System.out.println("Time\t\tUsername");
            System.out.println("----------------------------------------");

            // Iterate over each row in the ResultSet
            while (resultSet.next()) {
                // Retrieve and print each column's value from the current row
                Date time = resultSet.getDate("Time");
                String username = resultSet.getString("Username");

                // Print the retrieved values
                System.out.println(time + "\t" + username);
            }
        } catch (SQLException e) {
            System.out.println("Error occurred when retrieving availabilities");
            e.printStackTrace(); // For debugging, consider logging this in production code
        }
        // The try-with-resources statement takes care of closing the connection, statement, and resultSet
    }

    private static void searchCaregiverSchedule(String[] tokens) {
        // TODO: Part 2
        //check if caregiver or patient has logged in
        if (currentCaregiver == null && currentPatient == null) {
            System.out.println("Please login in first!");
            return;
        }
        if (tokens.length != 2) {
            System.out.println("Please try again!");
            return;
        }
        String DATE = tokens[1];
        ConnectionManager cm = new ConnectionManager();
        // Use try-with-resources for efficient management of database connection
        try (Connection con = cm.createConnection()) {
            // SQL query to find available caregivers and their vaccine information on a given date
            String selectAvailableCaregivers = "SELECT C.Username, V.Name, V.Doses "
                    + "FROM Availabilities AS A,Caregivers AS C,Vaccines AS V "
                    + "WHERE A.Username = C.Username AND A.Time = ? "
                    + "ORDER BY C.Username";
            // Use try-with-resources for preparing the SQL statement
            try (PreparedStatement statement = con.prepareStatement(selectAvailableCaregivers)) {
                statement.setString(1, DATE);// Set the date parameter in the SQL query
                try (ResultSet resultSet = statement.executeQuery()) {
                    //check if the resultSet is empty
                    if (!resultSet.isBeforeFirst()) {
                        System.out.println("Sorry,no available caregivers");
                    } else {
                        // Iterate over each row in the result set
                        while (resultSet.next()) {
                            // Retrieve and store each column's value from the current row
                            String username = resultSet.getString("Username");
                            String vaccine = resultSet.getString("Name");
                            int doses = resultSet.getInt("Doses");

                            // Print the caregiver's username, vaccine, and doses, separated by spaces
                            System.out.println(username + " " + vaccine + " " + doses);
                        }
                    }

                }
            }
        } catch (SQLException e) {
            System.out.println("Please try again!");
            e.printStackTrace(); // Print the stack trace for debugging purposes
        }


    }


    private static void reserve(String[] tokens) {
        // TODO: Part 2

        // Check if a caregiver or patient is currently logged in
        if (currentPatient == null ) {
            System.out.println("Please login first.");
            return;
        }
        if (currentCaregiver != null ) {
            System.out.println("Please login as a patient.");
            return;
        }

        if (tokens.length != 3) {
            System.out.println("Please try again!");
            return;
        }
        String date = tokens[1];
        String vaccineName = tokens[2];
        ConnectionManager cm = new ConnectionManager();

        String assignedCaregiverUsername = null;
        int availableDoses = 0;
        // Use try-with-resources for efficient management of database connection
        try (Connection con1 = cm.createConnection()) {
            // SQL query to find available caregivers and their vaccine information on a given date
            String selectAvailableCaregivers = "SELECT C.Username "
                    + "FROM Availabilities AS A, Caregivers AS C "
                    + "WHERE A.Username = C.Username AND A.Time = ? "
                    + "ORDER BY C.Username ASC";
            // Use try-with-resources for preparing the SQL statement
            try (PreparedStatement statement = con1.prepareStatement(selectAvailableCaregivers)) {
                statement.setString(1, date);// Set the date parameter in the SQL query

                try (ResultSet resultSet = statement.executeQuery()) {
                    //check if the resultSet is empty
                    if (!resultSet.isBeforeFirst()) {
                        System.out.println("Sorry,no available caregivers");
                        return;
                    } else {
                        // Choose the first caregiver alphabetically
                        resultSet.next(); // Move the cursor to the first row
                        assignedCaregiverUsername = resultSet.getString("Username");

                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Please try again!");
            e.printStackTrace(); // Print the stack trace for debugging purposes
            return;
        }
        try (Connection con2 = cm.createConnection()) {
            // Use con2 for one database operation
            // SQL query to find available vaccines
            String checkDoses = "SELECT SUM(V.Doses) "
                    + "FROM Vaccines AS V "
                    + "WHERE V.Name = ? ";
            // Use try-with-resources for preparing the SQL statement
            try (PreparedStatement statement = con2.prepareStatement(checkDoses)) {
                statement.setString(1, vaccineName);// Set the date parameter in the SQL query

                try (ResultSet resultSet = statement.executeQuery()) {
                    //check if the resultSet is empty
                    if (resultSet.next()) {
                        availableDoses = resultSet.getInt(1);
                        if (availableDoses <= 0) {
                            System.out.println("Not enough available doses!");
                            return;
                        }

                    }
                }

            }

        } catch (SQLException e) {
            System.out.println("Please try again!");
            e.printStackTrace(); // Print the stack trace for debugging purposes
            return;
        }
        //Successfully reserved
        String patientName =currentPatient.getUsername();
        long appointmentId = uploadAppointment(new String[]{patientName + " " + assignedCaregiverUsername + " " + date + " " + vaccineName});
        System.out.println("Successfully reserved!");
        System.out.println("Appointment ID:{" + appointmentId + "}"+" Caregiver username:{" + assignedCaregiverUsername+"}");

        // Delete the caregiver's availability record and vaccine doses

        ConnectionManager cm2 = new ConnectionManager();
        try (Connection con3 = cm2.createConnection()) {

            String deleteCaregiverAvailability = "DELETE FROM Availabilities WHERE Username = ? AND Time = ?";
            try (PreparedStatement deleteStatement = con3.prepareStatement(deleteCaregiverAvailability)) {
                deleteStatement.setString(1, assignedCaregiverUsername);
                deleteStatement.setString(2, date);
                deleteStatement.executeUpdate();
            }
            // Commit the first part of the transaction (assigning caregiver and deleting availability)
            con3.commit();
        } catch (SQLException e) {
            System.out.println("Please try again!");
            e.printStackTrace(); // Print the stack trace for debugging purposes
            return;
        }
        // Assuming one dose is used for the appointment
        availableDoses--;
        // Update the vaccine table with the new available doses
        try (Connection con4 = cm2.createConnection()) {
            String updateVaccineDoses = "UPDATE Vaccines SET Doses = ? WHERE Name = ?";
            try (PreparedStatement updateStatement = con4.prepareStatement(updateVaccineDoses)) {
                updateStatement.setInt(1, availableDoses);
                updateStatement.setString(2, vaccineName);
                updateStatement.executeUpdate();
            }
            // Commit the second part of the transaction (updating vaccine doses)
            con4.commit();
        } catch (SQLException e) {
            System.out.println("Please try again!");
            e.printStackTrace(); // Print the stack trace for debugging purposes
            return;
        }
    }

        private static long uploadAppointment(String[] tokens) {

         String[] parts =tokens[0].split(" ");


            long appointmentId = 0;
            String patient = parts[0];
            String caregiver = parts[1];
            String date = parts[2];
            String vaccine = parts[3];
            ConnectionManager cm = new ConnectionManager();
            Connection con = cm.createConnection();
            try {
                    // SQL query to get the maximum appointment ID from the Appointment table
                    String getMaxAppointmentId = "SELECT MAX(AppointmentID) FROM Appointment";

                    PreparedStatement statement = con.prepareStatement(getMaxAppointmentId);
                    ResultSet resultSet = statement.executeQuery();

                    // Check if there are existing appointments
                    if (resultSet.next()) {
                        long maxAppointmentId = resultSet.getLong(1);

                        // Increment the maximum appointment ID to generate a new one
                        appointmentId = maxAppointmentId + 1;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();

                } finally {
                    cm.closeConnection();
                }
            // After obtaining the new appointment ID, you can insert it into the Appointment table
            if (appointmentId > 0) {
                try (Connection con2 = cm.createConnection()) {
                    String updateAppointment = "INSERT INTO Appointment (AppointmentID, PatientUsername, Username, date, Name) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement statement = con2.prepareStatement(updateAppointment);
                    statement.setLong(1, appointmentId);
                    statement.setString(2,patient);
                    statement.setString(3,caregiver);
                    statement.setDate(4, Date.valueOf(date));
                    statement.setString(5,vaccine);

                    int rowAffected = statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    appointmentId=0;
                }
            }

                return appointmentId;
            }







    private static void uploadAvailability(String[] tokens) {
        // upload_availability <date>
        // check 1: check if the current logged-in user is a caregiver
        if (currentCaregiver == null) {
            System.out.println("Please login as a caregiver first!");
            return;
        }
        // check 2: the length for tokens need to be exactly 2 to include all information (with the operation name)
        if (tokens.length != 2) {
            System.out.println("Please try again!");
            return;
        }
        String date = tokens[1];
        try {
            Date d = Date.valueOf(date);
            currentCaregiver.uploadAvailability(d);
            System.out.println("Availability uploaded!");
        } catch (IllegalArgumentException e) {
            System.out.println("Please enter a valid date!");
        } catch (SQLException e) {
            System.out.println("Error occurred when uploading availability");
            e.printStackTrace();
        }
    }

    private static void cancel(String[] tokens) {
        // TODO: Extra credit
        long appointmentId = Long.parseLong(tokens[1]);

        ConnectionManager cm = new ConnectionManager();
        try (Connection con = cm.createConnection()) {
            // Check if the appointment exists
            String checkAppointmentQuery = "SELECT Username, date FROM Appointment WHERE AppointmentID = ? ";

            try (PreparedStatement checkStatement = con.prepareStatement(checkAppointmentQuery)) {
                checkStatement.setLong(1, appointmentId);

                ResultSet resultSet = checkStatement.executeQuery();
                if (resultSet.next()) {
                    String caregiverUsername = resultSet.getString("Username");
                    Date appointmentDate = resultSet.getDate("date");

                    // Cancel the appointment
                    String cancelAppointmentQuery ="DELETE FROM Appointment WHERE AppointmentID = ?";
                    try (PreparedStatement cancelStatement = con.prepareStatement(cancelAppointmentQuery)) {
                        cancelStatement.setLong(1, appointmentId);

                        int rowsAffected = cancelStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Appointment successfully canceled.");

                            // Update caregiver availability
                            updateAvailability(caregiverUsername, appointmentDate);

                        } else {
                            System.out.println("Appointment cancellation failed.");
                        }
                    }
                } else {
                    System.out.println("Appointment with the specified ID does not exist or is already canceled.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cm.closeConnection();
        }


    }

    private static void updateAvailability(String s, Date appointmentDate) {
      // Update the caregiver's availability. Adjust this according to your database schema
        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();
        String updateAvailabilityQuery = "INSERT INTO Availabilities (Username,Time) VALUES (?, ?)";
        try (PreparedStatement updateStatement = con.prepareStatement(updateAvailabilityQuery)) {
            updateStatement.setString(1, s);
            updateStatement.setDate(2, appointmentDate);

            updateStatement.executeUpdate();
            System.out.println("Caregiver availability has updated.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    private static void addDoses(String[] tokens) {
        // add_doses <vaccine> <number>
        // check 1: check if the current logged-in user is a caregiver
        if (currentCaregiver == null) {
            System.out.println("Please login as a caregiver first!");
            return;
        }
        // check 2: the length for tokens need to be exactly 3 to include all information (with the operation name)
        if (tokens.length != 3) {
            System.out.println("Please try again!");
            return;
        }
        String vaccineName = tokens[1];
        int doses = Integer.parseInt(tokens[2]);
        Vaccine vaccine = null;
        try {
            vaccine = new Vaccine.VaccineGetter(vaccineName).get();
        } catch (SQLException e) {
            System.out.println("Error occurred when adding doses");
            e.printStackTrace();
        }
        // check 3: if getter returns null, it means that we need to create the vaccine and insert it into the Vaccines
        //          table
        if (vaccine == null) {
            try {
                vaccine = new Vaccine.VaccineBuilder(vaccineName, doses).build();
                vaccine.saveToDB();
            } catch (SQLException e) {
                System.out.println("Error occurred when adding doses");
                e.printStackTrace();
            }
        } else {
            // if the vaccine is not null, meaning that the vaccine already exists in our table
            try {
                vaccine.increaseAvailableDoses(doses);
            } catch (SQLException e) {
                System.out.println("Error occurred when adding doses");
                e.printStackTrace();
            }
        }
        System.out.println("Doses updated!");
    }

    private static void showAppointments(String[] tokens) {
        // TODO: Part 2

        try {

            // Check if a caregiver or patient is currently logged in
            if (currentCaregiver != null) {



                ConnectionManager cm = new ConnectionManager();
                Connection con = cm.createConnection();
                try {
                    //SQL query to get the appointments detail for the specific caregiver
                    String getAppointmentDetail = "SELECT A.AppointmentID, A.Name, A.date, A.PatientUsername FROM Appointment AS A WHERE A.Username= ? ORDER BY A.AppointmentID";
                    PreparedStatement statement = con.prepareStatement(getAppointmentDetail);
                    statement.setString(1, currentCaregiver.getUsername());

                    ResultSet resultSet = statement.executeQuery();
                    boolean hasAppointments = false;
                    //Iterate over each row in the ResultSet
                    while (resultSet.next()) {
                        hasAppointments = true;
                        //Retrieve and print each column's value from the current row
                        Long AppointmentID = resultSet.getLong("AppointmentID");
                        String vaccine = resultSet.getString("Name");
                        Date date = resultSet.getDate("date");
                        String patient = resultSet.getString("PatientUsername");

                        //print the retrieved values

                        System.out.println(AppointmentID + " " + vaccine + " " + date + " " + patient);


                    }
                    if(!hasAppointments) {
                        System.out.println("You don't have any appointment! ");
                    }


                } catch (Exception e) {
                    //
                    System.out.println("Please try again!");

               } finally {
                    cm.closeConnection();
                }

            } else if (currentPatient != null) {

                ConnectionManager cm = new ConnectionManager();
                Connection con = cm.createConnection();
                try{
                    //SQL query to get the appointments detail for the specific caregiver
                    String getAppointmentDetail= "SELECT A.AppointmentID, A.Name, A.date, A.Username FROM Appointment AS A WHERE A.PatientUsername = ? ORDER BY A.AppointmentID";
                    PreparedStatement statement=con.prepareStatement(getAppointmentDetail);
                    statement.setString(1, currentPatient.getUsername());
                    ResultSet resultSet = statement.executeQuery();
                    boolean hasAppointments = false;
                    //Iterate over each row in the ResultSet
                    while(resultSet.next()){
                        hasAppointments = true;
                        //Retrieve and print each column's value from the current row
                        Long AppointmentID = resultSet.getLong("AppointmentID");
                        String vaccine = resultSet.getString("Name");
                        Date date = resultSet.getDate("date");
                        String caregiver = resultSet.getString("Username");

                        //print the retrieved values

                        System.out.println(AppointmentID+" "+vaccine+" "+date+" "+caregiver);

                    }
                    if (!hasAppointments) {
                        System.out.println("You don't have any appointment! ");
                    }

                } catch (Exception e) {
                    //
                    System.out.println("Please try again!");

                } finally{
                    cm.closeConnection();
                }




            } else {

                // If neither a caregiver nor a patient is logged in
                System.out.println("Please login first.");
            }
        } catch (Exception e) {
            //
            System.out.println("Please try again!");
        }


    }

    private static void logout(String[] tokens) {
        try {

            // TODO: Part 2
            // Check if a caregiver or patient is currently logged in
            if (currentCaregiver != null || currentPatient != null) {

                currentCaregiver = null;
                currentPatient = null;
                System.out.println("Successfully logged out!.");
                return;
            } else {

                // If neither a caregiver nor a patient is logged in
                System.out.println("Please login first.");
            }
        } catch (Exception e) {
            //
            System.out.println("Please try again!");
        }
    }
}

