package revision.projects;

import java.util.*;
import java.util.stream.Collectors;

enum OrderStatus{
    PLACED,
    PREPARING,
    DELIVERED,
    CANCELLED
}

//exception handling classes
class CustomerNotExists extends Exception{
    public CustomerNotExists(String message){
        super(message);
    }
}

class DuplicateOrderException extends Exception{
    public DuplicateOrderException(String message){
        super(message);
    }
}

class RestaurantNotFoundException extends Exception{
    public RestaurantNotFoundException(String message){
        super(message);
    }
}

class MenuItemNotFoundException extends Exception{
    public MenuItemNotFoundException(String message){
        super(message);
    }
}

class InsufficientBalanceException extends Exception{
    public InsufficientBalanceException(String message){
        super(message);
    }
}

class InvalidAmountException extends Exception{
    public InvalidAmountException(String message){
        super(message);
    }
}


//interface and their implementations
interface Payment{
    public void pay(Customer customer, double amount) throws InsufficientBalanceException;
}

class UPI implements Payment{
    @Override 
    public void pay(Customer customer, double amount) throws InsufficientBalanceException{
        customer.deductMoney(amount);
        System.out.println("Transaction of ₹" + amount + " was made successsfully through UPI.");
    }
}

class CardPayment implements Payment{
    public void pay(Customer customer, double amount) throws InsufficientBalanceException{
        customer.deductMoney(amount);
        System.out.println("Transaction of ₹" + amount + " was made successfully through card.");
    }
}

class CashPayment implements Payment{
    public void pay(Customer customer, double amount) throws InsufficientBalanceException{
        System.out.println("Cash on Delivery enabled on your order.");
    }
}

//classes
class MenuItem{

    private int id;
    private String name;
    private double price;
    private String category;
    private boolean available = true;

    public MenuItem(int id, String name, double price, String category){
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isAvailable(){
        return this.available;
    }

    public double getFoodPrice(){
        return this.price;
    }

    public int getMenuId(){
        return this.id;
    }

    public String getFoodName(){
        return this.name;
    }

    public void display(){
        System.out.println(
            this.id + " ".repeat(6) + this.name+ " ".repeat(6) + this.price+ " ".repeat(6) + this.category+ " ".repeat(6) + this.available + "\n"
        );
    }

    public void setAvailability(boolean available){
        this.available = available;
    }

}

class Restaurant{

    private int restaurantId;
    private String name;
    private String address;

    private ArrayList<MenuItem> menuItem = new ArrayList<>();

    public Restaurant(int id, String name, String address){
        this.restaurantId = id;
        this.name = name;
        this.address = address;
    }

    public int getRestaurantId(){
        return this.restaurantId;
    }

    public String getRestaurantName(){
        return this.name;
    }

    public String getRestaurantAddress(){
        return this.address;
    }

    private int getLastFoodId(){
        if(!menuItem.isEmpty()){
            return menuItem.get(menuItem.size() - 1).getMenuId();
        }
        return 10000;
    }

    public void addItem(String foodName, double price, String category){
        menuItem.add(new MenuItem(getLastFoodId()+1, foodName, price, category));
        System.out.println("Food added successfully!!!");
    }

    public void displayMenu(){
        for(MenuItem menu : menuItem){
            menu.display();
        }
    }

    public void displayInfo(){
        System.out.println(
            this.restaurantId + " ".repeat(6) + this.name + " ".repeat(6) + this.address + "\n"
        );
    }

    public MenuItem findItem(int id) throws MenuItemNotFoundException{
        for(MenuItem menu : menuItem){
            if(menu.getMenuId() == id){
                return menu;
            }
        }
        throw new MenuItemNotFoundException("This food doesn't exists at all.");
    }

}

abstract class User{

    private int id;
    private String name;
    private long phone;

    private double walletBalance;

    abstract public void display();

    public int getUserId(){
        return this.id;
    }

    public String getUserName(){
        return this.name;
    }

    public void setUserId(int id){
        this.id = id;
    }

    public void setUserName(String name){
        this.name = name;
    }

    public void setPhone(long phone){
        this.phone = phone;
    }

    public void setBalance(double amount){
        this.walletBalance = amount;
    }

    public void addMoney(double amount){
        this.walletBalance += amount;
    }

    public void deductMoney(double amount) throws InsufficientBalanceException{
        if(this.walletBalance<amount){
            throw new InsufficientBalanceException("You have insufficient balance.");
        }
        this.walletBalance -= amount;
    }

}

class Customer extends User{

    public Customer(int id, String name, long phone, double balance){
        this.setUserId(id);
        this.setUserName(name);
        this.setPhone(phone);
        this.setBalance(balance);
    }

    private ArrayList<Order> orderHistory = new ArrayList<>();

    public void viewOrders(){
        if(orderHistory.isEmpty()){
            System.out.println("You haven't made any orders yet!!.");
            return;
        }
        for(Order orders : orderHistory){
            System.out.println(
                orders.getOrderId() + " ".repeat(6) + orders.getOrderedItems() + " ".repeat(6) + orders.getTotalAmount()
            );
        }
    }

    @Override
    public void display() {

        System.out.println(
            getUserId()
            + " "
            + getUserName()
        );
    }

    public double getDiscount(){
        return 0;
    }

    public void addOrder(Order order) {
        orderHistory.add(order);
    }

    public void cancelOrder(int orderId) {

        Iterator<Order> iterator = orderHistory.iterator();
        while (iterator.hasNext()) {
            Order order = iterator.next();
            if (order.getOrderId() == orderId) {
                iterator.remove();
                return;
            }
        }
    }

}

class PremiumCustomer extends Customer{

    public PremiumCustomer(int id, String name, long phone, double balance){
        super(id, name, phone, balance);
    }

    private static final int DISCOUNT = 10;

    @Override 
    public double getDiscount(){
        return DISCOUNT;
    }

}

class Order{

    private int orderId;
    private Customer customer;
    private Restaurant restaurant;
    private ArrayList<MenuItem> orderedItems = new ArrayList<>();
    private double totalAmount;
    private OrderStatus status;

    public Order(int orderId, Customer customer, Restaurant restaurant){
        this.orderId = orderId;
        this.customer = customer;
        this.restaurant = restaurant;
        this.status = OrderStatus.PLACED;
    }

    public double getTotalAmount(){
        return totalAmount;
    }

    public void addMenuItem(MenuItem item){
        orderedItems.add(item);
    }

    public int getOrderId(){
        return this.orderId;
    }

    public void calculateBill(){
        this.totalAmount = 0;
        for(MenuItem item : orderedItems){
            this.totalAmount += item.getFoodPrice();
        }
        this.totalAmount -= totalAmount * customer.getDiscount()/100;
    }

    public String getOrderedItems() {

        return orderedItems.stream()
                .map(MenuItem::getFoodName)
                .collect(Collectors.joining(", "));
    }

    public void cancelOrder(){

    }

}

class FoodDeliverySystem{

    private ArrayList<Restaurant> restaurants = new ArrayList<>();
    private ArrayList<Customer> customers = new ArrayList<>(); 
    private Map<Integer, Order> orders = new HashMap<>();

    private Customer findCustomer(int id) throws CustomerNotExists{
        for(Customer customer : customers){
            if(customer.getUserId() == id){
                return customer;
            }
        }
        throw new CustomerNotExists("No such Customer exists.");
    }

    private Restaurant findRestaurant(int id) throws RestaurantNotFoundException{
        for(Restaurant restaurant : restaurants){
            if(restaurant.getRestaurantId() == id){
                return restaurant;
            }
        }
        throw new RestaurantNotFoundException("No such restaurant exists.");
    }

    private int getLastUserId(){
        if(!customers.isEmpty()){
            return customers.get(customers.size() - 1).getUserId();
        }
        return 100;
    }

    private int getLastRestaurantId(){
        if(!restaurants.isEmpty()){
            return restaurants.get(restaurants.size() - 1).getRestaurantId();
        }
        return 1000;
    } 

    private Order findOrder(int OrderId){
        return orders.get(OrderId);
    }

    public void registerCustomer(String name, long phone, double balance){
        customers.add(new Customer(getLastUserId()+1, name, phone, balance));
        System.out.println("Customer registered successfully!!!");
    }

    public void addRestaurant(String name, String address){
        restaurants.add(new Restaurant(getLastRestaurantId()+1, name, address));
        System.out.println("New Restaurant added successfully!!!");
    }

    public void addFoodItem(int restaurantId, String foodName, Double price, String category){
        boolean found = false;
        for(Restaurant restaurant : restaurants){
            if(restaurant.getRestaurantId() == restaurantId){
                restaurant.addItem(foodName, price, category);
                found = true;
                return;
            }
        }
        if(!found){
            System.out.println("No such restaurant exists.");
        }
    }

    private Payment getPaymentMethod(int choice) {

    switch (choice) {

        case 1:
            return new UPI();

        case 2:
            return new CardPayment();

        case 3:
            return new CashPayment();

        default:
            throw new IllegalArgumentException("Invalid payment method.");
    }
}

    public void placeOrder(int customerId, int restaurantId, String orderList, int paymentMethod){
        //getting food id from input
        String[] parts = orderList.split(",");
        int[] foodID = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try{
                foodID[i] = Integer.parseInt(parts[i].trim());
            }catch(NumberFormatException e){
                System.out.println("Invalid food ID.");
            }
        }
        try{
            Restaurant restaurant = findRestaurant(restaurantId);
            Customer customer = findCustomer(customerId);

            int orderId;
            do{
                orderId = (int)(Math.random()*900000)+100000;
            }
            while(orders.containsKey(orderId));

            Order order = new Order(orderId, customer, restaurant);

            for(int foodId : foodID){
                try{
                    MenuItem item = restaurant.findItem(foodId);
                    if(item.isAvailable()){
                        order.addMenuItem(item);
                    }else{
                        System.out.println("Currently " + item.getFoodName() + " is not available.");
                    }
                }catch(MenuItemNotFoundException e){
                    System.out.println(e.getMessage());
                }
            }
            
            Payment payment = getPaymentMethod(paymentMethod);
            
            try{
                order.calculateBill();
                if(order.getTotalAmount()==0){
                    System.out.println("No valid food items selected.");
                    return;
                }
                payment.pay(customer, order.getTotalAmount());
                orders.put(order.getOrderId(), order);
                customer.addOrder(order);
                System.out.println("Congratulations!! Your order is preparing... It will be delivered soon...");
            }catch(InsufficientBalanceException e){
                System.out.println(e.getMessage());
            }
        }catch(RestaurantNotFoundException e){
            System.out.println(e.getMessage());
        }catch(CustomerNotExists e){
            System.out.println(e.getMessage());
        }
    }

    public void cancelOrder(int customerId, int orderId){
        try{
            Customer customer = findCustomer(customerId);
            Order order = findOrder(orderId);
            if(order == null){
                System.out.println("Order does not exist.");
                return;
            }
            customer.cancelOrder(orderId);
            orders.remove(orderId);
            customer.addMoney(order.getTotalAmount());
            System.out.println("Your order has been CANCELLED. Your payment will be refunded.");
            
        }catch(CustomerNotExists e){
            System.out.println(e.getMessage());
        }
    }

    public void displayRestaurants(){
        if(restaurants.isEmpty()){
            System.out.println("No restaurants are registered yet!!!");
            return;
        }
        System.out.println("Here are registered restaurants...");
        for(Restaurant restaurant : restaurants){
            restaurant.displayInfo();
        }
    }

    public void searchRestaurantById(int id){
        try{
            if(findRestaurant(id) != null){
                findRestaurant(id).displayInfo();
                return;
            }
        }catch(RestaurantNotFoundException e){
            System.out.println("No restaurant found with ID " + id);
        }
    }

    public void searchRestaurantbyName(String name){
        for(Restaurant restaurant : restaurants){
            if(restaurant.getRestaurantName().equalsIgnoreCase(name)){
                restaurant.displayInfo();
                return;
            }
        }
        System.out.println("No such restaurant exists.");
    }

    public void searchRestaurantbyAddress(String address){
        boolean found = false;
        for(Restaurant restaurant : restaurants){
            if(restaurant.getRestaurantAddress().equalsIgnoreCase(address)){
                restaurant.displayInfo();
                found = true;
            }
        }
        if(!found)
        System.out.println("No restaurant exists in " + address);
    }

    public void getMenu(int restaurantId){
        for(Restaurant restaurant : restaurants){
            if(restaurant.getRestaurantId() == restaurantId){
                System.out.println("Here's menu at your selected restaurant...");
                restaurant.displayMenu();
                return;
            }
        }
        System.out.println("No menu available in this restaurant.");
    }

    public void getCustomer(int id) throws CustomerNotExists{
        findCustomer(id);
    }

    public void displayOrders(){

        if(orders.isEmpty()){
            System.out.println("No orders placed.");
            return;
        }
        for(Order order : orders.values()){

            System.out.println(
                    order.getOrderId()
                    + "    "
                    + order.getOrderedItems()
                    + "    ₹"
                    + order.getTotalAmount()
            );
        }
    }

    public void addBalance(int customerId, double amount){
        try{
            Customer customer = findCustomer(customerId);
            customer.addMoney(amount);
        }catch(CustomerNotExists e){
            System.out.println(e.getMessage());
        }
    }

    public void viewOrders(int customerId) {
        try{
            Customer customer = findCustomer(customerId);
            customer.viewOrders();
        }catch(CustomerNotExists e){
            System.out.println(e.getMessage());
        }
    }

}

public class OnlineFoodOrderingSystem {
    public static void showMenu(){
        System.out.println("\n" + "*".repeat(8) + "MENU" + "*".repeat(8));
        System.out.println(
            "1 Register Customer\n" + //
            "2 Add Restaurant\n" + //
            "3 Add Food Item\n" + //
            "4 Display Restaurants\n" + //
            "5 Search Restaurant\n" + //
            "6 Display Menu\n" + //
            "7 Place Order\n" + //
            "8 Cancel Order\n" + //
            "9 Add Wallet Money\n" + //
            "10 View Orders\n" + //
            "11 Exit"
        );
    }

    public static boolean verifyMobile(long mobile){
            if(String.valueOf(mobile).length()!=10){
                return false;
            }
            return true;
        }
    public static void main(String[] args) {

        int choice, id, ch, qty, c_id;
        String name, address, category, order;
        long phone;
        double price, balance;

        Scanner sc = new Scanner(System.in);
        FoodDeliverySystem placeOrder = new FoodDeliverySystem();

        while(true){
            showMenu();
            System.out.print("\nEnter your choice: ");
            choice = sc.nextInt();

            //switch case for menu
            switch(choice){

                // adding new customer
                case 1:
                    System.out.println("Enter your details here...");
                    System.out.print("Your name: ");
                    sc.nextLine();
                    name = sc.nextLine();
                    System.out.print("Your phone: ");
                    phone = sc.nextLong();
                    if(verifyMobile(phone)){
                        System.out.print("Enter Wallet Balance: ");
                        balance = sc.nextDouble();
                        placeOrder.registerCustomer(name, phone, balance);
                    }
                    else{
                        System.out.println("\n" + "##".repeat(10) + " Phone number must be 10 digits.");
                    }
                    break;

                // adding new restaurant
                case 2:
                    System.out.println("Enter restaurant details here...");
                    System.out.print("Restaurant name: ");
                    sc.nextLine();
                    name = sc.nextLine();
                    System.out.print("Restaurant address: ");
                    address = sc.nextLine();
                    placeOrder.addRestaurant(name, address);
                    break;

                // adding new food item
                case 3:
                    System.out.println("Enter food item details here...");
                    System.out.print("Enter Restaurant ID: ");
                    id = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Food name: ");
                    name = sc.nextLine();
                    System.out.print("Food price: ");
                    price = sc.nextDouble();
                    System.out.print("Food category: ");
                    category = sc.nextLine();
                    placeOrder.addFoodItem(id, name, price, category);
                    break;

                // display registered restaurants
                case 4:
                    placeOrder.displayRestaurants();
                    break;

                // search restaurants
                case 5:
                    System.out.println(
                        "1 Search by ID\n" + //
                        "2 Search by name\n" + //
                        "3 Search by address"
                    );
                    System.out.print("Enter your choice: ");
                    ch = sc.nextInt();
                    switch(ch){
                        case 1:
                            System.out.print("Enter Restaurant ID: ");
                            id = sc.nextInt();
                            placeOrder.searchRestaurantById(id);
                            break;

                        case 2:
                            System.out.print("Enter Restaurant name: ");
                            sc.nextLine();
                            name = sc.nextLine();
                            placeOrder.searchRestaurantbyName(name);
                            break;
                        
                        case 3:
                            System.out.print("Enter Restaurant address: ");
                            sc.nextLine();
                            address = sc.nextLine();
                            placeOrder.searchRestaurantbyAddress(address);
                            break;

                        default:
                            System.out.println("You entered wrong choice. Going to main menu.");
                    }
                    break;

                // get menu of restaurant
                case 6:
                    System.out.print("Enter Restaurant Id: ");
                    id = sc.nextInt();
                    placeOrder.getMenu(id);
                    break;

                // place order
                case 7:
                    System.out.print("Enter your ID: ");
                    c_id = sc.nextInt();
                    try{
                        placeOrder.getCustomer(c_id);

                        placeOrder.displayRestaurants();
                        System.out.println();
                        System.out.print("Please select a restaurant by entering ID: ");
                        id = sc.nextInt();
                        System.out.println();
                        placeOrder.getMenu(id);
                        System.out.println();
                        System.out.println("Please select your food from menu given: ");
                        System.out.print("Enter food IDs separated by comma(,): ");
                        sc.nextLine();
                        order = sc.nextLine();
                        System.out.print("Select your payment method: (1 for UPI / 2 for Card / 3 for Cash on Delivery): ");
                        ch = sc.nextInt();

                        placeOrder.placeOrder(c_id, id, order, ch);
                    }catch(CustomerNotExists e){
                        System.out.println("You are not a registered customer. Please go and register yourself first.");;
                    }
                    break;

                // cancel order
                case 8:
                    System.out.print("Enter Customer ID: ");
                    c_id = sc.nextInt();
                    System.out.print("Enter Order ID: ");
                    id = sc.nextInt();

                    placeOrder.cancelOrder(c_id, id);
                    break;

                // add money to wallet
                case 9:
                    System.out.print("Enter your ID: ");
                    c_id = sc.nextInt();
                    System.out.print("Enter amount to be added in wallet: ");
                    balance = sc.nextDouble();
                    placeOrder.addBalance(c_id, balance);
                    break;

                // view past orders
                case 10:
                    System.out.print("Enter your ID: ");
                    c_id = sc.nextInt();
                    placeOrder.viewOrders(c_id);
                    break;

                // exit
                case 11:
                    System.out.println("Visit Again.");
                    sc.close();
                    return;

                default: 
                    System.out.println("Please enter a valid choice.");
            }

        }
    }
}
