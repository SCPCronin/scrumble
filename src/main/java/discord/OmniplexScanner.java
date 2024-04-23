package discord;

import com.scrumble.scrumble.cinemaScraper.CinemaScraper;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class OmniplexScanner extends DiscordBot {


    public OmniplexScanner() {
        super();
    }

    private String beautifyCinemaName(String cinemaName) {
        String[] cinemaNameWordList = cinemaName.split("-");
        List<String> outputList = new ArrayList<>();
        for (String word : cinemaNameWordList) {
            outputList.add(word.substring(0, 1).toUpperCase() + word.substring(1));
        }
        return String.join(" ", outputList);
    }

    private static boolean isDateWithinNextWeek(String dateString) {
        // Parse the input date string into a LocalDate object
        dateString = dateString.replaceAll("(?<=\\d)(st|nd|rd|th)\\b", "");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
        LocalDate date = LocalDate.parse(dateString, formatter);

        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Calculate the date one week from now
        LocalDate oneWeekFromNow = currentDate.plusWeeks(1);

        // Check if the given date is within the next week
        return date.isAfter(currentDate) && date.isBefore(oneWeekFromNow);
    }

    private Boolean isMovieWithinTheNextWeek(HashMap<String, List<String>> movieTimes) {
        for (String date : movieTimes.keySet()) {
            if (isDateWithinNextWeek(date)) {
                return true;
            }
        }
        return false;
    }

    private String generateMessage(String cinemaName) throws IOException {

        // Get the data from the cinema scraper
        CinemaScraper cinemaScraper = new CinemaScraper();
        HashMap<String, HashMap<String, List<String>>> movieTimes = cinemaScraper.getListOfMovieTitlesByCinemaName(cinemaName);

        StringBuilder markdown = new StringBuilder();
        markdown.append("# " + this.beautifyCinemaName(cinemaName) + "\n");


        // For every key value pair in the movieTimes hashmap, add the movie name and the movie times to the markdown
        for (String movieName : movieTimes.keySet()) {
            // Check to see if the movie is within the next week
            if (isMovieWithinTheNextWeek(movieTimes.get(movieName))) {
                markdown.append("## ").append(movieName).append("\n");
                HashMap<String, List<String>> movieTimesMap = movieTimes.get(movieName);
                for (String date : movieTimesMap.keySet()) {
                    if (isDateWithinNextWeek(date)) {
                        markdown.append("### ").append(date).append("\n");
                        List<String> times = movieTimesMap.get(date);
                        for (String time : times) {
                            markdown.append("- ").append(time).append("\n");
                        }
                    }
                }
            }
        }

        return markdown.toString();
    }

    // Can you schedule this function to run every day at 9 am?
    @Scheduled(cron = "0 0 * * * 0")
    public void sendMovieSchedulesToDiscordChannel() throws IOException {
        sendMessageToChannel(this.generateMessage("dublin-rathmines"), DiscordConstants.CHANNELID_TESTING_BOTS);
        sendMessageToChannel(this.generateMessage("drogheda-boyne-centre"), DiscordConstants.CHANNELID_TESTING_BOTS);
        sendMessageToChannel(this.generateMessage("drogheda-scotch-hall"), DiscordConstants.CHANNELID_TESTING_BOTS);
        sendMessageToChannel(this.generateMessage("balbriggan"), DiscordConstants.CHANNELID_TESTING_BOTS);
    }
}