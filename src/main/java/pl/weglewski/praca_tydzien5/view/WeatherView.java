package pl.weglewski.praca_tydzien5.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import pl.weglewski.praca_tydzien5.client.WeatherClient;

import java.util.ArrayList;
import java.util.List;

@Route("weather")
public class WeatherView extends VerticalLayout {

    private WeatherClient weatherClient;

    @Autowired
    public WeatherView(WeatherClient weatherClient) {

        TextField labelField = new TextField();
        labelField.setLabel("Locate the city");

        Button button = new Button("Search");

        Label label = new Label("select appropriate localization ~ Latitude, Longitude:");
        Label label2 = new Label("nothing found");
        ListBox<String> listBox = new ListBox<>();
        Button show = new Button("Show weather");
        Button s1 = new Button("S1");
        add(labelField, button);

        Label state = new Label();

        Label temp = new Label();
        Label wind = new Label();
        Label pressure = new Label();

        Image image = new Image();

        button.addClickListener(buttonClickEvent -> {
            remove(label);
            remove(label2);
            remove(listBox);
            remove(show);

            remove(state);
            remove(temp);
            remove(wind);
            remove(pressure);
            remove(image);

            List<String> labels = weatherClient.getLattCityList(labelField.getValue());

            if (labels.size()>0)  {

                listBox.setItems(labels);
                add(label, listBox, show);
            }
                    else add(label2);

            show.addClickListener(showClickEvent -> {
                String select = listBox.getValue();
                if (select != null) {
                    weatherClient.getTodayWeather(select);
                    state.setText(weatherClient.getState());
                    temp.setText("Temperature: " + weatherClient.getTemp());
                    wind.setText("Wind: " + weatherClient.getWind());
                    pressure.setText("Pressure: " + weatherClient.getPressure());

                    add(state);
                    image.setSrc("https://www.metaweather.com/static/img/weather/png/64/" +
                            weatherClient.getAbbr() + ".png");
                    image.setAlt("DummyImage");
                    add(image);

                    add(temp, wind, pressure);
                }
            });
        });


    }
}
