import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MostActiveCookieTest {

    @Test
    public void testParseCommandLineArgs_ValidArgs() {
        String[] args = {"-f", "test.log", "-d", "2024-03-23"};
        CommandLineArgs result = MostActiveCookie.parseCommandLineArgs(args);
        assertEquals("test.log", result.filename());
        assertEquals(LocalDate.parse("2024-03-23"), result.date());
    }

    @Test
    public void testParseCommandLineArgs_InvalidArgs() {
        String[] args = {"-f", "test.log"};
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> MostActiveCookie.parseCommandLineArgs(args));
        assertEquals("Incorrect arguments, use the following format: java main.java.MostActiveCookie -f <filename> -d <date>",
                exception.getMessage());
    }

    @Test
    public void testParseCommandLineArgs_InvalidDate() {
        String[] args = {"-f", "test.log", "-d", "invalid_date"};
        DateTimeParseException exception = assertThrows(DateTimeParseException.class,
                () -> MostActiveCookie.parseCommandLineArgs(args));
        // Ensure that the exception message contains the expected substring
        assertTrue(exception.getMessage().contains("Text 'invalid_date' could not be parsed"));
    }

    @Test
    public void testFindMostActiveCookies_FileNotFound() {
        CommandLineArgs commandLineArgs = new CommandLineArgs("nonexistent.log", LocalDate.parse("2024-03-23"));
        // Ensure IOException is thrown
        IOException exception = assertThrows(IOException.class,
                () -> MostActiveCookie.findMostActiveCookies(commandLineArgs));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "resources/testDataValidResults.csv", numLinesToSkip = 1)
    public void testFindMostActiveCookies_ValidResults(
            String input, String expected) throws IOException {
        StringBuilder actualValueTemp = new StringBuilder();
        String actualValue;
        List<String> actualListValues = MostActiveCookie.findMostActiveCookies(MostActiveCookie.parseCommandLineArgs(input.split(" ")));
        for(String value : actualListValues) {
            actualValueTemp.append(value).append("|");
        }
        actualValue = actualValueTemp.substring(0, actualValueTemp.length()-1);
        assertEquals(expected, actualValue);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "resources/testDataInValidResults.csv", numLinesToSkip = 1)
    public void testFindMostActiveCookies_InValidResults(
            String input, String expected) throws IOException {
        StringBuilder actualValueTemp = new StringBuilder();
        String actualValue;
        Exception exception = assertThrows(Exception.class, ()-> MostActiveCookie.findMostActiveCookies(MostActiveCookie.parseCommandLineArgs(input.split(" "))));
    }
}