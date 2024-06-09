import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;

public class JourneyGraph extends JFrame {
    final private Font font = new Font("Raleway", Font.PLAIN, 12);             // to customize font
    final private Dimension dimension = new Dimension(150, 50);
    final private Dimension maxSize = new Dimension(600, 300);
    final private Dimension minSize = new Dimension(400, 100);

    //create panels
    private JPanel navigationPanel;
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private RouteDistance<String, Integer> routeDistance;
    
    public JourneyGraph() {
        initialize();                                                               //when constructors are called, it automatically calls on initialize method
    }
    
    private void initialize() {
        setTitle("Journey Plan");                                               //name for journey page
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        // navigation Panel
        navigationPanel = new JPanel();
        navigationPanel.setBackground(Color.LIGHT_GRAY);
        navigationPanel.setPreferredSize(new Dimension(1200, 50));
        JLabel titleLabel = new JLabel("NBA Manager Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        navigationPanel.add(titleLabel);
        // sidebar Panel
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.DARK_GRAY);
        sidebarPanel.setPreferredSize(new Dimension(200, 750));
        String[] buttonLabels = {"HOME", "TEAM", "PLAYER", "JOURNEY", "CONTRACT", "INJURY"};
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setPreferredSize(dimension);
            button.setMaximumSize(maxSize);
            button.setMinimumSize(minSize);
            button.setBackground(Color.LIGHT_GRAY);
            button.setFont(new Font("Arial", Font.BOLD, 15));
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    handleSidebarButtonClick(label);
                }
            });
            sidebarPanel.add(button);
        }

        //  content panel
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setPreferredSize(new Dimension(50, 500));

        //map with all routes
        JLabel imageLabel = loadImage("NBAMap.png", true,450, 260);     
        JPanel mapPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));                        //panel to display map
        mapPanel.add(imageLabel);                                                               //adds map panel to contentpanel

        routeDistance = new RouteDistance<>(JourneyRoutes());                                   //create an instance of routedistance class with
                                                                                                    // the cities and routes
        String startCity = "San Antonio(Spurs)";

        ArrayList<ArrayList<String>> dfsRoutes = routeDistance.depthFirstSearch(startCity);     //find journeys using dfs method and stores in list
        int[] totalDistance = new int[dfsRoutes.size()];;                                       //create array of int of distances 
        int i=0;
        for (ArrayList<String> route : dfsRoutes) {
                        totalDistance[i] = calculateTotalDistance(route, JourneyRoutes());      //add the distances according to respective routes, call the calculate total distance method
                        i++;
        }
        Arrays.sort(totalDistance);                                                             //sort total distance array in increasing order

        //BEST ROUTE DISPLAY
        JPanel bestRoutePanel = new JPanel();
        bestRoutePanel.setLayout(new BoxLayout(bestRoutePanel, BoxLayout.Y_AXIS));              //create panel 
        
        JLabel bestRouteLabel = new JLabel("Best Route:");
        bestRouteLabel.setFont(new Font("Arial", Font.BOLD, 15));

        //arrange so that the lowest distances at front array
       if(!dfsRoutes.isEmpty()){                                                                //if the list has journey of routes
        int firstTotalDistance = totalDistance[0];                                              //the only distance that matter here is at the first index
        for (ArrayList<String> route : dfsRoutes){                                              //getting the journey routes
            if(firstTotalDistance==calculateTotalDistance(route, JourneyRoutes())){             //if the supposed distance is the same as this routes' distance calculated
                StringBuilder firstFormattedRoute = new StringBuilder();                        //  format
            for (String city : route) {
                firstFormattedRoute.append(city).append(" -> ");
            }
            firstFormattedRoute.setLength(firstFormattedRoute.length() - 4);
            JLabel firstRouteLabel = new JLabel(firstFormattedRoute.toString()+ "-" +firstTotalDistance);       //adds distance in the label
            firstRouteLabel.setVerticalAlignment(SwingConstants.TOP);                           // Align label content to the top
            firstRouteLabel.setHorizontalAlignment(SwingConstants.LEFT);                        // Align label content to the left
            firstRouteLabel.setOpaque(false);                                          // Ensure label is opaque so background color shows
            firstRouteLabel.setBackground(Color.LIGHT_GRAY);                                    // Set background color to match panel background
            firstRouteLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));         // Add padding
            firstRouteLabel.setText("<html>" + 1 +". "+firstFormattedRoute.toString() + "<br>" +"Distance: "+ firstTotalDistance +" km"+ "</html>"); // Use HTML to create multiline label
            firstRouteLabel.setFont(font);
            bestRoutePanel.add(firstRouteLabel); 

            JFrame bestMapFrame = new JFrame("Best Route Map");                           //frame title

            JButton bestButton = new JButton("View Map");                                  //button to view map
            bestButton.addActionListener(e->showPopup(bestMapFrame,0,firstFormattedRoute));
            
            bestRoutePanel.add(bestButton);                                                     //adds button to the panel
        }
    }
}

        //OTHER ROUTES DISPLAY
        JPanel otherRoutesPanel = new JPanel();                                                 //create panel
        otherRoutesPanel.setLayout(new BoxLayout(otherRoutesPanel,1));

        JLabel otherRouteLabel = new JLabel("Other Routes:");
        otherRouteLabel.setFont(new Font("Arial", Font.BOLD, 15));

        i=0;                                                                                    //resetting int i

        Arrays.sort(totalDistance);                                                             //arrange so that the lowest distances at front array
        for(i=1; i<5; i++){
        for (ArrayList<String> route : dfsRoutes){                                              //for each journey, create a label of routes
            StringBuilder formattedRoute = new StringBuilder();
            if(totalDistance[i]==calculateTotalDistance(route, JourneyRoutes())){               //if the distance in index matches the distance calculated of this route
                for (String city : route) {                                                     //  format
                    formattedRoute.append(city).append(" -> ");
                }
                formattedRoute.setLength(formattedRoute.length() - 4);
                JLabel routeLabel=new JLabel(formattedRoute.toString()+" - "+totalDistance[i]);
                routeLabel.setVerticalAlignment(SwingConstants.TOP);                            // Align label content to the top
                routeLabel.setHorizontalAlignment(SwingConstants.LEFT);                         // Align label content to the left
                routeLabel.setOpaque(false);                                           // Ensure label is opaque so background color shows
                routeLabel.setBackground(Color.LIGHT_GRAY);                                     // Set background color to match panel background
                routeLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding
                routeLabel.setText("<html>" + (i+1) +". " + formattedRoute.toString() + "<br>" +"Distance: "+ totalDistance[i] + " km"+"</html>"); // Use HTML to create multiline label
                routeLabel.setFont(font);
                otherRoutesPanel.add(routeLabel);                                               //add this filled label onto the panel

                JFrame MapFrame = new JFrame("Route "+(i+1) +" Map");                            //title for this route 

                JButton Button = new JButton("View Map");                                   //button to view map
                int routeIndex =i;
                Button.addActionListener(e->showPopup(MapFrame,routeIndex,formattedRoute));

                otherRoutesPanel.add(Button);                                                   //adds view map button onto this panel
            }
        }
    }
        //adds map panel onto the content panel
        contentPanel.add(mapPanel,BorderLayout.NORTH);

        //bestroute title
        JPanel bestRouteWrapperPanel = createBorderedPanel("B E S T  R O U T E : ");
        bestRouteWrapperPanel.add(bestRoutePanel, BorderLayout.CENTER);

        //otherroute title
        JPanel otherRoutesWrapperPanel = createBorderedPanel("O T H E R  R O U T E S : ");
        otherRoutesWrapperPanel.add(otherRoutesPanel, BorderLayout.CENTER);

        //adding panel to put routes panel
        JPanel routePanels = new JPanel();
        routePanels.setLayout(new BoxLayout(routePanels, BoxLayout.X_AXIS));;

        //adds best and other routes onto the panel
        routePanels.add(bestRouteWrapperPanel);
        routePanels.add(otherRoutesWrapperPanel);

        //adds all route panels onto content panel
        contentPanel.add(routePanels);

        //adds all important panels
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(navigationPanel, BorderLayout.NORTH);
        getContentPane().add(sidebarPanel, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);  
        
    }

    private JPanel createBorderedPanel(String title) {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    
    // Create an empty border with padding around the panel
    EmptyBorder border = new EmptyBorder(10, 10, 10, 10);                   // Adjust padding as needed
    panel.setBorder(border);
    
    // Add a titled border to the panel
    TitledBorder titledBorder = new TitledBorder(title);
    titledBorder.setTitlePosition(TitledBorder.DEFAULT_POSITION);
    panel.setBorder(BorderFactory.createCompoundBorder(titledBorder, BorderFactory.createEmptyBorder(5, 5, 5, 5))); // Adjust padding as needed
    
    return panel;
}

    //route maps images popup
    private void showPopup(JFrame frame, int a, StringBuilder b){
        String imageName="";
        String popupName ="Route "+ (a+1)+ " Map";;
        switch(a){
            case(0):
                imageName = "bestRouteMap.png";
                popupName = "Best Route Map";
                break;
            case(1):
                imageName = "routeMap2.png";
                break;
            case(2):
                imageName = "routeMap3.png";
                break;
            case(3):
                imageName = "routeMap4.png";
                break;
            case(4):
                imageName = "routeMap5.png";
                break;
        }

        JLabel bestMapImage = loadImage(imageName, true,400, 250);      //loads images according to the set imagename
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));

        String[] bStrings = b.toString().split("->");                                                //format   
        StringBuilder route = new StringBuilder();
            for (String city : bStrings) {
                route.append(city).append("<br>");
            }
        JLabel routeLabel = new JLabel(route.toString());
        routeLabel.setText("<html>"+ route.toString() + "<br>" + "</html>");                               // Use HTML to create multiline label
        routeLabel.setFont(new Font("Monospace", Font.BOLD, 14));
        panel.add(routeLabel);                                                                             //add routes label onto this popup panel
        panel.add(bestMapImage);                                                                           //add image of the route for visualisation

        JOptionPane.showMessageDialog(frame, panel, popupName, JOptionPane.PLAIN_MESSAGE);
    }

    //calculate distance method
    private static int calculateTotalDistance(ArrayList<String> journey, Map<String, Integer> map) {
        int totalDistance = 0;
        for (int i = 0; i < journey.size() - 1; i++) {
            String currentCity = journey.get(i);                                                          //change source city
            String nextCity = journey.get(i + 1);                                                         //change destination sity
            totalDistance += map.getRouteWeight(currentCity, nextCity);                                   //adds the distances between the source city and dest city
        }
        return totalDistance;
    }

    //changing windows by calling the constructors of classes
    private void handleSidebarButtonClick(String label) {
        this.dispose();
        switch (label) {
            case "HOME":
                new Home();
                break;
            case "TEAM":
                new Team();
                break;
            case "PLAYER":
                new Players(); 
                break;
            case "JOURNEY":
                new JourneyGraph();
                break;
            case "INJURY":
                new Injury();
                break;
            case "CONTRACT":
                new Contract();
                break;
        }
    }

    //loading an image from JLabel
    private JLabel loadImage(String fileName, boolean isResized, int targetWidth, int targetHeight) {
        BufferedImage image;    
        JLabel imageContainer;

        try {
            image = ImageIO.read(new File(fileName));                       //reading the filename using bufferreader

            if (isResized) {
                image = resizeImage(image, targetWidth, targetHeight);      //call resizeImage method to adjust sizes
            }

            imageContainer = new JLabel(new ImageIcon(image));            
            return imageContainer;                                          //return the image when called
        } catch (Exception e) {
            System.out.println("Error: " + e);                              //error handling
            return null;
        }
    }
    // resizing image
    private BufferedImage resizeImage(BufferedImage image, int targetWidth, int targetHeight) {
        BufferedImage resizedImg = new BufferedImage(targetWidth,
                targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImg.createGraphics();
        graphics2D.drawImage(image, 0, 0, targetWidth,
                targetHeight, null);
        graphics2D.dispose();
        return resizedImg;
    }

    //adding cities and weighted routes into the system
    public Map<String, Integer> JourneyRoutes(){
        Map<String,Integer> map1 = new Map<>();
        String[] cities = {"San Antonio(Spurs)","Golden State(Warriors)","Boston(Celtics)",
            "Miami(Heat)","Los Angeles(Lakers)","Phoenix(Suns)","Orlando(Magic)",
            "Denver(Nuggets)","Oklahoma City(Thunder)","Houston(Rockets)"};     //citynames in array of Strings
        for(String i:cities)
            map1.addCity(i);                                            //iterate through the array to add each strings as a city node
        
        System.out.println("Number of cities: "+map1.getSize());        //check the total to be 10
        
        map1.addRoute("San Antonio(Spurs)","Phoenix(Suns)", 500);
        map1.addRoute("San Antonio(Spurs)","Oklahoma City(Thunder)", 678);
        map1.addRoute("San Antonio(Spurs)","Houston(Rockets)", 983);
        map1.addRoute("San Antonio(Spurs)","Orlando(Magic)", 1137);
        
        map1.addRoute("Phoenix(Suns)","Los Angeles(Lakers)", 577);
        
        map1.addRoute("Los Angeles(Lakers)","Oklahoma City(Thunder)", 1901);
        map1.addRoute("Los Angeles(Lakers)","Golden State(Warriors)", 554);
        
        map1.addRoute("Golden State(Warriors)","Oklahoma City(Thunder)", 2214);
        map1.addRoute("Golden State(Warriors)","Denver(Nuggets)", 1507);
        
        map1.addRoute("Denver(Nuggets)","Oklahoma City(Thunder)", 942);
        map1.addRoute("Denver(Nuggets)","Boston(Celtics)", 2845);
        
        map1.addRoute("Boston(Celtics)","Houston(Rockets)", 2584);
        map1.addRoute("Boston(Celtics)","Miami(Heat)", 3045);
        
        map1.addRoute("Miami(Heat)","Orlando(Magic)", 268);
        
        map1.addRoute("Orlando(Magic)","Houston(Rockets)", 458);

        map1.addRoute("Houston(Rockets)","Oklahoma City(Thunder)", 778);        //adding routes

        map1.printRoutes();                                         //checking routes to be added according to routes available

        return map1;                                                //returning all city nodes and routes when called
            }

    }

//class for adding cities as nodes
class City <T extends Comparable<T>, N extends Comparable<N>>{
    T cityName;                                                     //name to be called
    City<T,N> nextCity;                                             //neighbouring cities
    Route<T,N> firstRoute;                                          //routes from this city node

    public City(){                                                  //default constructors
        cityName = null;
        nextCity=null;
        firstRoute = null;
    }
    public City(T cityName, City<T,N> next){                        //constructors with city name and neighbouring city
        this.cityName = cityName;
        nextCity=next;
        firstRoute = null;
    }
}

//class for adding routes as edges
class Route<T extends Comparable<T>, N extends Comparable<N>>{
    City<T,N> toCity;                                               //the destinantion city
    N weight;                                                       //distance
    Route<T,N> nextRoute;                                           //other routes

    public Route(){                                                 //default constructors
        toCity=null;
        weight=null;
        nextRoute=null;
    }
    public Route(City<T,N> destination, N w, Route<T,N> a){         //constructors with source, dest and distance
        toCity=destination;
        weight=w;
        nextRoute=a;
    }
}

//class where the city and routes methods are implemented
class Map<T extends Comparable<T>, N extends Comparable<N>> {
    City<T,N> head;
    int size;
    public Map(){
        head=null;
        size=0;
    }

    //find the size
    public int getSize(){                                           
        return this.size;
    }

    //find if the city exist
    public boolean hasCity(T c){
        if(head==null)                                              //if head is null, automatically no city at all
            return false;
        City<T,N> temp=head;
        while(temp!=null){                                          //if head already has a city, the loop will start
            if(temp.cityName.compareTo(c)==0)
                return true;
            temp=temp.nextCity;                                     //iterate through the city class instance to find a city that has the same info
        }return false;
    }

    //adding cities
    public boolean addCity(T c){
        if(hasCity(c)==false){                                      //no such city exist yet
            City<T,N> temp = head;
            City<T,N> newCity= new City<>(c, null);            //make a new City(node) with the info
            if(head==null)                                          //if head has no city yet, head takes the newcity node
                head=newCity;
            else{                                                   //head already has a city
                City<T,N> previous = head;
                while(temp!=null){
                    previous=temp;
                    temp=temp.nextCity;                             //iterate throughout the city until the last city
                }previous.nextCity=newCity;                         //add the newcity to the pointer current last city
            }size++;
            return true;
        }else                                                       //city already exist
            return false;
    }

    //list out all cities
    public ArrayList<T> getAllCity(){
        ArrayList<T> list = new ArrayList<>();                      //create a list
        City<T,N> temp =head;                                       //start iterating from head
        while(temp!=null){                                          //while tere is city
            list.add(temp.cityName);                                //add the city names into the list
            temp=temp.nextCity;                                     //go to the next city
        }return list;                                               //return the list when called
    }

    //find if the city has this route
    public boolean hasRoute(T source, T destination){
        if(head==null)                                              //no city, no route
            return false;
        if(!hasCity(source)||(!hasCity(destination)))               //if one or both cities not exist, automatically no route
            return false;
        City<T,N> sourceCity = head;
        while(sourceCity!=null){                                    //iterate while there is city
            if(sourceCity.cityName.compareTo(source)==0){           //if the start city is at the supposed city
                Route<T,N> currentRoute = sourceCity.firstRoute;    //get the firstroute of this city
                while(currentRoute!=null){                          //while a firstroute exist
                    if(currentRoute.toCity.cityName.compareTo(destination)==0)          //if the route dest name is same
                        return true;
                    currentRoute=currentRoute.nextRoute;}           //iterate throughout the routes of this city
            }sourceCity=sourceCity.nextCity;                        //iterate throughout the city until the last city
        }return false;
    }

    //adding routes
    public boolean addRoute(T source, T destination, N w){
        if(head==null)                                              //if no city, no routes can be added
            return false;
        if(!hasCity(source)||(!hasCity(destination)))               //if one or both cities not exist, the route automatically cannot be added
            return false;
        City<T,N> sourceCity = head;
        while(sourceCity!=null){                                    //iterate while there is city
            if(sourceCity.cityName.compareTo(source)==0){           //if the start city is at the supposed source city
                City<T,N> destinationCity =head;                    //start iterating from the head city(to find the destination city)
                while(destinationCity!=null){                       //while there is a city
                    if(destinationCity.cityName.compareTo(destination)==0){         //if the destination city is reached
                        Route<T,N> forwardRoute=sourceCity.firstRoute;              //adds the route in forward direction first
                        Route<T,N> reverseRoute=destinationCity.firstRoute;         //adds another route as reverse direction for it goes both ways
                        Route<T,N> newForwardRoute = new Route<>(destinationCity, w, forwardRoute);
                        Route<T,N> newReverseRoute = new Route<>(sourceCity, w, reverseRoute);
                        sourceCity.firstRoute=newForwardRoute;
                        destinationCity.firstRoute=newReverseRoute;
                        return true;
                    }destinationCity = destinationCity.nextCity;     //find next destination city
                }
            }sourceCity=sourceCity.nextCity;                         //find next source city
        }return false;
    }

    //find neighbouring cities
    public ArrayList<T> getNeighbours(T c){
        if(!hasCity(c))                                             // if the city does not exist, cannot find the neighbouring cities
            return null;
        ArrayList<T> list = new ArrayList<T>();                     //create a list to store the neighbouring cities
        City<T,N> temp=head;                                        //start iterating from the head
        while(temp!=null){                                          //while there is a city
            if(temp.cityName.compareTo(c)==0){                      //find the city name that is called
                Route<T,N> currentRoute = temp.firstRoute;          
                while(currentRoute!=null){                          //while the city has routes
                    list.add(currentRoute.toCity.cityName);         //add the city name that the route go to
                    currentRoute=currentRoute.nextRoute;            //go to the next route
                }
            }temp=temp.nextCity;                                    //find the next city to reach the city that is called
        }return list;
    }

    //calculating distaces of each journey
    public N getRouteWeight(T source, T destination){
        N notFound=null;                                            
        if(head==null)                                              //if there is no head, distance cannot be calculated
            return notFound;
        if(!hasCity(source)||!hasCity(destination))                 //if one or both cities not exist, the distance automatically cannot be calculated
            return notFound;
        City<T,N> sourceCity=head;
        while(sourceCity!=null){
            if(sourceCity.cityName.compareTo(source)==0){
                Route<T,N> currentRoute=sourceCity.firstRoute;
                while(currentRoute!=null){
                    if(currentRoute.toCity.cityName.compareTo(destination)==0)
                        return currentRoute.weight;
                    currentRoute=currentRoute.nextRoute;
                }
            }sourceCity=sourceCity.nextCity;
        }return notFound;
    }

    //print routes in terminal for checking
    public void printRoutes(){
        City<T,N> temp=head;
        while(temp!=null){                                          //start iterating through all cities from head while the cities are not null
            System.out.println("# "+temp.cityName+" : ");           //format
            Route<T,N> currentRoute = temp.firstRoute;              //find routes from this city   
            while(currentRoute!=null){                              //iterate while there is route in this city
                System.out.println("["+ temp.cityName+","+currentRoute.toCity.cityName+"]: "+       //format
                        getRouteWeight(temp.cityName,currentRoute.toCity.cityName)+"km");           //route distance format
                currentRoute=currentRoute.nextRoute;                //find the next route of this city and go back to loop if there is route
            }System.out.println();
            temp=temp.nextCity;
        }
    }
}