import java.util.Comparator;
import javafx.collections.ObservableList;

public class RentalBusinessController {
    private RentalBusinessModel model;
    private String currentFuelTypeFilter = "All";
    private String currentCategoryFilter = "All";
    private String currentStatusFilter = "All";
    private Comparator<Car> currentComparator;

    public RentalBusinessController(RentalBusinessModel model){
        this.model = model;
        this.currentComparator = null;
    }

    public void addCar(Car c){
        this.model.addCar(c);
    }

    public void removeCar(Car c){
        this.model.removeCar(c);
    }

    public ObservableList<Car> handleSortBy(Comparator<Car> comparator) {
        currentComparator = comparator;
        model.sortBy(comparator);
        return model.customisedInventoryProperty();
    }

    public ObservableList<Car> applyFilters(String fuelType, String category, String status) {
        this.currentFuelTypeFilter = fuelType;
        this.currentCategoryFilter = category;
        this.currentStatusFilter = status;

        model.filterCars(fuelType, category, status);
        model.sortBy(currentComparator);

        return model.customisedInventoryProperty();
    }

    public ObservableList<Car> applyCurrentFilters(){
        model.filterCars(currentFuelTypeFilter, currentCategoryFilter, currentStatusFilter);
        model.sortBy(currentComparator);

        return model.customisedInventoryProperty();
    }

    public String getCurrentFuelTypeFilter() {
        return currentFuelTypeFilter;
    }

    public String getCurrentCategoryFilter() {
        return currentCategoryFilter;
    }

    public String getCurrentStatusFilter() {
        return currentStatusFilter;
    }

    public void addRental(Rental r){
        model.addRental(r);
    }

    public void returnRental(Rental r, boolean isDamaged){
        model.returnRental(r, isDamaged);
    }

    public void repairCar(Car c){
        model.repairCar(c);
    }

    public ObservableList<Rental> getRentalsByFirstName(String firstName) {
        return model.findRentalsByFirstName(firstName);
    }
}
