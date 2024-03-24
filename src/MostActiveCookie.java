import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

record CommandLineArgs(String filename, LocalDate date) {
}
public class MostActiveCookie {

    public static void main(String[] args) {
        try {
            CommandLineArgs commandLineArgs = parseCommandLineArgs(args);
            List<String> mostActiveCookies = findMostActiveCookies(commandLineArgs);
            mostActiveCookies.forEach(System.out::println);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
        catch (DateTimeParseException e) {
            System.err.println("Invalid date format. Please use yyyy-MM-dd format.");
        }
        catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

     static CommandLineArgs parseCommandLineArgs(String[] args) throws DateTimeParseException {
        if (args.length != 4 || !args[0].equals("-f") || !args[2].equals("-d")) {
            throw new IllegalArgumentException("Incorrect arguments, use the following format: java main.java.MostActiveCookie -f <filename> -d <date>");
        }

        String filename = args[1];
        String dateString = args[3];

        LocalDate date;
        date = LocalDate.parse(dateString);

        return new CommandLineArgs(filename, date);
    }

     static List<String> findMostActiveCookies(CommandLineArgs commandLineArgs) throws IOException {
        Map<String, Integer> cookieCounts = new HashMap<>();
        Set<String> mostActiveCookies = new HashSet<>();
        int[] maxCount = {0}; // a workaround for final variables in Lambda, no side effects because the array is not used outside the Lambda

        try (BufferedReader br = new BufferedReader(new FileReader(commandLineArgs.filename()))) {
            br.lines()
                    .skip(1) // Skip the headers
                    .map(line -> line.trim().split(","))
                    .filter(arrayOfLogs -> {
                        if(arrayOfLogs.length == 2) return true;
                        else {
                            System.err.println("bad formatted log: " + Arrays.toString(arrayOfLogs));
                            return false;
                        }
                    })
                    .forEach(arrayOfLogs -> {
                        String cookie = arrayOfLogs[0];
                        LocalDateTime timestamp;
                        try {
                            timestamp = LocalDateTime.parse(arrayOfLogs[1], DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                        } catch (DateTimeParseException  e) {
                            System.err.println(e.getMessage());
                            return; // Skip this line and continue processing next line
                        }
                        if (timestamp.toLocalDate().equals(commandLineArgs.date())) {
                            int count = cookieCounts.merge(cookie, 1, Integer::sum);
                            if (count > maxCount[0]) { // Using array element instead of variable
                                maxCount[0] = count;
                                mostActiveCookies.clear();
                                mostActiveCookies.add(cookie);
                            } else if (count == maxCount[0]) {
                                mostActiveCookies.add(cookie);
                            }
                        }
                    });
        }
        return new ArrayList<>(mostActiveCookies);
    }
}