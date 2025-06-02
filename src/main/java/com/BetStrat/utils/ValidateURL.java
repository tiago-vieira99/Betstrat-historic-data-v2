package com.BetStrat.utils;

import static com.BetStrat.constants.BetStratConstants.*;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ValidateURL {

    public static int isValidURL(String url) throws IOException {
        if (url.contains("team.asp")) {
            Document connection = Jsoup.connect(SOCCERSTATS_BASE_URL + url).get();
        } else if (url.contains("team_matches")){
            Document connection = Jsoup.connect(ZEROZERO_BASE_URL + url).get();
        } else {
            Document connection = Jsoup.connect(FCSTATS_BASE_URL + url).get();
        }
        return 0;
    }

}
