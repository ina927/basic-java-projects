import java.util.*;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RentalBusinessModel{
    private ObservableList<Car> inventory;
    private ObservableList<Car> customisedInventory;
    private ObservableList<Rental> rentals;

    RentalBusinessModel(){
        this.inventory = FXCollections.observableArrayList();
        inventory.addAll(new Car(FuelType.GASOLINE, CarCategory.SMALL, "ZKW482", "KIA", "Picanto", 5, 55.8),
        new Car(FuelType.GASOLINE, CarCategory.SMALL, "XJD539", "MG", "MG-3", 5, 55.4),
        new Car(FuelType.GASOLINE, CarCategory.SMALL, "QPR614", "HYUNDAI", "Accent", 5, 90.3),
        new Car(FuelType.GASOLINE, CarCategory.SMALL, "LMN852", "TOYOTA", "Yaris", 5, 97.2),
        new Car(FuelType.GASOLINE, CarCategory.SMALL, "VLT349", "MITSUBISHI", "Mirage", 5, 36.5),
        new Car(FuelType.GASOLINE, CarCategory.MEDIUM, "BHC763", "MG", "MG-5", 5, 76.1),
        new Car(FuelType.GASOLINE, CarCategory.MEDIUM, "GFA185", "KIA", "Cerato", 5, 170.4),
        new Car(FuelType.GASOLINE, CarCategory.MEDIUM, "HZK573", "HYUNDAI", "i30", 5, 107.6),
        new Car(FuelType.GASOLINE, CarCategory.LARGE, "UHT649", "MITSUBISHI", "ASX", 5, 181.2),
        new Car(FuelType.GASOLINE, CarCategory.LARGE, "RNP825", "TOYOTA", "Camry", 5, 115.8),
        new Car(FuelType.GASOLINE, CarCategory.LARGE, "BSL931", "HYUNDAI", "Elantra", 5, 116.3),
        new Car(FuelType.GASOLINE, CarCategory.LARGE, "TRF789", "HONDA", "Accord", 5, 130.7),
        new Car(FuelType.GASOLINE, CarCategory.SUV, "FJC719", "KIA", "Sportage", 8, 113.5),
        new Car(FuelType.GASOLINE, CarCategory.SUV, "MWH364", "MG", "HS", 7, 78.1),
        new Car(FuelType.GASOLINE, CarCategory.SUV, "YUP649", "TOYOTA", "Rav-4", 7, 107.8),
        
        new Car(FuelType.ELECTRIC, CarCategory.SMALL, "PLM456", "NISSAN", "Leaf", 5, 50.3),
        new Car(FuelType.ELECTRIC, CarCategory.SMALL, "HJK987", "FORD", "Fiesta", 5, 45.6),new Car(FuelType.ELECTRIC, CarCategory.MEDIUM, "XNC473", "TESLA", "Model-3", 5, 109.3),
        new Car(FuelType.ELECTRIC,CarCategory.MEDIUM, "LRT815","MG", "MG-4 EV", 5, 70.9),
        new Car(FuelType.ELECTRIC,CarCategory.MEDIUM, "QFJ658", "HYUNDAI", "Ioniq 5", 5, 90.2),
        new Car(FuelType.ELECTRIC, CarCategory.MEDIUM, "BNM234", "AUDI", "e-tron", 5, 145.9),
        new Car(FuelType.ELECTRIC, CarCategory.MEDIUM, "VGB675", "BMW", "i3", 5, 88.1),
        new Car(FuelType.ELECTRIC,CarCategory.LARGE, "VBY192", "TOYOTA", "bZ3", 5, 120.4),
        new Car(FuelType.ELECTRIC,CarCategory.LARGE, "ZGH983", "TESLA", "Model-Y", 5, 121.7),
        new Car(FuelType.ELECTRIC,CarCategory.LARGE, "TKJ347", "TESLA", "Model-S", 5, 150.2),
        new Car(FuelType.ELECTRIC,CarCategory.SUV, "FLW573", "TESLA", "Model-X", 7, 180.8),
        new Car(FuelType.ELECTRIC,CarCategory.SUV, "YZX394", "MITSUBISHI", "Outlander PHEV", 7, 120.5),
        new Car(FuelType.ELECTRIC,CarCategory.SUV, "CMQ412", "MG", "Marvel R", 8, 122.4),
        new Car(FuelType.ELECTRIC,CarCategory.SUV, "NSF785", "KIA", "EV6", 5, 81.4));
        
        this.customisedInventory = FXCollections.observableArrayList();
        for(Car c: inventory){
            this.customisedInventory.add(c);
        }

        this.rentals = FXCollections.observableArrayList();
        this.rentals.addAll(new Rental(new Customer("Ina", "Song", 14556068), inventory.get(6), 12, true),
        new Rental(new Customer("Jongmin", "Kim", 14579130), inventory.get(14), 9, false),
        new Rental(new Customer("Timothy", "Ling", 12345678), inventory.get(20), 5, true),
        new Rental(new Customer("Hazel", "Kim", 13572468), inventory.get(11), 15, true),
        new Rental(new Customer("Timothy", "Chalamet", 19283746), inventory.get(17), 23, false));      
        inventory.get(6).rent();
        inventory.get(14).rent();
        inventory.get(20).rent();
        inventory.get(11).rent();
        inventory.get(17).rent();
    }

    public ObservableList<Car> inventoryProperty(){
        return inventory;
    }

    public ObservableList<Car> customisedInventoryProperty(){
        return customisedInventory;
    }

    public ObservableList<Rental> rentalsProperty(){
        return rentals;
    }
    
    public void sortBy(Comparator<Car> comparator) {
        if (comparator != null) {
            Collections.sort(customisedInventory, comparator);
        }
    }

    public void filterCars(String fuelType, String category, String status) {
        ObservableList<Car> filteredList = FXCollections.observableArrayList();            
        for (Car car : inventory) {
            boolean matchesFuelType = fuelType.equals("All") || car.getFuelType().toString().equals(fuelType);
            boolean matchesCategory = category.equals("All") || car.getCategory().toString().equals(category);                
            boolean matchesStatus = status.equals("All") || car.getStatus().toString().equals(status);

            if (matchesFuelType && matchesCategory && matchesStatus) {
                filteredList.add(car);
            }
        }
        this.customisedInventory = filteredList;    
    }

    public void addCar(Car c){
        inventory.add(c);
        customisedInventory.add(c);
    }

    public void updateCar(Car c, int index){
        inventory.set(index, c);
    }

    public void removeCar(Car c){
        inventory.remove(c);
        customisedInventory.remove(c);
    }

    public void rentCar(Car c){
        c.rent();
    }

    public void returnCar(Car c, boolean isDamaged){
        c.getReturned(isDamaged);
    }

    public void repairCar(Car c){
        c.repair();
    }

    public void addRental(Rental r){
        rentCar(r.getCar());
        rentals.add(r);
    }

    public void returnRental(Rental r, boolean isDamaged){
        returnCar(r.getCar(), isDamaged);
        rentals.remove(r);
    }

    public ObservableList<Rental> findRentalsByFirstName(String firstName) {
        ObservableList<Rental> filteredRentals = FXCollections.observableArrayList();
        for (Rental rental : rentalsProperty()) {
            if (rental.getCustomer().getFirstName().equalsIgnoreCase(firstName)) {
                filteredRentals.add(rental);
            }
        }
        return filteredRentals;
    }

}


class Car{
    private final SimpleObjectProperty<FuelType> fuelType;
    private final SimpleObjectProperty<CarCategory> category;
    private final SimpleStringProperty licensePlate;
    private final SimpleStringProperty brand;
    private final SimpleStringProperty carModel;
    private final SimpleIntegerProperty seats;
    private final SimpleDoubleProperty pricePerDay;
    private final SimpleObjectProperty<Status> status;

    Car(FuelType fuelType, CarCategory category, String licensePlate, String brand, String model, int seats, double pricePerDay) {
        this.fuelType = new SimpleObjectProperty<>(fuelType);
        this.category = new SimpleObjectProperty<>(category);
        this.licensePlate = new SimpleStringProperty(licensePlate);
        this.brand = new SimpleStringProperty(brand);
        this.carModel = new SimpleStringProperty(model);
        this.seats = new SimpleIntegerProperty(seats);
        this.pricePerDay = new SimpleDoubleProperty(pricePerDay);
        this.status = new SimpleObjectProperty<>(Status.AVAILABLE);        
    }

    public FuelType getFuelType(){
        return fuelType.get();
    }

    public SimpleObjectProperty<FuelType> fuelTypeProperty(){
        return fuelType;
    }

    public CarCategory getCategory() {
        return category.get();
    }

    public SimpleObjectProperty<CarCategory> categoryProperty(){
        return category;
    }

    public String getLicensePlate() {
        return licensePlate.get();
    }

    public SimpleStringProperty licensePlateProperty(){
        return licensePlate;
    }

    public String getBrand() {
        return brand.get();
    }

    public SimpleStringProperty brandProperty(){
        return brand;
    }

    public String getCarModel() {
        return carModel.get();
    }

    public SimpleStringProperty carModelProperty(){
        return carModel;
    }

    public int getSeats() {
        return seats.get();
    }

    public SimpleIntegerProperty seatsProperty(){
        return seats;
    }

    public Status getStatus() {
        return status.get();
    }

    public void setStatus(Status status){
        this.status.set(status);
    }

    public SimpleObjectProperty<Status> statusProperty(){
        return status;
    }

    public double getPricePerDay() {
        return pricePerDay.get();
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay.set(pricePerDay);
    }

    public SimpleDoubleProperty pricePerDayProperty(){
        return pricePerDay;
    }

    public void rent(){
        setStatus(Status.RENTED);
    }
        
    public void getReturned(boolean isDamaged){
        if (isDamaged){
            setStatus(Status.NEEDS_MAINTENANCE);
        }else{
            setStatus(Status.AVAILABLE);
        }
    }
        
    public void repair(){
        setStatus(Status.AVAILABLE);
    }
    
    public String toString() {
        return getBrand() + " / " + getCarModel() + " (" + getLicensePlate() + ")";
    }
}

class Customer {
    private SimpleStringProperty firstName;
    private SimpleStringProperty lastName;
    private SimpleIntegerProperty licenseNumber;

    Customer(String firstName, String lastName, int licenseNumber) {
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.licenseNumber = new SimpleIntegerProperty(licenseNumber);
    }

    SimpleStringProperty firstNameProperty(){
        return firstName;
    }

    String getFirstName() {
        return firstName.get();
    }

    SimpleStringProperty lastNameProperty(){
        return lastName;
    }

    String getLastName() {
        return lastName.get();
    }

    SimpleIntegerProperty licenseNumberProperty(){
        return licenseNumber;
    }

    int getLicenseNumber() {
        return licenseNumber.get();
    }

    public String toString(){
        return  getFirstName() + " " + getLastName() + " (license no. " + getLicenseNumber() + ")";
    }
}


class Rental {
    private SimpleObjectProperty<Customer> customer;
    private SimpleObjectProperty<Car> car;
    private SimpleBooleanProperty isProtectionAdded;
    private SimpleIntegerProperty rentalDays;
    private SimpleDoubleProperty totalPrice;

    Rental(Customer customer, Car car, int rentalDays, boolean isProtectionAdded) {
        this.customer = new SimpleObjectProperty<>(customer);
        this.car = new SimpleObjectProperty<>(car);
        this.rentalDays = new SimpleIntegerProperty(rentalDays);
        this.totalPrice = new SimpleDoubleProperty();
        if (isProtectionAdded){
            this.isProtectionAdded = new SimpleBooleanProperty(true);
            this.totalPrice.bind((car.pricePerDayProperty().add(15)).multiply(rentalDays)); 
        } else{
            this.isProtectionAdded = new SimpleBooleanProperty(false);
            this.totalPrice.bind(car.pricePerDayProperty().multiply(rentalDays)); 
        }
    }

    SimpleObjectProperty<Customer> customerProperty(){
        return customer;
    }

    Customer getCustomer() {
        return customer.get();
    }

    SimpleObjectProperty<Car> carProperty(){
        return car;
    }

    Car getCar(){
        return carProperty().get();
    }

    SimpleBooleanProperty protectionAddedProperty(){
        return isProtectionAdded;
    }

    boolean getProtectionAdded(){
        return isProtectionAdded.get();
    }

    SimpleIntegerProperty rentalDaysProperty(){
        return rentalDays;
    }

    int getRentalDays() {
        return rentalDays.get();
    }

    SimpleDoubleProperty totalPriceProperty(){
        return totalPrice;
    }

    double getTotalPrice(){
        return totalPrice.get();
    }

    double calculateProtectionFee(){
        if(getProtectionAdded()){
            return 15 * getRentalDays();
        } else{
            return 0;
        }
    }

    public String toString(){
        return  "..................................................................................."
                + "\n [Customer] " + getCustomer().toString()
                + "\n [Car] " + getCar().toString()
                + "\n [Rental Days] " + getRentalDays() + " days"
                + "\n ▷ Price per day: $ " + getCar().getPricePerDay()
                + "\n ▷ Protection Fee: $ " + calculateProtectionFee()
                + "\n ▶ Total Price: $" + String.format("%.2f", getTotalPrice())
                + "\n.................................................................................";
    }
}

enum Status { 
    AVAILABLE("Available"), RENTED("Rented"), NEEDS_MAINTENANCE("Needs Maintenance");
    String string;

    private Status(String string){
        this.string = string;
    }

    public String toString(){
        return string;
    }
}

enum CarCategory { 
    SMALL("Small"), MEDIUM("Medium"), LARGE("Large"), SUV("SUV");
    String string;

    private CarCategory(String string){
        this.string = string;
    }

    public String toString(){
        return string;
    }
}

enum FuelType{ 
    ELECTRIC("Electric"), GASOLINE("Gasoline"); 
    String string;

    private FuelType(String string){
        this.string = string;
    }

    public String toString(){
        return string;
    }

}