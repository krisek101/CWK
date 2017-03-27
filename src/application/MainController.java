package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.jfoenix.controls.JFXTimePicker;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.text.Text;

public class MainController {

	private int hour = 0, minute = 0, second = 0;
	
	private LocalTime now, newValue;
	
	Timer timer;

	@FXML
	private Text czas;

	@FXML
	private Button start;

	@FXML
	private Button stop;

    @FXML
	private JFXTimePicker timePicker;

	@FXML
	private Spinner<Integer> hourSpinner;

	@FXML
	private Spinner<Integer> minuteSpinner;

	@FXML
	private Spinner<Integer> secondSpinner;

	@FXML
	private void initialize() {
		stop.setDisable(true);
		
		initSpinners();

		timePicker.valueProperty().addListener(new ChangeListener<LocalTime>(){
			@Override
			public void changed(ObservableValue<? extends LocalTime> obj, LocalTime old_value, LocalTime new_value) {
				timer = new Timer();
				timer.schedule(new TimerTask(){
					@Override
					public void run() {
						newValue = new_value;
						now = LocalTime.now();
						newValue = new_value.minusHours(now.getHour());
						newValue = new_value.minusMinutes(now.getMinute());
						hour = newValue.getHour();
						minute = newValue.getMinute();
						second = new_value.getSecond();
						changeCounter(hour, minute, second);
						hourSpinner.getValueFactory().setValue(hour);
						minuteSpinner.getValueFactory().setValue(minute);
						secondSpinner.getValueFactory().setValue(second);
					}
				}, 10, 60000);
			}
		});		
	    
		start.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				start.setDisable(true);
				stop.setDisable(false);
				minuteSpinner.setDisable(true);
				hourSpinner.setDisable(true);
				secondSpinner.setDisable(true);
				timePicker.setDisable(true);
				if(hour!=0 || minute!=0 || second!=0){
					startCounting();
				}
			}
		});
		stop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if(timer != null){
					timer.cancel();
				}
				minuteSpinner.setDisable(false);
				hourSpinner.setDisable(false);
				secondSpinner.setDisable(false);
				start.setDisable(false);
				timePicker.setDisable(false);
				minuteSpinner.getValueFactory().setValue(minute);
				hourSpinner.getValueFactory().setValue(hour);
				secondSpinner.getValueFactory().setValue(second);
			}
		});
	}

	private void initSpinners() {
		hourSpinner.valueProperty().addListener((obs, old_value, new_value) -> {
			hour = new_value;
			changeCounter(hour, minute, second);
		});
		hourSpinner.setEditable(true);
		minuteSpinner.valueProperty().addListener((obs, old_value, new_value) -> {
			minute = new_value;
			changeCounter(hour, minute, second);
		});
		minuteSpinner.setEditable(true);
		secondSpinner.valueProperty().addListener((obs, old_value, new_value) -> {
			second = new_value;
			changeCounter(hour, minute, second);
		});
		secondSpinner.setEditable(true);
	}

	private void startCounting() {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (second == 0 && minute > 0) {
					minute--;
					second = 60;
				}
				if (second == 0 && minute == 0 && hour > 0) {
					hour--;
					minute = 59;
					second = 60;
				}
				second--;
				changeCounter(hour, minute, second);
				if (second == 0 && minute == 0 && hour == 0) {
					timer.cancel();
					Runtime runtime = Runtime.getRuntime();
					try {
						Process proc = runtime.exec("shutdown -s -t 0");
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.exit(0);
				}
			}
		}, 100, 1000);
	}

	private void changeCounter(int hour, int min, int sec) {
		this.hour = hour;
		this.minute = min;
		this.second = sec;
		String timeString = "";
		if (hour < 10) {
			timeString += "0" + hour + ":";
		} else {
			timeString += hour + ":";
		}
		if (min < 10) {
			timeString += "0" + min + ":";
		} else {
			timeString += min + ":";
		}
		if (sec < 10) {
			timeString += "0" + sec;
		} else {
			timeString += sec;
		}
		czas.setText(timeString);
	}
}