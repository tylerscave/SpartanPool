package controller;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.Context;
import model.StringFormat;
import model.schedule.Location;
import model.schedule.Request;
import model.schedule.Ride;
import model.member.Driver;
import model.member.Member;
import model.member.Vehicle;
import model.schedule.Drive;
import model.schedule.DriveChoice;
import model.schedule.WeeklySchedule;
import model.schedule.RideRequest;
import model.schedule.Schedulable;
import model.schedule.ScheduleViewer;
import model.schedule.Scheduler;
import model.schedule.SchedulingContext;
import model.schedule.RideRequest.TimeType;

/**
 *COPYRIGHT (C) 2016 CmpE133_7. All Rights Reserved.
 * The controller for the DriveRequestScene
 * Solves CmpE133 SpartanPool
 * @author Tyler Jones,
*/
public class DriveRequestController implements Initializable {
    private Context context;
    //private SchedulingContext sc;
    private Member member;
    private Location pickup;
    private Location destination;
    private LocalDate date;
    private GregorianCalendar hourTime; 
    private Integer minuteTime;
    private boolean byStartTime;
    private ObservableList<GregorianCalendar> hours = FXCollections.observableArrayList();;
    private ObservableList<Location> locations = FXCollections.observableArrayList();
    private ObservableList<Integer> minutes = FXCollections.observableArrayList();
	
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<Location> pickupLocation;
    @FXML
    private ComboBox<Location> destinationLocation;
    @FXML
    private RadioButton arrivalRadio;
    @FXML
    private RadioButton departureRadio;
    @FXML
    private ComboBox<GregorianCalendar> hourCombo;
    @FXML
    private ComboBox<Integer> minuteCombo;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
        context = Context.getInstance();
        member = context.getMember();
        
        Vehicle vehicle = new Vehicle();
        member.setDrivingType(new Driver("licenseNumber", vehicle, null));
        
        //set up the location ComboBox
        for (Location l : context.getMap().getLocations())
            locations.add(l);
        pickupLocation.setItems(locations);
        destinationLocation.setItems(locations);

        
        //setup Arrival and Departure ComboBoxes
        //they are set up for todays date until a new date is picked from datePicker
        date = LocalDate.now();
        hours = hoursMaker(date);   
        hourCombo.setItems(hours);
        ArrayList<Integer> mins = new ArrayList<>(Arrays.asList(0, 10, 20, 30, 40, 50));
        minutes.addAll(mins);
        minuteCombo.setItems(minutes);        		
	}
	
	@FXML
	private void handleDatePicker(ActionEvent event) {
        date = datePicker.getValue();
        
        //update the arrive and depart boxes with correct date from datePicker
		hours.clear();		
        hours = hoursMaker(date);     
        hourCombo.setItems(hours);     
	}
	
	@FXML
	private void handleLocationCombos(ActionEvent event) {
		pickup = pickupLocation.getSelectionModel().getSelectedItem();
		destination = destinationLocation.getSelectionModel().getSelectedItem();
	}
	
	@FXML
	private void handleRadios(ActionEvent event) {
    	RadioButton radio = (RadioButton) event.getSource();
    	if (radio == arrivalRadio) {
    		byStartTime = false;
    	} else if(radio == departureRadio){
    		byStartTime = true;
    	}
	}
	
	@FXML
	private void handleHourCombo(ActionEvent event) {
		hourTime = hourCombo.getSelectionModel().getSelectedItem();
	}
	
	@FXML
	private void handleMinuteCombo(ActionEvent event) {
		minuteTime = minuteCombo.getSelectionModel().getSelectedItem();
	}

    @FXML
    private void handleCancelButton(ActionEvent event) {
    	try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/HomeScene.fxml"));
            Scene scene = new Scene(root);
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException ex) {
            Logger.getLogger(LoginSceneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void handleSubmitButton(ActionEvent event) {
    	GregorianCalendar selectedDateTime = new GregorianCalendar();
    	hourTime.set(GregorianCalendar.MINUTE, minuteTime);
    	selectedDateTime = hourTime;
        Request request = new Request(member, selectedDateTime, pickup, destination, Request.TimeType.Near, byStartTime);
        SchedulingContext sc = new SchedulingContext();
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setResizable(true);
    	alert.getDialogPane().setPrefSize(500, 250);
    	String fail = sc.schedule(request, null);
        if (fail.equals(Scheduler.SUCCESS)) {
        	Drive drive = member.getDrives().get(member.getDrives().size()-1);
        	List<Location> stops = drive.getRoute().getStops();
        	String alertMsg = "On "+StringFormat.getDateFromCalendar(drive.getEndTime())+
        			"\nFrom "+stops.get(0)+" at "+StringFormat.getTimeFromCalendar(drive.getStartTime())+
        			"\nTo "+stops.get(stops.size()-1)+" at "+StringFormat.getTimeFromCalendar(drive.getEndTime());
            if (stops.size() > 2) {
                alertMsg = alertMsg+"\nStops at: ";
                for (int j = 1; j < stops.size()-1; j++) {
                    alertMsg = alertMsg+stops.get(j) + ", ";
                }
            }
            alertMsg = "\n"+alertMsg+" "+drive.getNumSeats()+" seats available \nPassengers: ";                
            if (drive.numberOfRides() == 0) {
            	alertMsg = alertMsg+"None";
            }
            for (int i = 0; i < drive.numberOfRides(); i++) {
                ScheduleViewer sv = new ScheduleViewer();
                Ride ride = sv.getRideById(drive.getRideId(i));
                List<Location> rideStops = ride.getRoute().getStops();
                alertMsg = alertMsg+"  "+ride.getMemberName() + ": From "+rideStops.get(0)+" at "+StringFormat.getTimeFromCalendar(ride.getStartTime())+
                		"\nto "+rideStops.get(rideStops.size()-1)+
                		"\nat "+StringFormat.getTimeFromCalendar(ride.getEndTime());
            }      	
        	alert.setTitle("Schedule Information");
        	alert.setHeaderText("New Drive Scheduled!");
        	alert.setContentText(alertMsg);
        	alert.showAndWait();	
        } else {
        	Alert errorAlert = new Alert(AlertType.ERROR);
        	errorAlert.setTitle("Schedule Information");
        	errorAlert.setHeaderText(null);
        	errorAlert.setContentText(fail);
        	errorAlert.showAndWait();
        }
/*  
        //FOR TESTING DELETE LATER
		String requestAlert =  selectedDateTime.getDisplayName(GregorianCalendar.DAY_OF_WEEK, 
				GregorianCalendar.LONG, Locale.getDefault()) + " " +
				(selectedDateTime.get(GregorianCalendar.MONTH)+1) + "/" + //GregorianCalendar Jan=0
				selectedDateTime.get(GregorianCalendar.DAY_OF_MONTH) + "/" +
				selectedDateTime.get(GregorianCalendar.YEAR) + " " +
				selectedDateTime.get(GregorianCalendar.HOUR) + ":" +
				selectedDateTime.get(GregorianCalendar.MINUTE) + " " +
				selectedDateTime.getDisplayName(GregorianCalendar.AM_PM, 
						GregorianCalendar.LONG, Locale.getDefault());
*/
    	handleCancelButton(event);
    }
    
    /**
     * hoursMaker is a helper method to add GregorianCalendar objects (representing week days 
     * with specific hours) to the comboboxes with appropriate Strings printed in the dropdown list
     * @param date the LocalDate from datePicker to be converted to a GregorianCalendar object
     * @return list of hours for the Comboboxes with reference to their weekday
     */
	private ObservableList<GregorianCalendar> hoursMaker(LocalDate date) {
		hours = FXCollections.observableArrayList();
		// get times for 6AM to 11AM
		for (int i = 6; i <= 11; i++) {
			GregorianCalendar day = new GregorianCalendar() {
	            public String toString() {
	                return Integer.toString(this.get(GregorianCalendar.HOUR))
	                		+ this.getDisplayName(GregorianCalendar.AM_PM, 
	    							GregorianCalendar.LONG, Locale.getDefault());
	            	
	            }
	        };
	        day.set((GregorianCalendar.DAY_OF_YEAR), date.getDayOfYear());
			day.set(GregorianCalendar.AM_PM, GregorianCalendar.AM);
			day.set(GregorianCalendar.HOUR, i);
			day.clear(GregorianCalendar.MINUTE);
			day.clear(GregorianCalendar.SECOND);
			day.clear(GregorianCalendar.MILLISECOND);
			hours.add(day);
		}
		
		// get times for noon to 10pm
		for (int i = 12; i <= 22; i++) {
			GregorianCalendar day = new GregorianCalendar() {
	            public String toString() {
	            	if (this.get(GregorianCalendar.HOUR) == 0) {
	            		return 12 + this.getDisplayName(GregorianCalendar.AM_PM, 
		    							GregorianCalendar.LONG, Locale.getDefault());
	            	} else {
	            		return Integer.toString(this.get(GregorianCalendar.HOUR))
	            				+ this.getDisplayName(GregorianCalendar.AM_PM, 
	            						GregorianCalendar.LONG, Locale.getDefault());
	            	}
	            }
	        };
	        day.set((GregorianCalendar.DAY_OF_YEAR), date.getDayOfYear());	        
			day.set(GregorianCalendar.AM_PM, GregorianCalendar.PM);
			day.set(GregorianCalendar.HOUR_OF_DAY, i);
			day.clear(GregorianCalendar.MINUTE);
			day.clear(GregorianCalendar.SECOND);
			day.clear(GregorianCalendar.MILLISECOND);
        	hours.add(day);
		}
		return hours;
	}

}
