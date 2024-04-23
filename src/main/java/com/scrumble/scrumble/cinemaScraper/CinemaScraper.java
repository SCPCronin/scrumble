package com.scrumble.scrumble.cinemaScraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CinemaScraper {

    public HashMap<String, HashMap<String, HashMap<String, List<String>>>> getMovieShowTimesForAllCinemas () throws IOException {
        HashMap<String, HashMap<String, HashMap<String, List<String>>>> movieTimesByCinema = new HashMap<>();
        movieTimesByCinema.put("drogheda-boyne-centre", this.getListOfMovieTitlesByCinemaName("drogheda-boyne-centre"));
        movieTimesByCinema.put("drogheda-scotch-hall", this.getListOfMovieTitlesByCinemaName("drogheda-scotch-hall"));
        movieTimesByCinema.put("dublin-rathmines", this.getListOfMovieTitlesByCinemaName("dublin-rathmines"));
        movieTimesByCinema.put("balbrigggan", this.getListOfMovieTitlesByCinemaName("balbrigggan"));
        return movieTimesByCinema;
    }

    public HashMap<String, HashMap<String, List<String>>> getListOfMovieTitlesByCinemaName(String cinemaName) throws IOException {
        Document doc = Jsoup.connect("https://www.omniplex.ie/cinema/" + cinemaName).get();
        Elements listOfElements = doc.getElementsByClass("OMP_eventWrapper OMP_splitContainer OMP_borderBottomF OMP_padding30-tb");
        // Go through each element in the listofelements, and execute the parseMovieNameFromHTMl function on it. Add the result to a list
        HashMap<String, HashMap<String, List<String>>> movieTimes = new HashMap<>();
        for (Element element : listOfElements) {
            movieTimes.put(parseMovieNameFromHTMl(element.html()), parseMovieTimesFromHTML(element.html()));
        }
        return this.filterRegularMovies(movieTimes);
    };

    private String parseMovieNameFromHTMl(String html) {
        // Parse the movie name from this HTML:
        Document doc = Jsoup.parse(html);
        Element movieNameElement = doc.selectFirst("div.OMP_infoSection h3");
        return movieNameElement.text();
    }

    private HashMap<String, List<String>> parseMovieTimesFromHTML(String html) {
        Document doc = Jsoup.parse(html);
        HashMap<String, List<String>> movieTimesMap = new HashMap<>();

        // Find all elements containing movie times
        Elements dateElements = doc.select("div.OMP_listingDate");
        for (Element dateElement : dateElements) {
            String date = dateElement.select("span.OMP_colourD").text();
            Elements timeElements = dateElement.select("div.OMP_perfTimes a.OMP_buttonSelection");

            List<String> times = new ArrayList<>();
            for (Element timeElement : timeElements) {
                String time = timeElement.text();
                times.add(time);
            }

            movieTimesMap.put(date, times);
        }

        return movieTimesMap;
    }

    private HashMap<String, HashMap<String, List<String>>> filterRegularMovies(HashMap<String, HashMap<String, List<String>>> movieMap) {
        HashMap<String, HashMap<String, List<String>>> filteredMap = new HashMap<>();
        for (Map.Entry<String, HashMap<String, List<String>>> entry : movieMap.entrySet()) {
            HashMap<String, List<String>> movieDates = entry.getValue();
            if (movieDates.size() == 1) {
                filteredMap.put(entry.getKey(), movieDates);
            }
        }
        return filteredMap;
    }
}
