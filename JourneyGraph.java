import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.time.Year;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class JourneyGraph extends JFrame {
    final private Font font = new Font("Raleway", Font.PLAIN, 12);             // to customize font
    final private Dimension dimension = new Dimension(150, 50);
    final private Dimension maxSize = new Dimension(600, 300);
    final private Dimension minSize = new Dimension(400, 100);

    private JPanel navigationPanel;
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private JTable routeTable;
    private DefaultTableModel tableModel;
    private RouteDistance<String, Integer> routeDistance;

    public JourneyGraph() {
        initialize();
    }

    private void initialize() {
        setTitle("Journey Plan");                                               //name for every page
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
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout());
        //contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        //contentPanel.setPreferredSize(new Dimension(50, 500));

        //map image panel
        JLabel imageLabel = loadImage("NBAMap.png", true,450, 260);
        JPanel mapPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        mapPanel.add(imageLabel);

        routeDistance = new RouteDistance<>(JourneyRoutes()); // Assuming map1 is the graph used for route calculation
        String startCity = "San Antonio(Spurs)";

        ArrayList<ArrayList<String>> dfsRoutes = routeDistance.depthFirstSearch(startCity);
        int[] totalDistance = new int[dfsRoutes.size()];;
        int i=0;
        for (ArrayList<String> route : dfsRoutes) {
                        totalDistance[i] = calculateTotalDistance(route, JourneyRoutes());
                        i++;
        }
        Arrays.sort(totalDistance);

        //BEST ROUTE DISPLAY
        JPanel bestRoutePanel = new JPanel();
        bestRoutePanel.setLayout(new BoxLayout(bestRoutePanel, BoxLayout.Y_AXIS));

        JLabel bestRouteLabel = new JLabel("Best Route:");
        bestRouteLabel.setFont(new Font("Arial", Font.BOLD, 15));

if(!dfsRoutes.isEmpty()){
        int firstTotalDistance = totalDistance[0];
        for (ArrayList<String> route : dfsRoutes){
            if(firstTotalDistance==calculateTotalDistance(route, JourneyRoutes())){            
                StringBuilder firstFormattedRoute = new StringBuilder();
            for (String city : route) {
                firstFormattedRoute.append(city).append(" -> ");
            }
            firstFormattedRoute.setLength(firstFormattedRoute.length() - 4);
            JLabel firstRouteLabel = new JLabel(firstFormattedRoute.toString()+ "-" +firstTotalDistance);
           // firstRouteLabel.setPreferredSize(new Dimension(1000, 80)); // Set preferred size to accommodate the longer label
            firstRouteLabel.setVerticalAlignment(SwingConstants.TOP); // Align label content to the top
            firstRouteLabel.setHorizontalAlignment(SwingConstants.LEFT); // Align label content to the left
            firstRouteLabel.setOpaque(false); // Ensure label is opaque so background color shows
            firstRouteLabel.setBackground(Color.LIGHT_GRAY); // Set background color to match panel background
            firstRouteLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding
            firstRouteLabel.setText("<html>" + 1 +". "+firstFormattedRoute.toString() + "<br>" +"Distance: "+ firstTotalDistance +" km"+ "</html>"); // Use HTML to create multiline label
            firstRouteLabel.setFont(font);
            bestRoutePanel.add(firstRouteLabel); 

            JFrame bestMapFrame = new JFrame("Best Route Map");
            //JLabel bestMapImage = loadImage("2024-05-25 (2).png", true,400, 250);
            //bestMapFrame.add(bestMapImage);

            JButton bestButton = new JButton("View Map");
            bestButton.addActionListener(e->showPopup(bestMapFrame,0,firstFormattedRoute));

            bestRoutePanel.add(bestButton);
        }
    }}
        JPanel otherRoutesPanel = new JPanel();
        otherRoutesPanel.setLayout(new BoxLayout(otherRoutesPanel,1));

        JLabel otherRouteLabel = new JLabel("Other Routes:");
        otherRouteLabel.setFont(new Font("Arial", Font.BOLD, 15));
        //otherRoutesPanel.add(otherRouteLabel);

        i=0;
        for (ArrayList<String> route : dfsRoutes) {
            totalDistance[i] = calculateTotalDistance(route, JourneyRoutes());
            i++;
        }
        Arrays.sort(totalDistance);//arrange so that the lowest distances at front array
        for(i=1; i<5; i++){
        for (ArrayList<String> route : dfsRoutes){
            StringBuilder formattedRoute = new StringBuilder();
            if(totalDistance[i]==calculateTotalDistance(route, JourneyRoutes())){
                for (String city : route) {
                    formattedRoute.append(city).append(" -> ");
                }
                formattedRoute.setLength(formattedRoute.length() - 4);
                JLabel routeLabel=new JLabel(formattedRoute.toString()+" - "+totalDistance[i]);
                //routeLabel.setPreferredSize(new Dimension(1000, 80)); // Set preferred size to accommodate the longer label
                routeLabel.setVerticalAlignment(SwingConstants.TOP); // Align label content to the top
                routeLabel.setHorizontalAlignment(SwingConstants.LEFT); // Align label content to the left
                routeLabel.setOpaque(false); // Ensure label is opaque so background color shows
                routeLabel.setBackground(Color.LIGHT_GRAY); // Set background color to match panel background
                routeLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding
                routeLabel.setText("<html>" + (i+1) +". " + formattedRoute.toString() + "<br>" +"Distance: "+ totalDistance[i] + " km"+"</html>"); // Use HTML to create multiline label
                routeLabel.setFont(font);
                otherRoutesPanel.add(routeLabel); 

                JFrame MapFrame = new JFrame("Route "+(i+1) +" Map");
                //JLabel bestMapImage = loadImage("2024-05-25 (2).png", true,400, 250);
                //bestMapFrame.add(bestMapImage);

                JButton Button = new JButton("View Map");
                int routeIndex =i;
                Button.addActionListener(e->showPopup(MapFrame,routeIndex,formattedRoute));

                otherRoutesPanel.add(Button);
                //tableModel.addRow(new Object[]{routeLabel, totalDistance[i]});
            }
        }
    }

        // Call DFS
        //ArrayList<ArrayList<String>> dfsRoutes = routeDistance.depthFirstSearch(startCity);

        /*JPanel dfspanel = new JPanel();
        dfspanel.setLayout(new BorderLayout());
        JLabel totalDistanceLabel =new JLabel();
        
        int journeyNumber=1;
        dfspanel.setLayout(new BoxLayout(dfspanel, BoxLayout.Y_AXIS));
        JLabel dfslabel = new JLabel("Depth First Search Method:");
        dfslabel.setFont(new Font("Arial", Font.BOLD, 15));
        dfslabel.setPreferredSize(new Dimension(200, 30)); // Adjust dimensions as needed
        dfspanel.add(dfslabel);
        if (!dfsRoutes.isEmpty()) {
            for (ArrayList<String> journey : dfsRoutes) {
                JPanel journeyRoutePanel1 = new JPanel(new GridLayout(2, 1));
                totalDistanceLabel = new JLabel();
                StringBuilder journeyString = new StringBuilder("Journey " + journeyNumber + ": ");
                for (String city : journey) {
                    journeyString.append(city).append(" -> ");
                }
                journeyString.setLength(journeyString.length() - 4); // Remove the last " -> "
                JLabel journeyLabel = new JLabel(journeyString.toString());
                journeyRoutePanel1.add(journeyLabel, BorderLayout.NORTH);
                int totalDistance = calculateTotalDistance(journey, JourneyRoutes());
                totalDistanceLabel = new JLabel("Total Distance: " + totalDistance);
                journeyRoutePanel1.add(totalDistanceLabel, BorderLayout.CENTER);
                dfspanel.add(journeyRoutePanel1);
                journeyNumber++;
            }
        } else {
            totalDistanceLabel = new JLabel("No journey consisting of all cities found.");
            dfspanel.add(totalDistanceLabel, BorderLayout.CENTER);
        }
        JScrollPane scrollPane = new JScrollPane(dfspanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        /* 
        //Call BFS
        ArrayList<ArrayList<String>> bfsRoutes = routeDistance.breadthFirstSearch(startCity);
        JPanel bfspanel = new JPanel();
        bfspanel.setLayout(new BorderLayout());
        
        JLabel bfslabel = new JLabel("Breadth First Search Method:");
        bfslabel.setFont(new Font("Arial", Font.BOLD, 15));
        bfspanel.add(bfslabel);
        journeyNumber = 1;
        JPanel journeyRoutePanel2 = new JPanel(new BorderLayout());
        if(!bfsRoutes.isEmpty()){
            for (ArrayList<String> journey : bfsRoutes) {
                journeyRoutePanel2 = new JPanel(new BorderLayout());
                StringBuilder journeyString = new StringBuilder("Journey " + journeyNumber + ": ");
                for (String city : journey) {
                    journeyString.append(city).append(" -> ");
                }
                journeyString.setLength(journeyString.length() - 4);
                JLabel journeyLabel = new JLabel(journeyString.toString());
                journeyRoutePanel2.add(journeyLabel, BorderLayout.NORTH);
                int totalDistance = calculateTotalDistance(journey, JourneyRoutes());
                totalDistanceLabel = new JLabel("Total Distance: " + totalDistance);
                journeyRoutePanel2.add(totalDistanceLabel, BorderLayout.CENTER);
                bfspanel.add(journeyRoutePanel2); 
                journeyNumber++;
                }
        }else{
            journeyRoutePanel2 = new JPanel(new BorderLayout());
            totalDistanceLabel = new JLabel("No journey consisting of all cities found.");
            journeyRoutePanel2.add(totalDistanceLabel, BorderLayout.CENTER);
            bfspanel.add(journeyRoutePanel2);
        }
        ArrayList<String> bfsTraversal = routeDistance.bfsTraversal(startCity);
        StringBuilder journeyString = new StringBuilder("Journey :"+(journeyNumber-1) +":");
        for (int i = 0; i < bfsTraversal.size(); i++) {
            journeyString.append(bfsTraversal.get(i)).append(" -> ");
        }
            journeyString.setLength(journeyString.length() - 4);
            JLabel journeyLabel = new JLabel(journeyString.toString());
            journeyRoutePanel2.add(journeyLabel, BorderLayout.NORTH);
            bfspanel.add(journeyRoutePanel2);*/

        // Calculate total distance
        //int totalDistance = calculateTotalDistance(bfsTraversal, map1);

        // Print total distance
        //System.out.println("Total Distance: " + totalDistance);

        //JPanel journeyPanel = new JPanel(new GridLayout(2, 1, 10, 5));
        //journeyPanel.setBorder(new TitledBorder("Journey Distances"));
        //journeyPanel.add(bfspanel);
        //journeyPanel.add(scrollPane);

        contentPanel.add(mapPanel,BorderLayout.NORTH);

        JPanel bestRouteWrapperPanel = createBorderedPanel("B E S T  R O U T E : ");
        bestRouteWrapperPanel.add(bestRoutePanel, BorderLayout.CENTER);

        //new JPanel(new BorderLayout());
//bestRouteWrapperPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Best Route"));
//bestRouteWrapperPanel.add(bestRoutePanel, BorderLayout.CENTER);

// OTHER ROUTES DISPLAY
JPanel otherRoutesWrapperPanel = createBorderedPanel("O T H E R  R O U T E S : ");
        otherRoutesWrapperPanel.add(otherRoutesPanel, BorderLayout.CENTER);
//JPanel otherRoutesWrapperPanel = new JPanel(new BorderLayout());
//otherRoutesWrapperPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Other Routes"));
//otherRoutesWrapperPanel.add(otherRoutesPanel, BorderLayout.CENTER);

        //bestRoutePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        //otherRoutesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel routePanels = new JPanel();
        routePanels.setLayout(new BoxLayout(routePanels, BoxLayout.X_AXIS));;
        //contentPanel.add(bestRoutePanel);
        //contentPanel.add(otherRoutesPanel);
        //contentPanel.add(dfsPanel);
        //contentPanel.add(scrollPane, BorderLayout.SOUTH);
        routePanels.add(bestRouteWrapperPanel);
        routePanels.add(otherRoutesWrapperPanel);

        contentPanel.add(routePanels);
        //contentPanel.add(otherRoutesWrapperPanel);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(navigationPanel, BorderLayout.NORTH);
        getContentPane().add(sidebarPanel, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);  

    }

    private JPanel createBorderedPanel(String title) {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());

    // Create an empty border with padding around the panel
    EmptyBorder border = new EmptyBorder(10, 10, 10, 10); // Adjust padding as needed
    panel.setBorder(border);

    // Add a titled border to the panel
    TitledBorder titledBorder = new TitledBorder(title);
    titledBorder.setTitlePosition(TitledBorder.DEFAULT_POSITION);
    panel.setBorder(BorderFactory.createCompoundBorder(titledBorder, BorderFactory.createEmptyBorder(5, 5, 5, 5))); // Adjust padding as needed

    return panel;
}
    private void showPopup(JFrame frame, int a, StringBuilder b){
        String imageName="";
        switch(a){
            case(0):
                imageName = "bestRouteMap.png";
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
        JLabel bestMapImage = loadImage(imageName, true,400, 250);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));

        String[] bStrings = b.toString().split("->");
        StringBuilder route = new StringBuilder();
            for (String city : bStrings) {
                route.append(city).append("<br>");
            }
        JLabel routeLabel = new JLabel(route.toString());
        routeLabel.setText("<html>"+ route.toString() + "<br>" + "</html>"); // Use HTML to create multiline label
        routeLabel.setFont(new Font("Monospace", Font.BOLD, 14));
        panel.add(routeLabel);
        panel.add(bestMapImage);
       // panel.add(backButton, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(frame, panel, "Best Route Map", JOptionPane.PLAIN_MESSAGE);

        // Add action listener to the back button
        //backButton.addActionListener(e -> {
            // Handle back button click here
            //JOptionPane.getRootFrame().dispose(); // Close the popup
        //});
    }

    private static int calculateTotalDistance(ArrayList<String> journey, Map<String, Integer> map) {
        int totalDistance = 0;
        for (int i = 0; i < journey.size() - 1; i++) {
            String currentCity = journey.get(i);
            String nextCity = journey.get(i + 1);
            totalDistance += map.getRouteWeight(currentCity, nextCity);
        }
        return totalDistance;
    }

    private void handleSidebarButtonClick(String label) {
        this.dispose();
        switch (label) {
            case "HOME":
                new Home();
                break;
            case "TEAM":
                new MyTeam();
                break;
            case "PLAYER":
                new Player_(); 
                break;
            case "JOURNEY":
                new JourneyGraph();
                break;
            case "CONTRACT":
                new Contract();
                break;
            case "INJURY":
                new Injury();
                break;
        }
    }
    private JLabel loadImage(String fileName, boolean isResized, int targetWidth, int targetHeight) {
        BufferedImage image;
        JLabel imageContainer;

        try {
            image = ImageIO.read(new File(fileName));

            if (isResized) {
                image = resizeImage(image, targetWidth, targetHeight);
            }

            imageContainer = new JLabel(new ImageIcon(image));
            return imageContainer;
        } catch (Exception e) {
            System.out.println("Error: " + e);
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

    public Map<String, Integer> JourneyRoutes(){
        Map<String,Integer> map1 = new Map<>();
        String[] cities = {"San Antonio(Spurs)","Golden State(Warriors)","Boston(Celtics)",
            "Miami(Heat)","Los Angeles(Lakers)","Phoenix(Suns)","Orlando(Magic)",
            "Denver(Nuggets)","Oklahoma City(Thunder)","Houston(Rockets)"};
        for(String i:cities)
            map1.addCity(i);

        System.out.println("Number of cities: "+map1.getSize());

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

        map1.addRoute("Houston(Rockets)","Oklahoma City(Thunder)", 778);

        map1.printRoutes();

        return map1;
            }

    }
class City <T extends Comparable<T>, N extends Comparable<N>>{
    T cityName;
    int indeg;
    int outdeg;
    City<T,N> nextCity;
    Route<T,N> firstRoute;

    public City(){
        cityName = null;
        indeg=0;
        outdeg=0;
        nextCity=null;
        firstRoute = null;
    }
    public City(T cityName, City<T,N> next){
        this.cityName = cityName;
        indeg=0;
        outdeg=0;
        nextCity=next;
        firstRoute = null;
    }
}
class Route<T extends Comparable<T>, N extends Comparable<N>>{
    City<T,N> toCity;
    N weight;
    Route<T,N> nextRoute;

    public Route(){
        toCity=null;
        weight=null;
        nextRoute=null;
    }
    public Route(City<T,N> destination, N w, Route<T,N> a){
        toCity=destination;
        weight=w;
        nextRoute=a;
    }
}

class Map<T extends Comparable<T>, N extends Comparable<N>> {
    City<T,N> head;
    int size;
    public Map(){
        head=null;
        size=0;
    }
    public int getSize(){
        return this.size;
    }
    public boolean hasCity(T c){
        if(head==null)//if head is null, theother is also null, auto no city
            return false;
        City<T,N> temp=head;
        while(temp!=null){//if head already has a city, the loop will start
            if(temp.cityName.compareTo(c)==0)
                return true;
            temp=temp.nextCity;//iterate through the city class instance to find a city that has the same info
        }return false;
    }
    public boolean addCity(T c){
        if(hasCity(c)==false){//no such city exist yet
            City<T,N> temp = head;
            City<T,N> newCity= new City<>(c, null);//make a new City(node) with the info
            if(head==null)//if head has no city yet, head takes the newcity node
                head=newCity;
            else{//head already has a city
                City<T,N> previous = head;
                while(temp!=null){
                    previous=temp;
                    temp=temp.nextCity;//iterate throughthe city until the last city
                }previous.nextCity=newCity;
            }size++;
            return true;
        }else//city already exist
            return false;
    }
    public ArrayList<T> getAllCity(){
        ArrayList<T> list = new ArrayList<>();
        City<T,N> temp =head;
        while(temp!=null){
            list.add(temp.cityName);
            temp=temp.nextCity;
        }return list;
    }
    public boolean hasRoute(T source, T destination){
        if(head==null)//no city, no route
            return false;
        if(!hasCity(source)||(!hasCity(destination)))//if both cities not exist
            return false;
        City<T,N> sourceCity = head;
        while(sourceCity!=null){//iterate while there is city
            if(sourceCity.cityName.compareTo(source)==0){//if the start city is at the supposed city
                Route<T,N> currentRoute = sourceCity.firstRoute;
                while(currentRoute!=null){//if theres no route yet
                    if(currentRoute.toCity.cityName.compareTo(destination)==0)//if the route dest name is same
                        return true;
                    currentRoute=currentRoute.nextRoute;//iterate
                }
            }sourceCity=sourceCity.nextCity;
        }return false;
    }
    public boolean addRoute(T source, T destination, N w){
        if(head==null)//no city, no route
            return false;
        if(!hasCity(source)||(!hasCity(destination)))//if both cities not exist
            return false;
        City<T,N> sourceCity = head;
        while(sourceCity!=null){//iterate while there is city
            if(sourceCity.cityName.compareTo(source)==0){//if the start city is at the supposed city
                City<T,N> destinationCity =head;
                while(destinationCity!=null){
                    if(destinationCity.cityName.compareTo(destination)==0){
                        Route<T,N> forwardRoute=sourceCity.firstRoute;
                        Route<T,N> reverseRoute=destinationCity.firstRoute;
                        Route<T,N> newForwardRoute = new Route<>(destinationCity, w, forwardRoute);
                        Route<T,N> newReverseRoute = new Route<>(sourceCity, w, reverseRoute);
                        sourceCity.firstRoute=newForwardRoute;
                        destinationCity.firstRoute=newReverseRoute;
                        return true;
                    }destinationCity = destinationCity.nextCity;
                }
            }sourceCity=sourceCity.nextCity;
        }return false;
    }
    public ArrayList<T> getNeighbours(T c){
        if(!hasCity(c))
            return null;
        ArrayList<T> list = new ArrayList<T>();
        City<T,N> temp=head;
        while(temp!=null){
            if(temp.cityName.compareTo(c)==0){
                Route<T,N> currentRoute = temp.firstRoute;
                while(currentRoute!=null){//while the city has route
                    list.add(currentRoute.toCity.cityName);
                    currentRoute=currentRoute.nextRoute;
                }
            }temp=temp.nextCity;
        }return list;

    }
    public N getRouteWeight(T source, T destination){
        N notFound=null;
        if(head==null)
            return notFound;
        if(!hasCity(source)||!hasCity(destination))
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
    public void printRoutes(){
        City<T,N> temp=head;
        while(temp!=null){
            System.out.println("# "+temp.cityName+" : ");
            Route<T,N> currentRoute = temp.firstRoute;
            while(currentRoute!=null){
                System.out.println("["+ temp.cityName+","+currentRoute.toCity.cityName+"]: "+
                        getRouteWeight(temp.cityName,currentRoute.toCity.cityName)+"km");
                currentRoute=currentRoute.nextRoute;
            }System.out.println();
            temp=temp.nextCity;
        }
    }
}