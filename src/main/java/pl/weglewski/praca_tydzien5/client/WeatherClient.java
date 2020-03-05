package pl.weglewski.praca_tydzien5.client;

import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import pl.weglewski.praca_tydzien5.model.ConsolidatedWeather;
import pl.weglewski.praca_tydzien5.model.Location;
import pl.weglewski.praca_tydzien5.model.Weather;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Controller
public class WeatherClient {

    private static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Location[] locations;
    private Weather weather;
    private List<ConsolidatedWeather> listConsWeather;
    private ConsolidatedWeather todayWeather;

    public void getLocation(String loc) {
        RestTemplate restTemplate = new RestTemplate();
        locations = restTemplate.getForObject("https://www.metaweather.com/api/location/search/?query="+loc,
                Location[].class);
    }

    public void getWeather(Integer woeid) {
        RestTemplate restTemplate = new RestTemplate();
        weather = restTemplate.getForObject("https://www.metaweather.com/api/location/"+woeid.toString(),
                Weather.class);
    }

    public String getCity(String Latt) {
        Optional<Location> locationByLatt = Stream.of(locations).filter(v -> Latt.equalsIgnoreCase(v.getLattLong())).findFirst();
        return locationByLatt.get().getTitle();
    }

    public List<String> getLattCityList(String city){
        getLocation(city);
        List<String> labels = new ArrayList<>();
        Stream.of(locations).forEach(v -> labels.add(v.getLattLong()));
        List<String> lattcity = new ArrayList<>();;
        labels.stream().forEach(v -> lattcity.add(getCity(v)+" ~ "+v));
        return lattcity;
    }

    public Integer getWoeid(String Latt) {
        Optional<Location> locationByLatt = Stream.of(locations).filter(v -> Latt.equalsIgnoreCase(v.getLattLong())).findFirst();
        return locationByLatt.get().getWoeid();
    }

    public void getTodayWeather(String select){
        getWeather(getWoeid(select.substring(select.indexOf("~")+2)));
        listConsWeather = weather.getConsolidatedWeather();
        Date date = new Date();
        String dat = (sdf.format(date));
        Optional<ConsolidatedWeather> todayWeath = listConsWeather.stream().filter(d -> dat.equalsIgnoreCase(d.getApplicableDate())).findFirst();
        todayWeather = todayWeath.get();
    }

    public String getTemp(){
        Double temp = todayWeather.getTheTemp();
        String rounded = String.format("%.1f", temp);
        return rounded + " *C";
    }
    public String getWind(){
        Double speed = todayWeather.getWindSpeed()*1.609344;
        String rounded = String.format("%.1f", speed);
        return rounded + " km/h " + todayWeather.getWindDirectionCompass();
    }
    public String getPressure(){
        return todayWeather.getAirPressure().toString() + " mbar";
    }
    public String getState(){
        return todayWeather.getWeatherStateName();
    }

    public String getAbbr(){
        return todayWeather.getWeatherStateAbbr();
    }
}
