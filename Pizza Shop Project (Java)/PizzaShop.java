import java.util.ArrayList;
import java.util.HashMap;

public class PizzaShop {
    public static void main(String[] args) {
        PizzaShop pizzashop = new PizzaShop();

        while(true){
            System.out.println("\n\n-------------------------------------------------------");
            System.out.println("                      PIZZA SHOP                       ");
            System.out.println("-------------------------------------------------------");
            System.out.println("                  1. Proceed Orders                    ");
            System.out.println("                  2. Manage Orders                     ");
            System.out.println("                  3. Close System                      ");
            System.out.println("-------------------------------------------------------");

            System.out.print("Select the option(1 - 3): ");
            int choice = In.nextInt();
            if(choice == 1){
                pizzashop.proceedOrder();
            } else if(choice == 2){
                pizzashop.manageOrders();
            } else if (choice == 3){
                System.out.println("System closed. See You Next Time!!");
                break;
            } else {
                System.out.println("INVALID CHOICE: Please select a valid number!");
            }
        }
    }

    private HashMap<Integer, Pizza> pizzaMenu;
    private ArrayList<DineInOrder> dineInOrders;
    private ArrayList<DeliveryOrder> deliveryOrders;

    public PizzaShop() {
        pizzaMenu = new HashMap<>();
        pizzaMenu.put(1, new Pizza("Pepperoni Cheese Pizza", 15.5));
        pizzaMenu.put(2, new Pizza("Super Supreme Pizza", 17.5));
        pizzaMenu.put(3, new Pizza("BBQ Chicken Pizza", 19.5));
        pizzaMenu.put(4, new Pizza("Garlic Prawn Pizza", 20.5));
        pizzaMenu.put(5, new Pizza("Meat Deluxe Pizza", 22.5));
        dineInOrders = new ArrayList<>();
        deliveryOrders = new ArrayList<>();
    }

    public void showMenu() {
        System.out.println("\n===================PIZZA MENU=====================");
        System.out.println("             PIZZA                  M      L      ");
        for (HashMap.Entry<Integer, Pizza> entry : pizzaMenu.entrySet()) {
            Pizza pizza = entry.getValue();
            System.out.println("    " + entry.getKey() + ". " + pizza.getName() + " \t$ " 
                               + pizza.getPrice('M') + " / " + pizza.getPrice('L'));
        }
        System.out.println("==================================================");
    }

    public void proceedOrder() {
        int orderType;
        DineInOrder dineInOrder = null;
        DeliveryOrder deliveryOrder = null;

        while(true){
            System.out.println("\n-------------------------------------------------------");
            System.out.println("              1. Proceed Dine-in Orders                ");
            System.out.println("              2. Proceed Delivery Orders               ");
            System.out.println("-------------------------------------------------------");
            System.out.print("Select the option(1 / 2): ");
            orderType = In.nextInt();
            if(orderType != 1 && orderType != 2){
                System.out.println("INVALID OPTION: Please enter either 1 or 2.");
                continue;
            }

            if (orderType == 1) {
                System.out.print("Enter table number: ");
                int tableNumber = In.nextInt();
                dineInOrder = new DineInOrder(tableNumber);
                dineInOrders.add(dineInOrder);
            } else if (orderType == 2) {
                System.out.print("Enter delivery address: ");
                String address = In.nextLine();
                deliveryOrder = new DeliveryOrder(address);
                deliveryOrders.add(deliveryOrder);
            }
        
            showMenu();

            while (true) {                
                System.out.print("Enter the number of the pizza (1-5): ");
                int menuChoice = In.nextInt();

                if (pizzaMenu.containsKey(menuChoice)) {
                    Pizza selectedPizza = pizzaMenu.get(menuChoice).generatePizza();
                    if(orderType == 1){
                        selectedPizza.setSize(dineInOrder.askSize());
                        dineInOrder.addPizza(selectedPizza);
                        System.out.println("\n" + selectedPizza.getName() + "(" + selectedPizza.getSize() + ") is added to your order");
                        System.out.println("- Price: $" + selectedPizza.getPrice(selectedPizza.getSize()));
                        System.out.println("- Current Total: $" + dineInOrder.getTotalPrice());
                    } else {
                        selectedPizza.setSize(deliveryOrder.askSize());
                        deliveryOrder.addPizza(selectedPizza);
                        System.out.println("\n" + selectedPizza.getName() + "(" + selectedPizza.getSize() + ") is added to your order");
                        if(deliveryOrder.getTotalPrice() >= 45){
                            deliveryOrder.setDeliveryFee(0);
                            System.out.println("- Price: $" + selectedPizza.getPrice(selectedPizza.getSize()));
                            System.out.println("- Current Total: $" + deliveryOrder.getTotalPrice() + " (Free delivery)");
                        } else {
                            double needMore = 45 - deliveryOrder.getTotalPrice();
                            System.out.println("- Price: $" + selectedPizza.getPrice(selectedPizza.getSize()));
                            System.out.println("- Current Total: $" + deliveryOrder.getTotalPrice() + " (including $5 delivery fee)");  
                            System.out.println("[ FREE DELIVERY over $40: You can add $" + needMore + " more for free delivery! ]");                          
                        }                
                    }
                } else {
                    System.out.println("INVALID MENU: Please select a valid number from the menu.\n");
                    continue;
                }

                System.out.print("\nWould you like to add another pizza? (1 - yes / 2 - no): ");
                int input = In.nextInt();
                if(input != 1){
                    break;
                }
            }

            System.out.println("\n------ORDER COMPLETED. Thank you for your order!!------");
            if(orderType == 1){
                System.out.println("\t\tFinal order total: $" + dineInOrder.getTotalPrice());
            } else {
                System.out.println("\t\tFinal order total: $" + deliveryOrder.getTotalPrice());
            } 

            System.out.println("-------------------------------------------------------");
            System.out.print("\nDo you want to start a new order? (1 - yes / 2 - no): ");
            int input = In.nextInt();
            if(input != 1){
                break;
            }
        }            
    }       


    public void manageOrders(){
        System.out.println("\n-------------------------------------------------------");
        System.out.println("               1. Manage Dine-in Orders                ");
        System.out.println("               2. Manage Delivery Orders               ");
        System.out.println("-------------------------------------------------------");

        while(true){
            System.out.print("Select the option(1 / 2): ");
            int choice = In.nextInt();
            if(choice == 1){
                manageDineInOrders(dineInOrders);
                break;
            } else if(choice == 2){
                manageDeliveryOrders(deliveryOrders);
                break;
            } else {
                System.out.println("INVALID CHOICE: Please select 1 or 2!");
            }
        }

    }

    private void manageDineInOrders(ArrayList<DineInOrder> dineInOrders) {
        while(true){
            if (dineInOrders.size() == 0) {
                System.out.println("\n< There are no Dine-in Orders. >");
                break;
            } else {
                System.out.println("\n\n▼ ▼ ▼ ▼ ▼ ▼ ▼ DINE-IN ORDER LIST ▼ ▼ ▼ ▼ ▼ ▼ ▼");
                if (dineInOrders.size() == 0){
                    System.out.println("The list is empty.");
                }
                for (int i = 0; i < dineInOrders.size(); i++) {
                    System.out.println("\n<Dine-in Order: no. " + (i + 1) + ">");
                    dineInOrders.get(i).displayOrderDetails();
                }
                System.out.print("\nWould you like to cancel any order? (1 - yes / 2 - no): ");
                int wantToCancel = In.nextInt();
                if (wantToCancel == 1){
                    while(true){
                        System.out.print("Enter the number of the order you want to cancel: ");
                        int cancelNumber = In.nextInt();
                        if(cancelNumber > 0 && cancelNumber <= dineInOrders.size()){
                            dineInOrders.remove(cancelNumber - 1);
                            System.out.println("<Dine-in Order no." + cancelNumber + "> has been cancelled.\n");
                            break;
                        } else {
                            System.out.println("INVALID ORDER: Please check the order list again!!\n");
                        }
                    }
                    System.out.print("Would you like to view the renewed order list? (1 - yes / 2 - no): ");
                    int reviewList = In.nextInt();
                    if (reviewList != 1) {
                        break;
                    }
                } else {
                    break;
                }
            }
        }
    }

    private void manageDeliveryOrders(ArrayList<DeliveryOrder> deliveryOrders) {
        while(true){
            if (deliveryOrders.size() == 0) {
                System.out.println("\n< There are no Delivery Orders. >");
                break;
            } else {
                System.out.println("\n\n▼ ▼ ▼ ▼ ▼ ▼ ▼ DELIVERY ORDER LIST ▼ ▼ ▼ ▼ ▼ ▼ ▼");
                for (int i = 0; i < deliveryOrders.size(); i++) {
                    System.out.println("\n<Delivery Order: no. " + (i + 1) + ">");
                    deliveryOrders.get(i).displayOrderDetails();
                }
                System.out.print("\nWould you like to cancel any order? (1 - yes / 2 - no): ");
                int wantToCancel = In.nextInt();
                if (wantToCancel == 1){
                    while(true){
                        System.out.print("Enter the number of the order you want to cancel: ");
                        int cancelNumber = In.nextInt();
                        if(cancelNumber > 0 && cancelNumber <= deliveryOrders.size()){
                            deliveryOrders.remove(cancelNumber - 1);
                            System.out.println("<Delivery Order no." + cancelNumber + "> has been cancelled.\n");
                            break;
                        } else {
                            System.out.println("INVALID ORDER: Please check the order list again!!\n");
                        }
                    }
                    System.out.print("Would you like to view the renewed order list? (1 - yes / 2 - no): ");
                    int reviewList = In.nextInt();
                    if (reviewList != 1) {
                        break;
                    }
                } else {
                    break;
                }
            }
        }
    }
}

class Order {
    protected ArrayList<Pizza> pizzaOrder;
    protected double totalPrice;

    public Order() {
        pizzaOrder = new ArrayList<>();
        totalPrice = 0;
    }

    public void addPizza(Pizza pizza) {
        pizzaOrder.add(pizza);
        totalPrice += pizza.getPrice(pizza.getSize());
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public char askSize() {
        while(true) {
            System.out.print("Enter the size(M / L): ");
            char size = In.nextChar();
            if (size == 'M' || size == 'm') {
                return 'M';
            } else if (size == 'L' || size == 'l'){
                return 'L';
            } else {
                System.out.println("Invalid size. Please enter M or L!\n");
            }
        }
    }

    public void displayPizzas(){
        int pizzaNumber = 1;
        for (Pizza pizza : pizzaOrder) {
            System.out.print("(" + pizzaNumber + ") " + pizza.getName());
            System.out.println(" (" + pizza.getSize() + ") \t\t $" + pizza.getPrice(pizza.getSize()));
            pizzaNumber++;
        }    
    }

    public void displayOrderDetails() {  
        displayPizzas(); 
        System.out.println(toString());          
    }
    
    public String toString(){
        return "..............................................."
              + "\nTotal price: $" + this.totalPrice;
    }
}

class DineInOrder extends Order {
    private int tableNumber;

    public DineInOrder(int tableNumber) {
        super();
        this.tableNumber = tableNumber;
    }

    @Override
    public void displayOrderDetails() {
        System.out.println("...............................................");           
        System.out.println("▶ Table Number: " + this.tableNumber);
        super.displayOrderDetails();
    }
}

class DeliveryOrder extends Order {
    private String address;
    private double deliveryFee;

    public DeliveryOrder(String address) {
        super();
        this.address = address;
        this.deliveryFee = 5.0;
    }

    @Override
    public double getTotalPrice() {
        return super.getTotalPrice() + deliveryFee;
    }

    public void setDeliveryFee(double deliveryFee){
        this.deliveryFee = deliveryFee;
    }
    
    @Override
    public void displayOrderDetails() {
        System.out.println("...............................................");           
        System.out.println("▶ Address: " + this.address);
        super.displayOrderDetails();
        System.out.println("Delivery Fee: $" + this.deliveryFee);
    }

}

class Pizza {
    private String name;
    private double basePrice;
    private char size;

    public Pizza(String name, double basePrice) {
        this.name = name;
        this.basePrice = basePrice;
        this.size = 'M';
    }

    public Pizza generatePizza() {
        Pizza newPizza = new Pizza(this.name, this.basePrice);
        return newPizza;
    }

    public void setSize(char size){
        this.size = size;
    }

    public String getName() {
        return this.name;
    }

    public double getPrice(char size) {;
        if (size == 'L'){
            return (basePrice + 4.0);
        } else {
            return basePrice;
        }
    }

    public char getSize(){
        return this.size;
    }
}


