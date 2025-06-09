import java.util.Comparator;
import java.util.List;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class RentalBusinessView {
    private RentalBusinessController controller; 
    private RentalBusinessModel model; 
    private Stage primaryStage, manageMenuStage, rentMenuStage, returnMenuStage, returnCarStage, repairMenuStage;
    private TableView<Car> carView;
    private TableView<Rental> rentalTableView;
    private VBox view;

    public RentalBusinessView(RentalBusinessController controller, RentalBusinessModel model, Stage primaryStage){
        this.controller = controller;
        this.model = model;
        this.primaryStage = primaryStage;

        createMainMenuPage();
    }

    public Parent asParent(){
        return view;
    }

    private void createMainMenuPage(){
        Label titleLabel = new Label("CAR RENTAL SYSTEM");
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setFont(Font.font(20));

        Button manageMenuBtn = createMenuButton("Manage Cars");
        manageMenuBtn.setOnAction(e -> createManageMenuPage());

        Button rentMenuBtn = createMenuButton("Rent Cars");
        rentMenuBtn.setOnAction(e -> createRentMenuPage());

        Button returnMenuBtn = createMenuButton("Return Cars");
        returnMenuBtn.setOnAction(e -> createReturnMenuPage());

        Button repairMenuBtn = createMenuButton("Repair Cars");
        repairMenuBtn.setOnAction(e -> createRepairMenuPage());

        HBox row1 = new HBox(20, manageMenuBtn, rentMenuBtn);
        row1.setAlignment(Pos.CENTER);
        HBox row2 = new HBox(20, returnMenuBtn, repairMenuBtn);
        row2.setAlignment(Pos.CENTER);

        VBox root = new VBox(20, titleLabel, row1, row2);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        view = new VBox(10, root);
        view.setAlignment(Pos.CENTER);
    }

    private void createManageMenuPage(){
        manageMenuStage = new Stage();
        manageMenuStage.initOwner(primaryStage);
        manageMenuStage.initModality(Modality.APPLICATION_MODAL);
        manageMenuStage.setTitle("Manage Cars");

        Label titleLabel = new Label("CAR INVENTORY MANAGEMENT");
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setFont(Font.font(20));
        titleLabel.setPadding(new Insets(15));

        Button applyFiltersBtn = new Button("Apply filters");
        applyFiltersBtn.setOnAction(e -> createFilterWindow(null));  
        
        Button showAllBtn = new Button("Show all");
        showAllBtn.setOnAction(e -> updateTableView(controller.applyFilters("All", "All", "All")));

        ToggleGroup sortGroup = new ToggleGroup();
        RadioButton ByBrandBtn = new RadioButton("By Brand / Model");
        ByBrandBtn.setToggleGroup(sortGroup);
        ByBrandBtn.setOnAction(e -> updateTableView(controller.handleSortBy(Comparator.comparing(Car::getBrand).thenComparing(Car::getCarModel))));

        RadioButton ByPriceBtn = new RadioButton("By Price");
        ByPriceBtn.setToggleGroup(sortGroup);
        ByPriceBtn.setOnAction(e -> updateTableView(controller.handleSortBy(Comparator.comparing(Car::getPricePerDay))));

        RadioButton BySeatsBtn = new RadioButton("By Seats");
        BySeatsBtn.setToggleGroup(sortGroup);
        BySeatsBtn.setOnAction(e -> updateTableView(controller.handleSortBy(Comparator.comparing(Car::getSeats))));

        HBox filterAndSortRow = new HBox(10, applyFiltersBtn, showAllBtn, new Label("\t\t\t\t\t\t\t\t\tSort: "), ByBrandBtn, ByPriceBtn, BySeatsBtn);
        filterAndSortRow.setPadding(new Insets(0, 0, 0, 10));

        carView = new TableView<>();
        TableColumn<Car, FuelType> fuelCol = new TableColumn<>("Fuel Type");
        fuelCol.setCellValueFactory(cellData -> cellData.getValue().fuelTypeProperty());

        TableColumn<Car, CarCategory> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());

        TableColumn<Car, String> brandCol = new TableColumn<>("Brand");
        brandCol.setCellValueFactory(cellData -> cellData.getValue().brandProperty());

        TableColumn<Car, String> carModelCol = new TableColumn<>("Model");
        carModelCol.setCellValueFactory(cellData -> cellData.getValue().carModelProperty());

        TableColumn<Car, Integer> seatsCol = new TableColumn<>("Seats");
        seatsCol.setCellValueFactory(cellData -> cellData.getValue().seatsProperty().asObject());

        TableColumn<Car, String> priceCol = new TableColumn<>("Price per day");
        priceCol.setCellValueFactory(cellData -> cellData.getValue().pricePerDayProperty().asString("$ %.2f"));

        TableColumn<Car, String> licenseCol = new TableColumn<>("License Plate");
        licenseCol.setCellValueFactory(cellData -> cellData.getValue().licensePlateProperty());

        TableColumn<Car, Status> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        
        carView.getColumns().addAll(fuelCol, categoryCol, brandCol, carModelCol, seatsCol, priceCol, licenseCol, statusCol);
        carView.setItems(controller.applyFilters("All", "All", "All"));

        Label errorMessageLabel = new Label();
        errorMessageLabel.setTextFill(Color.RED);
        Button addCarBtn = new Button("Add car");
        addCarBtn.setOnAction(event -> createAddCarForm());

        Button deleteCarBtn = new Button("Delete car");
        deleteCarBtn.setOnAction(event -> {
            Car selectedCar = carView.getSelectionModel().getSelectedItem();
            if(selectedCar == null){
                errorMessageLabel.setText("Please select a car to delete");
                return;
            }
            createDeleteConfirmationWindow(selectedCar);
            errorMessageLabel.setText("");
        });

        Button updatePriceBtn = new Button("Update price");
        updatePriceBtn.setOnAction(event -> {
            Car selectedCar = carView.getSelectionModel().getSelectedItem();
            if(selectedCar == null){
                errorMessageLabel.setText("Please select a car to update");
                return;
            }
            createUpdatePriceForm(selectedCar);
            errorMessageLabel.setText("");
        });

        HBox manageBtnRow = new HBox(10, addCarBtn, deleteCarBtn, updatePriceBtn, errorMessageLabel);
        manageBtnRow.setAlignment(Pos.TOP_LEFT);
        manageBtnRow.setPadding(new Insets(0, 10, 10, 10));

        VBox root = new VBox(10, titleLabel, filterAndSortRow, carView, manageBtnRow);
        root.setAlignment(Pos.CENTER);
        Scene manageScene = new Scene(root, 700, 500);
        manageMenuStage.setScene(manageScene);
        manageMenuStage.show();
    }

    private void createFilterWindow(Status statusFilter) {
        Stage filterStage = new Stage();
        filterStage.initModality(Modality.APPLICATION_MODAL);
        filterStage.initOwner(manageMenuStage);
        filterStage.setTitle("Apply Filters");

        String currentFuelType = controller.getCurrentFuelTypeFilter();
        String currentCategory = controller.getCurrentCategoryFilter();
        String currentStatus = controller.getCurrentStatusFilter();

        ToggleGroup fuelTypeGroup = new ToggleGroup();
        RadioButton fuelAllBtn = new RadioButton("All");
        fuelAllBtn.setToggleGroup(fuelTypeGroup);
        RadioButton fuelElectricBtn = new RadioButton("Electric");
        fuelElectricBtn.setToggleGroup(fuelTypeGroup);
        RadioButton fuelGasolineBtn = new RadioButton("Gasoline");
        fuelGasolineBtn.setToggleGroup(fuelTypeGroup);

        if(currentFuelType.equals("All")){
            fuelAllBtn.setSelected(true);
        } else if (currentFuelType.equals("Electric")) {
            fuelElectricBtn.setSelected(true);
        } else {
            fuelGasolineBtn.setSelected(true);
        } 

        HBox fuelTypeRow = new HBox(10, new Label("Fuel Type:"), fuelAllBtn, fuelElectricBtn, fuelGasolineBtn);

        ToggleGroup categoryGroup = new ToggleGroup();
        RadioButton categoryAllBtn = new RadioButton("All");
        categoryAllBtn.setToggleGroup(categoryGroup);
        RadioButton categorySmallBtn = new RadioButton("Small");
        categorySmallBtn.setToggleGroup(categoryGroup);
        RadioButton categoryMediumBtn = new RadioButton("Medium");
        categoryMediumBtn.setToggleGroup(categoryGroup);
        RadioButton categoryLargeBtn = new RadioButton("Large");
        categoryLargeBtn.setToggleGroup(categoryGroup);
        RadioButton categorySUVBtn = new RadioButton("SUV");
        categorySUVBtn.setToggleGroup(categoryGroup);

        if(currentCategory.equals("All")){
            categoryAllBtn.setSelected(true);
        } else if (currentCategory.equals("Small")) {
            categorySmallBtn.setSelected(true);
        } else if (currentCategory.equals("Medium")) {
            categoryMediumBtn.setSelected(true);
        } else if (currentCategory.equals("Large")) {
            categoryLargeBtn.setSelected(true);
        } else {
            categorySUVBtn.setSelected(true);
        } 

        HBox categoryRow = new HBox(10, new Label("Category:"), categoryAllBtn, categorySmallBtn, categoryMediumBtn, categoryLargeBtn, categorySUVBtn);

        ToggleGroup statusGroup = new ToggleGroup();
        RadioButton statusAllBtn, statusAvailableBtn, statusRentedBtn, statusMaintenanceBtn;
        HBox statusRow = new HBox();
        if(statusFilter == null){
            statusAllBtn = new RadioButton("All");
            statusAllBtn.setToggleGroup(statusGroup);
            statusAllBtn.setSelected(true);
            statusAvailableBtn = new RadioButton("Available");
            statusAvailableBtn.setToggleGroup(statusGroup);
            statusRentedBtn = new RadioButton("Rented");
            statusRentedBtn.setToggleGroup(statusGroup);
            statusMaintenanceBtn = new RadioButton("Needs Maintenance");
            statusMaintenanceBtn.setToggleGroup(statusGroup);

            if(currentStatus.equals("All")){
                statusAllBtn.setSelected(true);
            } else if (currentStatus.equals("Available")) {
                statusAvailableBtn.setSelected(true);
            } else if (currentStatus.equals("Rented")) {
                statusRentedBtn.setSelected(true);
            } else {
                statusMaintenanceBtn.setSelected(true);
            } 

            statusRow = new HBox(10, new Label("Status:"), statusAllBtn, statusAvailableBtn, statusRentedBtn, statusMaintenanceBtn);
        }

        Button applyBtn = new Button("Apply");
        applyBtn.setOnAction(e -> {
            String selectedFuelType = ((RadioButton) fuelTypeGroup.getSelectedToggle()).getText();
            String selectedCategory = ((RadioButton) categoryGroup.getSelectedToggle()).getText();
            String selectedStatus;
            if (statusFilter == null) {
                selectedStatus = ((RadioButton) statusGroup.getSelectedToggle()).getText();
            } else{
                selectedStatus = statusFilter.toString();
            }
            updateTableView(controller.applyFilters(selectedFuelType, selectedCategory, selectedStatus));
            filterStage.close();
        });

        VBox root;
        if(statusFilter == null){
            root = new VBox(20, fuelTypeRow, categoryRow, statusRow, applyBtn);
        } else{
            root = new VBox(20, fuelTypeRow, categoryRow, applyBtn);
        }

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));
        Scene filterScene = new Scene(root, 400, 200);
        filterStage.setScene(filterScene);
        filterStage.show();
    }

    private void createAddCarForm() {
        Stage addCarStage = new Stage();
        addCarStage.initModality(Modality.APPLICATION_MODAL);
        addCarStage.initOwner(manageMenuStage);
        addCarStage.setTitle("Add Car");

        Label titleLabel = new Label("ADD CAR FORM");
        titleLabel.setFont(Font.font(16));
        titleLabel.setPadding(new Insets(7));
        titleLabel.setAlignment(Pos.CENTER);

        ToggleGroup fuelTypeGroup = new ToggleGroup();
        RadioButton fuelElectricBtn = new RadioButton("Electric");
        fuelElectricBtn.setToggleGroup(fuelTypeGroup);
        RadioButton fuelGasolineBtn = new RadioButton("Gasoline");
        fuelGasolineBtn.setToggleGroup(fuelTypeGroup);
        HBox fuelTypeRow = new HBox(5, new Label("  Fuel Type: "), fuelElectricBtn, fuelGasolineBtn);
        fuelTypeRow.setAlignment(Pos.CENTER_LEFT);

        ToggleGroup categoryGroup = new ToggleGroup();
        RadioButton categorySmallBtn = new RadioButton("Small");
        categorySmallBtn.setToggleGroup(categoryGroup);
        RadioButton categoryMediumBtn = new RadioButton("Medium");
        categoryMediumBtn.setToggleGroup(categoryGroup);
        RadioButton categoryLargeBtn = new RadioButton("Large");
        categoryLargeBtn.setToggleGroup(categoryGroup);
        RadioButton categorySUVBtn = new RadioButton("SUV");
        categorySUVBtn.setToggleGroup(categoryGroup);        
        HBox categoryRow = new HBox(5, new Label("  Category: "), categorySmallBtn, categoryMediumBtn, categoryLargeBtn, categorySUVBtn);
    
        TextField licensePlateField = new TextField();
        licensePlateField.setPromptText("3 Alphabet + 3 Numbers");
        configTextFieldForLicensePlate(licensePlateField);
        HBox licenseRow = new HBox(new Label("  License Plate: "), licensePlateField);

        TextField brandField = new TextField();
        configTextFieldForNames(brandField);
        HBox brandRow = new HBox(new Label("  Brand:\t\t   "), brandField);
    
        TextField modelField = new TextField();
        HBox modelRow = new HBox(new Label("  Model:\t\t   "), modelField);
  
        TextField seatsField = new TextField();
        configTextFieldForInts(seatsField);
        HBox seatsRow = new HBox(new Label("  Seats:\t\t   "), seatsField);
    
        TextField priceField = new TextField();
        priceField.setPromptText("AUD($)");
        configTextFieldForDoubles(priceField);
        HBox priceRow = new HBox(new Label("  Price per Day: "), priceField);
    
        Label errorMessageLabel = new Label();
        errorMessageLabel.setTextFill(Color.RED);
        Button submitBtn = new Button("Submit");
        submitBtn.setOnAction(e -> {
            RadioButton selectedFuelBtn = (RadioButton) fuelTypeGroup.getSelectedToggle();
            RadioButton selectedCategoryBtn = (RadioButton) categoryGroup.getSelectedToggle();
            if (selectedFuelBtn == null || selectedCategoryBtn == null) {
                errorMessageLabel.setText("All fields must be filled out.");
                return;
            }

            FuelType fuelType = null;
            if(selectedFuelBtn.getText().equals("Electric")){
                fuelType = FuelType.ELECTRIC;
            } else {
                fuelType = FuelType.GASOLINE;
            }

            CarCategory category = null;
            if(selectedCategoryBtn.getText().equals("Small")){
                category = CarCategory.SMALL;
            } else if(selectedCategoryBtn.getText().equals("Medium")){
                category = CarCategory.MEDIUM;
            } else if(selectedCategoryBtn.getText().equals("Large")){
                category = CarCategory.LARGE;
            } else {
                category = CarCategory.SUV;
            }

            String licensePlate = licensePlateField.getText().toUpperCase();    
            String brand = brandField.getText().toUpperCase();
            String model = modelField.getText();
            String seatsText = seatsField.getText();
            String priceText = priceField.getText();
            if (licensePlate.isEmpty() || brand.isEmpty() || model.isEmpty() || seatsText.isEmpty() || priceText.isEmpty()){
                errorMessageLabel.setText("All fields must be filled out.");
                return;
            }

            if (licensePlate.length() != 6) {
                errorMessageLabel.setText("License Plate must be 3 alphabets + 3 numbers.");
                return;
            }

            int seats = Integer.parseInt(seatsText);
            double pricePerDay = Double.parseDouble(priceText);
            if (seats > 8) {
                errorMessageLabel.setText("The maximum number of seats is 8.");
                return;
            }
            
            Car newCar = new Car(fuelType, category, licensePlate, brand, model, seats, pricePerDay);
            controller.addCar(newCar);
            updateTableView();
            addCarStage.close();
        });

        HBox submitRow = new HBox(10, errorMessageLabel, submitBtn);
        submitRow.setAlignment(Pos.CENTER_RIGHT);
        submitRow.setPadding(new Insets(0, 2, 0, 0));

        VBox root = new VBox(17, titleLabel, fuelTypeRow, categoryRow, licenseRow, brandRow, modelRow, seatsRow, priceRow, submitRow);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(0, 15, 15, 15));
        Scene addCarScene = new Scene(root, 350, 390);
        addCarStage.setScene(addCarScene);
        addCarStage.show();
    }

    private void createUpdatePriceForm(Car car) {
        Stage updatePriceStage = new Stage();
        updatePriceStage.initModality(Modality.APPLICATION_MODAL);
        updatePriceStage.initOwner(manageMenuStage);
        updatePriceStage.setTitle("Update Price");
    
        Label carInfoLabel = new Label("Selected Car: " + car);    
        TextField priceField = new TextField(String.format("%.2f", car.getPricePerDay()));
        configTextFieldForDoubles(priceField);
        HBox priceRow = new HBox(10, new Label("  Price per Day: "), priceField);
     
        Label errorMessageLabel = new Label();
        errorMessageLabel.setTextFill(Color.RED);
        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            String priceText = priceField.getText();
            if (priceText.isEmpty()) {
                errorMessageLabel.setText("Please enter a price.");
                return;
            }
            double pricePerDay = Double.parseDouble(priceText);
            car.setPricePerDay(pricePerDay);
        
            updateTableView();
            updatePriceStage.close();
        });
        VBox saveBtnBox = new VBox(3, saveBtn, errorMessageLabel);
        saveBtnBox.setAlignment(Pos.CENTER);
        
        VBox root = new VBox(15, carInfoLabel, priceRow, saveBtnBox);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));
        Scene updatePriceScene = new Scene(root, 300, 150);
        updatePriceStage.setScene(updatePriceScene);
        updatePriceStage.show();
    }

    private void createDeleteConfirmationWindow(Car car) {
        Stage confirmStage = new Stage();
        confirmStage.initModality(Modality.APPLICATION_MODAL);
        confirmStage.initOwner(manageMenuStage);
        confirmStage.setTitle("Delete Car");
    
        Label carInfoLabel = new Label("Selected Car: " + car);    
        Label confirmLabel = new Label("Are you sure you want to delete this car?");

        Button confirmBtn = new Button("Yes, Delete");
        confirmBtn.setOnAction(e -> {
            controller.removeCar(car); 
            updateTableView();
            confirmStage.close();
        });
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> confirmStage.close());
        HBox buttonRow = new HBox(10, confirmBtn, cancelBtn);
        buttonRow.setAlignment(Pos.CENTER);
    
        VBox root = new VBox(10, carInfoLabel, confirmLabel, buttonRow);
        root.setAlignment(Pos.CENTER);
        Scene confirmScene = new Scene(root, 300, 150);
        confirmStage.setScene(confirmScene);
        confirmStage.show();
    }

    private void createRentMenuPage(){
        rentMenuStage = new Stage();
        rentMenuStage.initOwner(primaryStage);
        rentMenuStage.initModality(Modality.APPLICATION_MODAL);
        rentMenuStage.setTitle("Rent Cars");
        
        Label titleLabel = new Label("CAR RENTAL FORM");
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setFont(Font.font(20));

        Label step1Label = new Label("■  STEP 1: CUSTOMER INFORMATION");
        step1Label.setAlignment(Pos.CENTER_LEFT);
        CheckBox customerCheckBox = new CheckBox("Is the customer over 21 and holding full license?");
        customerCheckBox.setPadding(new Insets(0,0,0,10));
        customerCheckBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if(wasSelected && !isSelected){
                customerCheckBox.setTextFill(Color.RED);
            }
            if(isSelected){
                customerCheckBox.setTextFill(Color.BLACK);
            }
        });

        TextField firstNameField = new TextField();
        configTextFieldForNames(firstNameField);
        HBox firstNameRow = new HBox(10, new Label("   First name: "), firstNameField);

        TextField lastNameField = new TextField();
        configTextFieldForNames(lastNameField);
        HBox lastNameRow = new HBox(10, new Label("   Last name:  "), lastNameField);

        TextField licenseNumField = new TextField();
        licenseNumField.setPromptText("8 digits");
        configTextFieldForLicenseNumber(licenseNumField);
        HBox licenseNumRow = new HBox(10, new Label("   License no.:"), licenseNumField);
        VBox step1Box = new VBox(10, step1Label, customerCheckBox, firstNameRow, lastNameRow, licenseNumRow);

        Label step2Label = new Label("■  STEP 2: CAR SELECTION");
        step2Label.setAlignment(Pos.CENTER_LEFT);

        SimpleObjectProperty<Car> selectedCar = new SimpleObjectProperty<>();
        TextField selectedCarField = new TextField();
        selectedCarField.setEditable(false);
        Button selectCarBtn = new Button("Select Car");
        selectCarBtn.setOnAction(e -> {
            SimpleObjectProperty<Car> selectedCarProperty = createSelectCarWindow(); 
            selectedCarProperty.addListener((obs, oldCar, newCar) -> {
                if (newCar != null) {
                    selectedCar.set(newCar);
                    selectedCarField.setText(newCar.getBrand() + " " + newCar.getCarModel());
                }
            });
        });
        HBox selectCarRow = new HBox(6, new Label("   Selected car:"), selectedCarField, selectCarBtn);
        VBox step2Box = new VBox(10, step2Label, selectCarRow);

        Label step3Label = new Label("■  STEP 3: RENTAL INFORMATION");
        step3Label.setAlignment(Pos.CENTER_LEFT);
        TextField rentalDaysField = new TextField();
        configTextFieldForInts(rentalDaysField);
        HBox rentalDaysRow = new HBox(6, new Label("   Rental days: "), rentalDaysField);
        
        CheckBox protectionCheckBox = new CheckBox("Include protection option");
        protectionCheckBox.setPadding(new Insets(0,0,0,10));
        Label protectionLabel = new Label();
        protectionLabel.setText("\t  - $15 per day"+
                                "\n\t  - Cover up to $10,000 fee for any damage or theft");
        protectionLabel.setTextFill(Color.BLUE);
        VBox protectionBox = new VBox(protectionCheckBox, protectionLabel);
        VBox step3Box = new VBox(10, step3Label, rentalDaysRow, protectionBox);

        Label pricePerDayLabel = new Label("- Price per day: $0");
        Label protectionPriceLabel = new Label("- Protection price: $0");
        Label totalPriceLabel = new Label("- Total price: $0");
        selectedCar.addListener((obs, oldCar, newCar) -> updatePriceLabels(newCar, rentalDaysField, protectionCheckBox, pricePerDayLabel, protectionPriceLabel, totalPriceLabel));
        rentalDaysField.textProperty().addListener((obs, oldText, newText) -> updatePriceLabels(selectedCar.get(), rentalDaysField, protectionCheckBox, pricePerDayLabel, protectionPriceLabel, totalPriceLabel));
        protectionCheckBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> updatePriceLabels(selectedCar.get(), rentalDaysField, protectionCheckBox, pricePerDayLabel, protectionPriceLabel, totalPriceLabel));

        Label errorMessageLabel = new Label();
        errorMessageLabel.setTextFill(Color.RED);
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> rentMenuStage.close());
        Button submitBtn = new Button("Submit");
        submitBtn.setAlignment(Pos.CENTER_RIGHT);
        submitBtn.setOnAction(e -> {
            if (!customerCheckBox.isSelected()) {
                errorMessageLabel.setText("Customer must be 21+ and holding a full license.\n");
                customerCheckBox.setTextFill(Color.RED);
                return;
            } else{
                customerCheckBox.setTextFill(Color.BLACK);
            }
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String licenseNumText = licenseNumField.getText();
            Car car = selectedCar.get();
            String rentalDaysText = rentalDaysField.getText();
            if (firstName.isEmpty() || lastName.isEmpty() || licenseNumText.isEmpty() || car == null || rentalDaysText.isEmpty()){
                errorMessageLabel.setText("All fields must be filled out.");
                return;
            }

            if(licenseNumText.length() != 8){
                errorMessageLabel.setText("License number must be 8 digits");
                return;
            }
            
            int licenseNumber = Integer.parseInt(licenseNumText);
            int rentalDays = Integer.parseInt(rentalDaysText);
            boolean isProtectionAdded = protectionCheckBox.isSelected();
            
            if (rentalDays > 31){
                errorMessageLabel.setText("The maximum rental period is 31 days.");
                return;
            } else if (rentalDays < 1){
                errorMessageLabel.setText("The minumum rental period is 1 day.");
                return;
            }
        
            Customer customer = new Customer(firstName, lastName, licenseNumber);
            Rental rental = new Rental(customer, car, rentalDays, isProtectionAdded);
            createRentalConfirmationWindow(rental);
        });
        HBox submitBtnRow = new HBox(10, errorMessageLabel, cancelBtn, submitBtn);
        submitBtnRow.setAlignment(Pos.BOTTOM_RIGHT);
        submitBtnRow.setPadding(new Insets(0,10,0,0));
        VBox priceAndSubmitBox = new VBox(5, pricePerDayLabel, protectionPriceLabel, totalPriceLabel, submitBtnRow);

        VBox root = new VBox(35, titleLabel, step1Box, step2Box, step3Box, priceAndSubmitBox);
        root.setPadding(new Insets(0, 0, 0, 20));
        root.setAlignment(Pos.CENTER);
        Scene rentScene = new Scene(root, 410, 550);
        rentMenuStage.setScene(rentScene);
        rentMenuStage.show();
    }

    private SimpleObjectProperty<Car> createSelectCarWindow(){
        Stage selectCarStage = new Stage();
        selectCarStage.initOwner(rentMenuStage);
        selectCarStage.initModality(Modality.APPLICATION_MODAL);
        selectCarStage.setTitle("Select Car");
        SimpleObjectProperty<Car> selectedCarProperty = new SimpleObjectProperty<>();

        Button applyFiltersBtn = new Button("Apply filters");
        applyFiltersBtn.setOnAction(e -> createFilterWindow(Status.AVAILABLE));  
        
        Button showAllBtn = new Button("Show all");
        showAllBtn.setOnAction(e -> updateTableView(controller.applyFilters("All", "All", "All")));

        ToggleGroup sortGroup = new ToggleGroup();
        RadioButton ByBrandBtn = new RadioButton("By Brand / Model");
        ByBrandBtn.setToggleGroup(sortGroup);
        ByBrandBtn.setOnAction(e -> updateTableView(controller.handleSortBy(Comparator.comparing(Car::getBrand).thenComparing(Car::getCarModel))));

        RadioButton ByPriceBtn = new RadioButton("By Price");
        ByPriceBtn.setToggleGroup(sortGroup);
        ByPriceBtn.setOnAction(e -> updateTableView(controller.handleSortBy(Comparator.comparing(Car::getPricePerDay))));

        RadioButton BySeatsBtn = new RadioButton("By Seats");
        BySeatsBtn.setToggleGroup(sortGroup);
        BySeatsBtn.setOnAction(e -> updateTableView(controller.handleSortBy(Comparator.comparing(Car::getSeats))));

        
        HBox filterAndSortRow = new HBox(10, applyFiltersBtn, showAllBtn, new Label("\t\t\tSort: "), ByBrandBtn, ByPriceBtn, BySeatsBtn);
        filterAndSortRow.setPadding(new Insets(0, 0, 0, 10));

        this.carView = new TableView<>();
        TableColumn<Car, FuelType> fuelCol = new TableColumn<>("Fuel Type");
        fuelCol.setCellValueFactory(cellData -> cellData.getValue().fuelTypeProperty());

        TableColumn<Car, CarCategory> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());

        TableColumn<Car, String> brandCol = new TableColumn<>("Brand");
        brandCol.setCellValueFactory(cellData -> cellData.getValue().brandProperty());

        TableColumn<Car, String> carModelCol = new TableColumn<>("Model");
        carModelCol.setCellValueFactory(cellData -> cellData.getValue().carModelProperty());

        TableColumn<Car, Integer> seatsCol = new TableColumn<>("Seats");
        seatsCol.setCellValueFactory(cellData -> cellData.getValue().seatsProperty().asObject());

        TableColumn<Car, String> priceCol = new TableColumn<>("Price per day");
        priceCol.setCellValueFactory(cellData -> cellData.getValue().pricePerDayProperty().asString("$ %.2f"));

        TableColumn<Car, String> licenseCol = new TableColumn<>("License Plate");
        licenseCol.setCellValueFactory(cellData -> cellData.getValue().licensePlateProperty());
        
        carView.getColumns().addAll(fuelCol, categoryCol, brandCol, carModelCol, seatsCol, priceCol, licenseCol);
        carView.setItems(controller.applyFilters("All", "All", "All"));

        Label errorMessageLabel = new Label();
        errorMessageLabel.setTextFill(Color.RED);
        Button selectBtn = new Button("Select");
        selectBtn.setOnAction(event -> {
            Car selectedCar = carView.getSelectionModel().getSelectedItem();
            if(selectedCar == null){
                errorMessageLabel.setText("Please select a car to continue");
                return;
            } else {
                selectedCarProperty.set(selectedCar); 
                selectCarStage.close();
            }
        });

        HBox selectBtnRow = new HBox(10, selectBtn, errorMessageLabel);
        selectBtnRow.setAlignment(Pos.CENTER);

        VBox root = new VBox(10, filterAndSortRow, carView, selectBtnRow);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(15, 0, 15, 0));
        Scene selectCarScene = new Scene(root, 570, 450);
        selectCarStage.setScene(selectCarScene);
        selectCarStage.show();
        
        return selectedCarProperty;
    }

    private void createRentalConfirmationWindow(Rental rental){
        Stage confirmStage = new Stage();
        confirmStage.initModality(Modality.APPLICATION_MODAL);
        confirmStage.initOwner(rentMenuStage);
        confirmStage.setTitle("Confirm Rental");
    
        Label carInfoLabel = new Label(rental.toString());    
        Label confirmLabel = new Label("Do you confirm this rental?");
        Button confirmBtn = new Button("Yes, Confirm");
        confirmBtn.setOnAction(e -> {
            controller.addRental(rental);
            confirmStage.close();
            rentMenuStage.close();
            createSuccessfulRentalWindow();
        });
    
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> confirmStage.close());
    
        HBox buttonRow = new HBox(10, confirmBtn, cancelBtn);
        buttonRow.setAlignment(Pos.CENTER);
    
        VBox root = new VBox(10, carInfoLabel, confirmLabel, buttonRow);
        root.setAlignment(Pos.CENTER);
        Scene confirmScene = new Scene(root, 400, 250);
        confirmStage.setScene(confirmScene);
        confirmStage.show();
    }

    private void createSuccessfulRentalWindow() {
        Stage successStage = new Stage();
        successStage.initModality(Modality.APPLICATION_MODAL);
        successStage.initOwner(primaryStage);
        successStage.setTitle("Rental Successful");
    
        Label successMarkLabel = new Label("✓");
        successMarkLabel.setTextFill(Color.GREEN);
        Label successLabel = new Label(" The rental is successfully added to the system");
        HBox successRow = new HBox(successMarkLabel, successLabel);
        
        Button viewRentalsBtn = new Button("View Rental List");
        viewRentalsBtn.setOnAction(e -> {
            successStage.close();
            createReturnMenuPage();  
        });
    
        Button backToMainMenuBtn = new Button("Back to Main Menu");
        backToMainMenuBtn.setOnAction(e -> {
            successStage.close();
            primaryStage.show(); 
        });
    
        HBox buttonRow = new HBox(10, viewRentalsBtn, backToMainMenuBtn);
        buttonRow.setAlignment(Pos.CENTER);
    
        VBox root = new VBox(10, successRow, buttonRow);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        Scene successScene = new Scene(root, 300, 150);
        successStage.setScene(successScene);
        successStage.show();
    }

    private void createReturnMenuPage(){
        returnMenuStage = new Stage();
        returnMenuStage.initOwner(primaryStage);
        returnMenuStage.initModality(Modality.APPLICATION_MODAL);
        returnMenuStage.setTitle("Rental records");

        Label titleLabel = new Label("RENTAL RECORDS");
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setFont(Font.font(20));

        Button searchRentalBtn = new Button("Search Rental");
        searchRentalBtn.setOnAction(e -> createSearchRentalWindow());
        HBox searchRow = new HBox(searchRentalBtn);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        rentalTableView = new TableView<>();
        TableColumn<Rental, String> customerNameCol = new TableColumn<>("Customer");
        customerNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getCustomer().getFirstName() + " " + cellData.getValue().getCustomer().getLastName()));

        TableColumn<Rental, Integer> licenseNumCol = new TableColumn<>("License no.");
        licenseNumCol.setCellValueFactory(cellData -> cellData.getValue().getCustomer().licenseNumberProperty().asObject());

        TableColumn<Rental, String> carCol = new TableColumn<>("Car");
        carCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCar().toString()));

        TableColumn<Rental, String> pricePerDayCol = new TableColumn<>("Daily Price");
        pricePerDayCol.setCellValueFactory(cellData -> cellData.getValue().getCar().pricePerDayProperty().asString("$ %.2f"));

        TableColumn<Rental, Integer> rentalDaysCol = new TableColumn<>("Duration");
        rentalDaysCol.setCellValueFactory(cellData -> cellData.getValue().rentalDaysProperty().asObject());

        TableColumn<Rental, String> totalPriceCol = new TableColumn<>("Total Price");
        totalPriceCol.setCellValueFactory(cellData -> cellData.getValue().totalPriceProperty().asString("$ %.2f"));

        TableColumn<Rental, String> protectionAddedCol = new TableColumn<>("Protection");
        protectionAddedCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getProtectionAdded()){
                return new SimpleStringProperty("O");
            } else{
                return new SimpleStringProperty("X");
            }
        });

        Button returnCarBtn = new Button("Return Car");
        Label errorMessageLabel = new Label();
        errorMessageLabel.setTextFill(Color.RED);
        returnCarBtn.setOnAction(event -> {
            Rental selectedRental = rentalTableView.getSelectionModel().getSelectedItem();
            if(selectedRental == null){
                errorMessageLabel.setText("Please select a rental to return");
                return;
            }
            createReturnCarForm(selectedRental);
            errorMessageLabel.setText("");
        });

        HBox returnCarRow = new HBox(10, returnCarBtn, errorMessageLabel);
        returnCarRow.setAlignment(Pos.CENTER_LEFT);

        rentalTableView.getColumns().addAll(customerNameCol, licenseNumCol, carCol, pricePerDayCol, rentalDaysCol, totalPriceCol, protectionAddedCol);
        rentalTableView.setItems(model.rentalsProperty());

        VBox root = new VBox(10, titleLabel, searchRow, rentalTableView, returnCarRow);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 700, 400);
        returnMenuStage.setScene(scene);
        returnMenuStage.show();
    }

    private void createSearchRentalWindow() {
        Stage searchStage = new Stage();
        searchStage.initModality(Modality.APPLICATION_MODAL);
        searchStage.initOwner(returnMenuStage);
        searchStage.setTitle("Search Rental");

        Label firstNameLabel = new Label("First Name:");
        TextField firstNameField = new TextField();
        HBox searchRow = new HBox(10, firstNameLabel, firstNameField);
        searchRow.setAlignment(Pos.CENTER);

        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> {
            String firstName = firstNameField.getText();
            ObservableList<Rental> rentals = controller.getRentalsByFirstName(firstName);
            if (rentals.isEmpty()) {
                Label errorMessageLabel = new Label("No rental records found for the given first name.");
                errorMessageLabel.setTextFill(Color.RED);
                HBox errorRow = new HBox(errorMessageLabel);
                errorRow.setAlignment(Pos.CENTER);
                VBox errorRoot = new VBox(10, errorRow);
                errorRoot.setAlignment(Pos.CENTER);
                Scene errorScene = new Scene(errorRoot, 300, 150);
                searchStage.setScene(errorScene);
            } else {
                rentalTableView.setItems(rentals);
                searchStage.close();
            }
        });

        VBox root = new VBox(10, searchRow, searchBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        Scene searchScene = new Scene(root, 300, 150);
        searchStage.setScene(searchScene);
        searchStage.show();
    }

    private void createReturnCarForm(Rental rental){
        returnCarStage = new Stage();
        returnCarStage.initModality(Modality.APPLICATION_MODAL);
        returnCarStage.initOwner(returnMenuStage);
        returnCarStage.setTitle("Return Car");

        SimpleDoubleProperty pricePerDayProperty = rental.getCar().pricePerDayProperty();
        SimpleIntegerProperty lateDaysProperty = new SimpleIntegerProperty(0);
        SimpleDoubleProperty lateFeeProperty = new SimpleDoubleProperty();
        lateFeeProperty.bind(lateDaysProperty.multiply(pricePerDayProperty.multiply(1.2)));
        SimpleBooleanProperty isDamaged = new SimpleBooleanProperty();
        SimpleDoubleProperty damageFeeProperty = new SimpleDoubleProperty(0);
        SimpleDoubleProperty totalFeeProperty = new SimpleDoubleProperty();
        if(rental.getProtectionAdded()){
            totalFeeProperty.bind(lateFeeProperty);
        } else{
            totalFeeProperty.bind(lateFeeProperty.add(damageFeeProperty));
        }
        Label damageCoverageLabel = new Label();
        damageCoverageLabel.setTextFill(Color.RED);

        Label titleLabel = new Label("RETURN CAR FORM");
        titleLabel.setFont(Font.font(20));

        Label rentalInfoLabel = new Label(rental.toString());
        Label step1Label= new Label("■  STEP 1: LATE FEE");
        TextField lateDaysField = new TextField();
        lateDaysField.textProperty().bind(lateDaysProperty.asString("%d  days"));
        lateDaysField.setEditable(false);
        Button increaseBtn = new Button("▲");
        increaseBtn.setOnAction(event -> {
            lateDaysProperty.set(lateDaysProperty.get() + 1);
        });
        Button decreaseBtn = new Button("▼");
        decreaseBtn.setOnAction(event -> {
            if(lateDaysProperty.get() - 1 >= 0){
                lateDaysProperty.set(lateDaysProperty.get() - 1);
            }
        });
        HBox lateDaysRow = new HBox(new Label("   Late Days:  "), lateDaysField, increaseBtn, decreaseBtn);
        VBox step1Box = new VBox(10, step1Label, lateDaysRow);

        Label step2Label = new Label("■  STEP 2: DAMAGE FEE");
        Label instructionLabel = new Label("   Select the damage level: ");
        ToggleGroup damageGroup = new ToggleGroup();
        RadioButton noDamageBtn = new RadioButton("1. No Damage");
        noDamageBtn.setToggleGroup(damageGroup);
        noDamageBtn.setSelected(true);
        noDamageBtn.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected){
                isDamaged.set(false);
                damageFeeProperty.set(0);
                damageCoverageLabel.setText("");;

            }
        });
        RadioButton minorDamageBtn = new RadioButton("2. Minor Damage - small scratches or dents");
        minorDamageBtn.setToggleGroup(damageGroup);
        minorDamageBtn.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected){
                damageFeeProperty.set(pricePerDayProperty.get() * 1.5);
                isDamaged.set(true);
                if(rental.getProtectionAdded()){
                    damageCoverageLabel.setText("(Covered by protection)");
                }
            }
        });
        RadioButton moderateDamageBtn = new RadioButton("3. Moderate Damage - larger dents, broken mirrors");
        moderateDamageBtn.setToggleGroup(damageGroup);
        moderateDamageBtn.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected){
                damageFeeProperty.set(pricePerDayProperty.get() * 3.5);
                isDamaged.set(true);
                if(rental.getProtectionAdded()){
                    damageCoverageLabel.setText("(Covered by protection)");
                }
            }
        });
        RadioButton severeDamageBtn = new RadioButton("4. Severe Damgage - major bodywork");
        severeDamageBtn.setToggleGroup(damageGroup);
        severeDamageBtn.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected){
                damageFeeProperty.set(pricePerDayProperty.get() * 5.5);
                isDamaged.set(true);
                if(rental.getProtectionAdded()){
                    damageCoverageLabel.setText("(Covered by protection)");
                }
            }
        });
        VBox damageBtnBox = new VBox(10, noDamageBtn, minorDamageBtn, moderateDamageBtn, severeDamageBtn);
        damageBtnBox.setPadding(new Insets(0,0,0,15));
        VBox step2Box = new VBox(10, step2Label, instructionLabel, damageBtnBox);

        Label lateFeeLabel = new Label();
        lateFeeLabel.textProperty().bind(lateFeeProperty.asString("- Late fee: $%.2f"));
        Label damageFeeLabel = new Label();
        damageFeeLabel.textProperty().bind(damageFeeProperty.asString("- Damage fee: $%.2f"));
        HBox damageFeeBox = new HBox(5, damageFeeLabel, damageCoverageLabel);
        Label totalFeeLabel = new Label();
        totalFeeLabel.textProperty().bind(totalFeeProperty.asString("- Total fee: $%.2f"));
        VBox feeCalculationBox = new VBox(5, lateFeeLabel, damageFeeBox, totalFeeLabel);
        feeCalculationBox.setAlignment(Pos.BOTTOM_LEFT);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> returnCarStage.close());
        Button submitBtn = new Button("Submit");
        submitBtn.setAlignment(Pos.CENTER_RIGHT);
        submitBtn.setOnAction(e -> {
           createReturnConfirmationWindow(feeCalculationBox, rental, isDamaged.get());
        });

        HBox submitBtnRow = new HBox(10, cancelBtn, submitBtn);
        submitBtnRow.setAlignment(Pos.CENTER_RIGHT);
        submitBtnRow.setPadding(new Insets(0,10,0,0));

        VBox root = new VBox(25, titleLabel, rentalInfoLabel, step1Box, step2Box, feeCalculationBox, submitBtnRow);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        Scene returnCarScene = new Scene(root, 430, 600);
        returnCarStage.setScene(returnCarScene);
        returnCarStage.show();
    }

    private void createReturnConfirmationWindow(VBox feeCalculationBox, Rental rental, boolean isDamaged){
        Stage confirmStage = new Stage();
        confirmStage.initModality(Modality.APPLICATION_MODAL);
        confirmStage.initOwner(returnCarStage);
        confirmStage.setTitle("Confirm Return");
        
        Label lineLabel1 = new Label("-------------------------------------------------------------");
        feeCalculationBox.setPadding(new Insets(0,0,0,50));
        Label lineLabel2 = new Label("-------------------------------------------------------------");

        Label confirmLabel = new Label("Do you confirm this return?");
        Button confirmBtn = new Button("Yes, Confirm");
        confirmBtn.setOnAction(e -> {
            controller.returnRental(rental, isDamaged);
            returnCarStage.close();
            confirmStage.close();
            createSuccessfulReturnWindow();
        });
    
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> confirmStage.close());
    
        HBox buttonRow = new HBox(10, confirmBtn, cancelButton);
        buttonRow.setAlignment(Pos.CENTER);
    
        VBox root = new VBox(10, lineLabel1, feeCalculationBox, lineLabel2, confirmLabel, buttonRow);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));
        Scene confirmScene = new Scene(root, 350, 200);
        confirmStage.setScene(confirmScene);
        confirmStage.show();
    }

    private void createSuccessfulReturnWindow() {
        Stage successStage = new Stage();
        successStage.initModality(Modality.APPLICATION_MODAL);
        successStage.initOwner(primaryStage);
        successStage.setTitle("Return Successful");
    
        Label successMarkLabel = new Label("✓");
        successMarkLabel.setTextFill(Color.GREEN);
        Label successLabel = new Label("  The rental is successfully returned!");
        HBox successRow = new HBox(successMarkLabel, successLabel);
        successRow.setAlignment(Pos.CENTER);

        Button carRepairBtn = new Button("Proceed to Car Repair");
        carRepairBtn.setOnAction(e -> {
            returnMenuStage.close();
            successStage.close();
            createRepairMenuPage();
        });
    
        Button backToMainMenuBtn = new Button("Back to Main Menu");
        backToMainMenuBtn.setOnAction(e -> {
            returnMenuStage.close();
            successStage.close();
            primaryStage.show(); 
        });
    
        HBox buttonRow = new HBox(10, carRepairBtn, backToMainMenuBtn);
        buttonRow.setAlignment(Pos.CENTER);
    
        VBox root = new VBox(10, successRow, buttonRow);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        Scene successScene = new Scene(root, 300, 150);
        successStage.setScene(successScene);
        successStage.show();
    }

    private void createRepairMenuPage(){
        repairMenuStage = new Stage();
        repairMenuStage.initOwner(primaryStage);
        repairMenuStage.initModality(Modality.APPLICATION_MODAL);
        repairMenuStage.setTitle("Repair menu");

        ObservableList<Car> maintenanceCars = controller.applyFilters("All", "All", "Needs Maintenance");

        if (maintenanceCars.isEmpty()) {
            Label noCarsLabel = new Label("There are currently no cars requiring maintenance.");
            noCarsLabel.setAlignment(Pos.CENTER);
            noCarsLabel.setFont(Font.font(14));

            Button backToMainMenuBtn = new Button("Back to Main Menu");
            backToMainMenuBtn.setOnAction(e -> {
                repairMenuStage.close();
                primaryStage.show();
            });
            
            VBox root = new VBox(20, noCarsLabel, backToMainMenuBtn);
            root.setAlignment(Pos.CENTER);
            root.setPadding(new Insets(30));
            
            Scene scene = new Scene(root, 370, 150);
            repairMenuStage.setScene(scene);
        } else {
            Label titleLabel = new Label("CARS REQUIRING MAINTENANCE");
            titleLabel.setAlignment(Pos.CENTER);
            titleLabel.setFont(Font.font(20));

            carView = new TableView<>();
            TableColumn<Car, FuelType> fuelCol = new TableColumn<>("Fuel Type");
            fuelCol.setCellValueFactory(cellData -> cellData.getValue().fuelTypeProperty());

            TableColumn<Car, CarCategory> categoryCol = new TableColumn<>("Category");
            categoryCol.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());

            TableColumn<Car, String> brandCol = new TableColumn<>("Brand");
            brandCol.setCellValueFactory(cellData -> cellData.getValue().brandProperty());

            TableColumn<Car, String> carModelCol = new TableColumn<>("Model");
            carModelCol.setCellValueFactory(cellData -> cellData.getValue().carModelProperty());

            TableColumn<Car, Integer> seatsCol = new TableColumn<>("Seats");
            seatsCol.setCellValueFactory(cellData -> cellData.getValue().seatsProperty().asObject());

            TableColumn<Car, String> priceCol = new TableColumn<>("Price per day");
            priceCol.setCellValueFactory(cellData -> cellData.getValue().pricePerDayProperty().asString("$ %.2f"));

            TableColumn<Car, String> licenseCol = new TableColumn<>("License Plate");
            licenseCol.setCellValueFactory(cellData -> cellData.getValue().licensePlateProperty());
            
            carView.getColumns().addAll(fuelCol, categoryCol, brandCol, carModelCol, seatsCol, priceCol, licenseCol);
            carView.setItems(controller.applyFilters("All", "All", "Needs Maintenance"));

            Button repairCarBtn = new Button("Repair Car");
            Label errorMessageLabel = new Label();
            errorMessageLabel.setTextFill(Color.RED);
            repairCarBtn.setOnAction(event -> {
                Car selectedCar = carView.getSelectionModel().getSelectedItem();
                if(selectedCar == null){
                    errorMessageLabel.setText("Please select a car to repair");
                    return;
                }
                createRepairConfirmationWindow(selectedCar);
                errorMessageLabel.setText("");
            });

            HBox repairCarRow = new HBox(10, repairCarBtn, errorMessageLabel);
            repairCarRow.setAlignment(Pos.CENTER_LEFT);

            VBox root = new VBox(10, titleLabel, carView, repairCarRow);
            root.setPadding(new Insets(20));
            root.setAlignment(Pos.CENTER);
            Scene scene = new Scene(root, 530, 350);
            repairMenuStage.setScene(scene);
        }
        repairMenuStage.show();
    }

    private void createRepairConfirmationWindow(Car car){
        Stage confirmStage = new Stage();
        confirmStage.initModality(Modality.APPLICATION_MODAL);
        confirmStage.initOwner(repairMenuStage);
        confirmStage.setTitle("Repair Car");
    
        Label carInfoLabel = new Label("Selected Car: " + car);    
        Label confirmLabel = new Label("Do you want to proceed with the repair of this car?");
        Button confirmBtn = new Button("Yes, proceed");
        confirmBtn.setOnAction(e -> {
            controller.repairCar(car); 
            updateTableView();
            confirmStage.close();
            createSuccessfulRepairWindow();
        });
    
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> confirmStage.close());
    
        HBox buttonRow = new HBox(10, confirmBtn, cancelBtn);
        buttonRow.setAlignment(Pos.CENTER);
    
        VBox root = new VBox(10, carInfoLabel, confirmLabel, buttonRow);
        root.setAlignment(Pos.CENTER);
        Scene confirmScene = new Scene(root, 300, 150);
        confirmStage.setScene(confirmScene);
        confirmStage.show();
    }

    private void createSuccessfulRepairWindow() {
        Stage successStage = new Stage();
        successStage.initModality(Modality.APPLICATION_MODAL);
        successStage.initOwner(primaryStage);
        successStage.setTitle("Repair Successful");
    
        Label successMarkLabel = new Label("✓");
        successMarkLabel.setTextFill(Color.GREEN);
        Label successLabel = new Label("  The car is successfully repaired and now available!");
        HBox successRow = new HBox(successMarkLabel, successLabel);
        successRow.setAlignment(Pos.CENTER);

        Button continueRepairBtn = new Button("Continue Repair");
        continueRepairBtn.setOnAction(e -> {
            successStage.close();
            repairMenuStage.close();
            createRepairMenuPage(); 
        });
    
        Button backToMainMenuBtn = new Button("Back to Main Menu");
        backToMainMenuBtn.setOnAction(e -> {
            successStage.close();
            repairMenuStage.close();
            primaryStage.show(); 
        });
    
        HBox buttonRow = new HBox(10, continueRepairBtn, backToMainMenuBtn);
        buttonRow.setAlignment(Pos.CENTER);
    
        VBox root = new VBox(10, successRow, buttonRow);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        Scene successScene = new Scene(root, 320, 150);
        successStage.setScene(successScene);
        successStage.show();
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setMinSize(120, 60);
        button.setFont(Font.font(14)); 
        return button;
    }

    public void updateTableView(List<Car> cars) {
        ObservableList<Car> carObservableList = FXCollections.observableArrayList(cars);
        carView.setItems(carObservableList);
    }

    public void updateTableView(){
        carView.setItems(model.customisedInventoryProperty());
        updateTableView(controller.applyCurrentFilters());
    }

    private void updatePriceLabels(Car car, TextField rentalDaysField, CheckBox protectionCheckBox, Label pricePerDayLabel, Label protectionPriceLabel, Label totalPriceLabel) {    
        if (car == null){
            return;
        }
        double pricePerDay, protectionPrice, totalPrice;
        int rentalDays;

        pricePerDay = car.getPricePerDay();

        if(rentalDaysField.getText().isEmpty()){
            rentalDays = 0; 
        } else{
            rentalDays = Integer.parseInt(rentalDaysField.getText());
        }

        if(protectionCheckBox.isSelected()){
            protectionPrice = 15 * rentalDays;
        } else{
            protectionPrice = 0;
        }
        
        totalPrice = (pricePerDay * rentalDays) + protectionPrice;
    
        pricePerDayLabel.setText(String.format("- Price per day: $%.2f", pricePerDay));
        protectionPriceLabel.setText(String.format("- Protection price: $%.2f", protectionPrice));
        totalPriceLabel.setText(String.format("- Total price: $%.2f", totalPrice));
    }

    private void configTextFieldForLicensePlate(TextField field) {
        field.setTextFormatter(new TextFormatter<String>((Change c) -> {
            String newText = c.getControlNewText().toUpperCase();
            if (newText.matches("[A-Z]{3}\\d{0,3}") || newText.matches("[A-Z]{0,3}")) {
                return c;
            }
            return null;
        }));
    }

    private void configTextFieldForLicenseNumber(TextField field) {
        field.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,8}")) {  
                return change;
            } 
            return null; 
        }));
    }

    private void configTextFieldForNames(TextField field) {
        field.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[a-zA-Z]*")) { 
                return change;
            }
            return null;  
        }));
    }

    private void configTextFieldForDoubles(TextField field){
        field.setTextFormatter(new TextFormatter<Integer> ((Change c) -> {
            if ( c.getControlNewText().isEmpty() ||  c.getControlNewText().matches("\\d*\\.?\\d*")) {
                return c;
            }
            return null;
        }));        
    }

    private void configTextFieldForInts(TextField field) {
        field.setTextFormatter(new TextFormatter<Integer>((Change c) -> {
            if (c.getControlNewText().matches("\\d*")) {
                return c;
            }
            return null;
        }));
    }    
}
