package ch.epfl.isochrone.gui;

import ch.epfl.isochrone.geo.PointOSM;
import ch.epfl.isochrone.geo.PointWGS84;
import ch.epfl.isochrone.tiledmap.*;
import ch.epfl.isochrone.timetable.Date;
import ch.epfl.isochrone.timetable.Date.Month;
import ch.epfl.isochrone.timetable.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * Classe principale du programme, gérant l'UI et appelant les autres classes quand nécessaire
 * @author Tristan Deloche (234045)
 */
public final class IsochroneTL {
    private static final String OSM_TILE_URL = "http://b.tile.openstreetmap.org/";
    private static final PointWGS84 INITIAL_POSITION = new PointWGS84(Math.toRadians(6.476), Math.toRadians(46.613));
    private static final String INITIAL_STARTING_STOP_NAME = "Lausanne-Flon";
    private static final Date INITIAL_DATE = new Date(1, Month.OCTOBER, 2013);
    private static final int INITIAL_ZOOM = 11;
    private static final int INITIAL_DEPARTURE_TIME = SecondsPastMidnight.fromHMS(6, 8, 0);
    private static final int WALKING_TIME = 5 * 60;
    private static final double WALKING_SPEED = 1.25;
    private static final double OPACITY = 0.5;

    private final TiledMapComponent tiledMapComponent;
    private final TileProvider bgTileProvider;
    private TileProvider cachedIsochroneTileProvider;

    private Stop startingStop;
    private Date departureDate;
    private TimeTable timeTable;
    private TimeTableReader timeTableReader;
    private Graph graph;
    private FastestPathTree fastestPathTree;
    private Set<Stop> stopSet;
    private int departureTime;

    private Point mousePositionBeforeMove = new Point();
    private Point viewPositionBeforeMove = new Point();

    /**
     * Constructeur principal de la classe
     * @throws IOException
     */
    private IsochroneTL() throws IOException {
        departureDate = INITIAL_DATE;
        departureTime = INITIAL_DEPARTURE_TIME;
        timeTableReader = new TimeTableReader("/time-table/");
        timeTable = timeTableReader.readTimeTable();

        timeTable.stops().parallelStream()
                .filter(aStop ->
                        aStop.name().equals(INITIAL_STARTING_STOP_NAME)
                )
                .forEach(aStop -> startingStop = aStop);

        tiledMapComponent = new TiledMapComponent(INITIAL_ZOOM);

        updateDate();
        updateFastestPathTree();

        bgTileProvider = new CachedTileProvider(new OSMTileProvider(new URL(OSM_TILE_URL)));

        refreshProviders();
    }

    private JComponent createCenterPanel() {
        final JViewport viewPort = new JViewport();
        viewPort.setView(tiledMapComponent);
        PointOSM startingPosOSM = INITIAL_POSITION.toOSM(tiledMapComponent.zoom());
        viewPort.setViewPosition(new Point(startingPosOSM.roundedX(), startingPosOSM.roundedY()));

        final JPanel copyrightPanel = createCopyrightPanel();

        final JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(400, 300));

        layeredPane.add(viewPort, new Integer(0));
        layeredPane.add(copyrightPanel, new Integer(1));

        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                final Rectangle newBounds = layeredPane.getBounds();
                viewPort.setBounds(newBounds);
                copyrightPanel.setBounds(newBounds);

                viewPort.revalidate();
                copyrightPanel.revalidate();
            }
        });

        // Détecte la position du pointeur
        layeredPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePositionBeforeMove.setLocation(e.getLocationOnScreen());
                viewPositionBeforeMove.setLocation(viewPort.getViewPosition());
            }
        });

        // Détecte les mouvements de la souris quand le bouton gauche est pressé
        layeredPane.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point newPosition = new Point();
                newPosition.setLocation(viewPositionBeforeMove.getX() - (e.getLocationOnScreen().getX() - mousePositionBeforeMove.getX()), viewPositionBeforeMove.getY() - (e.getLocationOnScreen().getY() - mousePositionBeforeMove.getY()));
                viewPort.setViewPosition(newPosition);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });

        // Détection des mouvements de la roulette de la souris pour adapter le zoom
        layeredPane.addMouseWheelListener(e -> {
            Point cursorPosition = e.getPoint();
            int oldZoom = tiledMapComponent.zoom();
            int newZoom = oldZoom - e.getWheelRotation();

            if (newZoom > 19) {
                newZoom = 19;
            } else if (newZoom < 10) {
                newZoom = 10;
            }

            tiledMapComponent.setZoom(newZoom);

            PointOSM unzoomedPointOSM = new PointOSM(oldZoom, cursorPosition.getX() + viewPort.getViewPosition().getX(), cursorPosition.getY() + viewPort.getViewPosition().getY());
            PointOSM zoomedPointOSM = unzoomedPointOSM.atZoom(newZoom);
            viewPort.setViewPosition(new Point(zoomedPointOSM.roundedX() - cursorPosition.x, zoomedPointOSM.roundedY() - cursorPosition.y));
        });

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(layeredPane, BorderLayout.CENTER);
        return centerPanel;
    }

    // Création du panel contenant les éléments de contrôle (Arrêts et Date/Heure)
    private JComponent createManagingPanel() {
        JLabel startLabel = new JLabel("Starting stop : ");
        JLabel dateLabel = new JLabel("Date and hour (DD/MM/YY) : ");

        List<Stop> orderedStopList = new LinkedList<>(stopSet);
        Collections.sort(orderedStopList, (stop1, stop2) -> stop1.name().compareTo(stop2.name()));

        Vector<Stop> stopsVector = new Vector<>(orderedStopList);
        final JComboBox<Stop> stopsJComboBox = new JComboBox<>(stopsVector);
        stopsJComboBox.setSelectedItem(startingStop);

        final SpinnerDateModel spinnerDateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(spinnerDateModel);

        java.util.Date javaDate = departureDate.toJavaDate();
        //noinspection deprecation
        javaDate.setHours(SecondsPastMidnight.hours(departureTime));
        //noinspection deprecation
        javaDate.setMinutes(SecondsPastMidnight.minutes(departureTime));
        dateSpinner.setValue(javaDate);

        JPanel myPanel = new JPanel(new FlowLayout());

        myPanel.add(startLabel);
        myPanel.add(stopsJComboBox);
        myPanel.add(new JSeparator());
        myPanel.add(dateLabel);
        myPanel.add(dateSpinner);

        // Détecte une modification de la date ou de l'heure
        spinnerDateModel.addChangeListener(e -> {
            Date modelDate = new Date(spinnerDateModel.getDate());
            int departureTime1 = SecondsPastMidnight.fromJavaDate(spinnerDateModel.getDate());

            try {
                setDateAndDepartureTime(modelDate, departureTime1);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        // Détecte et applique un changement de l'arrêt de départ
        stopsJComboBox.addActionListener(e -> {
            try {
                setStartingStop((Stop) stopsJComboBox.getSelectedItem());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        return myPanel;
    }

    private JPanel createCopyrightPanel() {
        Icon tlIcon = new ImageIcon(getClass().getResource("/images/tl-logo.png"));
        String copyrightText = "TL's Data (2013)/ ©OpenStreetMap contributors / Program by Tristan Deloche";
        JLabel copyrightLabel = new JLabel(copyrightText, tlIcon, SwingConstants.CENTER);
        copyrightLabel.setOpaque(true);
        copyrightLabel.setForeground(new Color(1f, 1f, 1f, 0.6f));
        copyrightLabel.setBackground(new Color(0f, 0f, 0f, 0.4f));
        copyrightLabel.setBorder(BorderFactory.createEmptyBorder(3, 0, 5, 0));

        JPanel copyrightPanel = new JPanel(new BorderLayout());
        copyrightPanel.add(copyrightLabel, BorderLayout.PAGE_END);
        copyrightPanel.setOpaque(false);
        return copyrightPanel;
    }

    private void start() {
        JFrame frame = new JFrame("Isochrone TL");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(createCenterPanel(), BorderLayout.CENTER);
        frame.getContentPane().add(createManagingPanel(), BorderLayout.PAGE_START);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String... args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                new IsochroneTL().start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // Applique la correction d'horaires dûe aux horaire non-civils utilisés par les TL (4h/4h au lieu de 0h/24h)
    private void setDateAndDepartureTime(Date newDepartureDate, int newDepartureTime) throws IOException {
        if (SecondsPastMidnight.hours(newDepartureTime) < 4) {
            newDepartureTime += SecondsPastMidnight.fromHMS(24,0,0);
            setDepartureTime(newDepartureTime);
            setDepartureDate(newDepartureDate.relative(-1));
        } else {
            setDepartureTime(newDepartureTime);
            setDepartureDate(newDepartureDate);
        }
    }

    // Change la date de recherche
    private void setDepartureDate(Date newDepartureDate) throws IOException {
        if (!departureDate.equals(newDepartureDate)) {
            Date oldDate = departureDate;
            departureDate = newDepartureDate;

            if (!timeTable.servicesForDate(oldDate).equals(timeTable.servicesForDate(newDepartureDate))) {
                updateDate();
                refreshProviders();
            }
        }
    }

    // Change l'heure de départ
    private void setDepartureTime(int newDepartureTime) throws IOException {
        if (newDepartureTime != departureTime) {
            departureTime = newDepartureTime;
            updateFastestPathTree();
            refreshProviders();
        }
    }

    // Change le stop de départ
    private void setStartingStop(Stop newStartingStop) throws IOException {
        if (!startingStop.equals(newStartingStop)) {
            startingStop = newStartingStop;
            updateFastestPathTree();
            refreshProviders();
        }
    }

    // Génère une carte isochrone à l'aide des attributs privés de la classe qui sont modifiées par les méthodes de contrôle de l'UI
    private IsochroneTileProvider makeIsochroneTileProvider() throws IOException {
        LinkedList<Color> colorLinkedList = new LinkedList<>();
        colorLinkedList.add(new Color(0, 0, 0));
        colorLinkedList.add(new Color(0, 0, 255));
        colorLinkedList.add(new Color(0, 255, 0));
        colorLinkedList.add(new Color(255, 255, 0));
        colorLinkedList.add(new Color(255, 0, 0));
        ColorTable myColorTable = new ColorTable(5, colorLinkedList);

        return new IsochroneTileProvider(fastestPathTree, myColorTable, WALKING_SPEED);
    }

    // MÀJ la carte isochrone en fonction de la nouvelle date de recherche
    private void updateDate() throws IOException {
        String[] argDateArray = departureDate.toString().split("-");
        Integer[] dateArray = new Integer[3];

        for (int i = 0; i < argDateArray.length; i++) {
            dateArray[i] = Integer.parseInt(argDateArray[i]);
        }
        Set<Service> serviceSet = timeTable.servicesForDate(new Date(dateArray[2], dateArray[1], dateArray[0]));

        stopSet = new HashSet<>(timeTable.stops());
        graph = timeTableReader.readGraphForServices(stopSet, new HashSet<>(serviceSet), Integer.parseInt(Integer.toString(WALKING_TIME)), Double.parseDouble(Double.toString(WALKING_SPEED)));
        updateFastestPathTree();
    }

    // MÀJ le FastestPathTree en fonction des nouveaux paramètres
    private void updateFastestPathTree() throws IOException {
        List<Stop> stopList = new LinkedList<>();
        stopList.addAll(stopSet);
        Collections.sort(stopList, (s1, s2) -> s1.name().compareTo(s2.name()));
        Stop firstStop = new Stop("NULL", null);
        for (Stop aStop : stopList) {
            if (aStop.name().equals(startingStop.name())) {
                firstStop = aStop;
            }
        }
        String[] hourArray = SecondsPastMidnight.toString(departureTime).split(":");
        fastestPathTree = graph.fastestPaths(firstStop, SecondsPastMidnight.fromHMS(Integer.parseInt(hourArray[0]), Integer.parseInt(hourArray[1]), Integer.parseInt(hourArray[2])));
    }

    // Refresh le TiledMapComponent une fois les nouveaux providers créés
    private void refreshProviders() throws IOException {
        tiledMapComponent.clearProviders();
        tiledMapComponent.addProvider(bgTileProvider);
        cachedIsochroneTileProvider = new CachedTileProvider(new TransparentTileProvider(makeIsochroneTileProvider(), OPACITY));
        tiledMapComponent.addProvider(cachedIsochroneTileProvider);
    }
}