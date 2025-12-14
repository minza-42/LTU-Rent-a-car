import java.util.Scanner;
/**
 * LTU Rent-a-car Exam Application
 *
 * This Java program implements a short-term car rental system for use at Luleå University of Technology.
 * It allows adding cars to a fleet, renting and returning cars, and printing summaries of rentals and fleet status.
 *
 * @author Minai Karlsson (minkar4)
 *
 */
public class Main {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final int MAX_CARS = 100;
    private static final int MAX_RENTALS = 100;
    private static final int COST_PER_HOUR = 120;
    private static final int MINUTES_IN_HOUR = 60;
    private static final int MAX_HOUR = 23;
    private static final int MAX_MINUTE = 59;
    private static final double ROUNDING_SCALE = 100.0;
    private static final double ONE_DECIMAL = 10.0;

    /**
     * Main entry point of the application.
     *
     * @param args command-line arguments
     */
    public static void main(final String[] args) {
        final String[] regNumbers = new String[MAX_CARS];
        final String[] models = new String[MAX_CARS];
        final boolean[] isRented = new boolean[MAX_CARS];
        int carCount = 0;

        final String[] rentalNames = new String[MAX_RENTALS];
        final String[] rentalRegNumbers = new String[MAX_RENTALS];
        final String[] pickupTimes = new String[MAX_RENTALS];
        final String[] returnTimes = new String[MAX_RENTALS];
        final double[] rentalCosts = new double[MAX_RENTALS];
        int rentalCount = 0;

        while (true) {
            printMenu();
            final String option = SCANNER.nextLine().trim().toLowerCase();

            switch (option) {
                case "1":
                    carCount = addCar(regNumbers, models, isRented, carCount);
                    break;
                case "2":
                    rentalCount = rentCar(
                        regNumbers, models, isRented, carCount,
                        rentalNames, rentalRegNumbers, pickupTimes,
                        returnTimes, rentalCosts, rentalCount
                    );
                    break;
                case "3":
                    returnCar(
                        regNumbers, models, isRented, carCount,
                        rentalNames, rentalRegNumbers, pickupTimes,
                        returnTimes, rentalCosts, rentalCount
                    );
                    break;
                case "4":
                    printFleet(regNumbers, models, isRented, carCount);
                    break;
                case "5":
                    printSummary(rentalNames, rentalRegNumbers, pickupTimes, returnTimes, rentalCosts, rentalCount);
                    break;
                case "q":
                    return;
                default:
                    System.out.println("invalid menu item");
            }
        }
    }

    /**
     * Prints the menu options.
     */
    private static void printMenu() {
        System.out.println("# LTU Rent-a-car");
        System.out.println("1. Add car to fleet");
        System.out.println("2. Rent a car");
        System.out.println("3. Return a car");
        System.out.println("4. Print car fleet");
        System.out.println("5. Print rental summary");
        System.out.println("q. End program");
        System.out.print("> Enter your option: ");
    }

    /**
     * Adds a new car to the fleet.
     *
     * @param regNumbers car registration number array
     * @param models car models array
     * @param isRented rental status array
     * @param carCount current car count
     * @return updated car count
     */
    private static int addCar(final String[] regNumbers, final String[] models, final boolean[] isRented, final int carCount) {
        System.out.print("> Enter registration number: ");
        final String reg = SCANNER.nextLine().toUpperCase();
        if (!reg.matches("[A-Z]{3}[0-9]{3}")) {
            System.out.println("invalid registration number");
            return carCount;
        }
        for (int i = 0; i < carCount; i++) {
            if (regNumbers[i].equals(reg)) {
                System.out.println("number " + reg + " already exists");
                return carCount;
            }
        }
        System.out.print("> Enter make and model: ");
        final String model = SCANNER.nextLine();

        regNumbers[carCount] = reg;
        models[carCount] = model;
        isRented[carCount] = false;
        System.out.println(model + " with registration number " + reg + " was added to car fleet.");
        return carCount + 1;
    }

    /**
     * Registers a car rental.
     *
     * @param regNumbers registration numbers
     * @param models car models
     * @param isRented rental statuses
     * @param carCount number of cars
     * @param rentalNames renter names
     * @param rentalRegNumbers rental car numbers
     * @param pickupTimes pickup times
     * @param returnTimes return times
     * @param rentalCosts calculated costs
     * @param rentalCount current rental count
     * @return updated rental count
     */
    private static int rentCar(
        final String[] regNumbers, final String[] models, final boolean[] isRented, final int carCount,
        final String[] rentalNames, final String[] rentalRegNumbers, final String[] pickupTimes,
        final String[] returnTimes, final double[] rentalCosts, final int rentalCount
    ) {
        System.out.print("> Enter car's registration number: ");
        final String reg = SCANNER.nextLine().toUpperCase();
        if (!reg.matches("[A-Z]{3}[0-9]{3}")) {
            System.out.println("invalid registration number");
            return rentalCount;
        }

        int index = -1;
        for (int i = 0; i < carCount; i++) {
            if (regNumbers[i].equals(reg)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            System.out.println("car " + reg + " not found");
            return rentalCount;
        }
        if (isRented[index]) {
            System.out.println("car " + reg + " not available");
            return rentalCount;
        }

        System.out.print("> Enter time of pickup: ");
        final String pickup = SCANNER.nextLine();
        if (!isValidTime(pickup)) {
            System.out.println("invalid time format");
            return rentalCount;
        }

        System.out.print("> Enter renter's name: ");
        final String name = SCANNER.nextLine();

        rentalRegNumbers[rentalCount] = reg;
        rentalNames[rentalCount] = name;
        pickupTimes[rentalCount] = pickup;
        returnTimes[rentalCount] = "";
        rentalCosts[rentalCount] = 0;
        isRented[index] = true;

        System.out.println("Car with registration number " + reg + " was rented by " + name + " at " + pickup + ".");
        return rentalCount + 1;
    }

    /**
     * Processes the return of a rented car.
     *
     * @param regNumbers car numbers
     * @param models car models
     * @param isRented rental status
     * @param carCount car count
     * @param rentalNames rental names
     * @param rentalRegNumbers rental reg numbers
     * @param pickupTimes pickup times
     * @param returnTimes return times
     * @param rentalCosts costs
     * @param rentalCount number of rentals
     */
    private static void returnCar(
        final String[] regNumbers, final String[] models, final boolean[] isRented, final int carCount,
        final String[] rentalNames, final String[] rentalRegNumbers, final String[] pickupTimes,
        final String[] returnTimes, final double[] rentalCosts, final int rentalCount
    ) {
        System.out.print("> Enter registration number: ");
        final String reg = SCANNER.nextLine().toUpperCase();
        if (!reg.matches("[A-Z]{3}[0-9]{3}")) {
            System.out.println("invalid registration number");
            return;
        }

        int carIndex = -1;
        for (int i = 0; i < carCount; i++) {
            if (regNumbers[i].equals(reg)) {
                carIndex = i;
                break;
            }
        }

        if (carIndex == -1) {
            System.out.println("car " + reg + " not found");
            return;
        }
        if (!isRented[carIndex]) {
            System.out.println("car " + reg + " not rented");
            return;
        }

        System.out.print("> Enter time of return: ");
        final String ret = SCANNER.nextLine();
        if (!isValidTime(ret)) {
            System.out.println("invalid time format");
            return;
        }

        for (int i = 0; i < rentalCount; i++) {
            if (rentalRegNumbers[i].equals(reg) && returnTimes[i].isEmpty()) {
                returnTimes[i] = ret;
                final double hours = calculateHours(pickupTimes[i], ret);
                rentalCosts[i] = Math.round(hours * COST_PER_HOUR * ROUNDING_SCALE) / ROUNDING_SCALE;
                isRented[carIndex] = false;

                System.out.println("===================================");
                System.out.println("LTU Rent-a-car");
                System.out.println("===================================");
                System.out.println("Name: " + rentalNames[i]);
                System.out.println("Car: " + models[carIndex] + " (" + reg + ")");
                System.out.println("Time: " + pickupTimes[i] + "-" + ret + " (" + hours + " hours)");
                System.out.println("Total cost: " + (int) rentalCosts[i] + " SEK");
                return;
            }
        }
    }

    /**
     * Prints all cars and statuses.
     *
     * @param regNumbers car numbers
     * @param models car models
     * @param isRented statuses
     * @param carCount count of cars
     */
    private static void printFleet(final String[] regNumbers, final String[] models, final boolean[] isRented, final int carCount) {
        System.out.println("LTU Rent-a-car car fleet:");
        System.out.println("Fleet:");
        System.out.println("Model\tNumberplate\tStatus");
        int available = 0;
        for (int i = 0; i < carCount; i++) {
            final String status = isRented[i] ? "Rented" : "Available";
            System.out.println(models[i] + "\t" + regNumbers[i] + "\t" + status);
            if (!isRented[i]) {
                available++;
            }
        }
        System.out.println("Total number of cars: " + carCount);
        System.out.println("Total number of available cars: " + available);
    }

    /**
     * Prints all rentals and revenue.
     *
     * @param rentalNames names
     * @param rentalRegNumbers numbers
     * @param pickupTimes times
     * @param returnTimes times
     * @param rentalCosts costs
     * @param rentalCount count
     */
    private static void printSummary(final String[] rentalNames, final String[] rentalRegNumbers,
                                     final String[] pickupTimes, final String[] returnTimes,
                                     final double[] rentalCosts, final int rentalCount) {
        System.out.println("LTU Rent-a-car rental summary:");
        System.out.println("Rentals:");
        System.out.println("Name\tNumberplate\tPickup\tReturn\tCost");
        double total = 0;
        for (int i = 0; i < rentalCount; i++) {
            final String ret = returnTimes[i].isEmpty() ? "" : returnTimes[i];
            final String cost = returnTimes[i].isEmpty() ? "" : ((int) rentalCosts[i]) + " SEK";
            System.out.println(rentalNames[i] + "\t" + rentalRegNumbers[i] + "\t" + pickupTimes[i] + "\t" + ret + "\t" + cost);
            total += rentalCosts[i];
        }
        System.out.println("Total number of rentals: " + rentalCount);
        System.out.println("Total revenue: " + (int) total + " SEK");
    }

    /**
     * Checks if the time is in hh:mm format and valid.
     *
     * @param time input time
     * @return true if valid, false otherwise
     */
    private static boolean isValidTime(final String time) {
        if (!time.matches("[0-9]{2}:[0-9]{2}")) {
            return false;
        }
        final String[] parts = time.split(":");
        final int h = Integer.parseInt(parts[0]);
        final int m = Integer.parseInt(parts[1]);
        return h >= 0 && h <= MAX_HOUR && m >= 0 && m <= MAX_MINUTE;
    }

    /**
     * Calculates time difference in hours between two times.
     *
     * @param from start time
     * @param to end time
     * @return number of hours as double
     */
    private static double calculateHours(final String from, final String to) {
        final String[] f = from.split(":");
        final String[] t = to.split(":");
        final int startMin = Integer.parseInt(f[0]) * MINUTES_IN_HOUR + Integer.parseInt(f[1]);
        final int endMin = Integer.parseInt(t[0]) * MINUTES_IN_HOUR + Integer.parseInt(t[1]);
        if (endMin <= startMin) {
            return 0;
        }
        return Math.round((endMin - startMin) / (double) MINUTES_IN_HOUR * ONE_DECIMAL) / ONE_DECIMAL;
    }
}