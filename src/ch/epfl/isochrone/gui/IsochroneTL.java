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

public final class IsochroneTL {
    private static final String OSM_TILE_URL = "http://b.tile.openstreetmap.org/";
    private static final int INITIAL_ZOOM = 11;
    private static final PointWGS84 INITIAL_POSITION = new PointWGS84(Math.toRadians(6.476), Math.toRadians(46.613));
    private static final String INITIAL_STARTING_STOP_NAME = "Lausanne-Flon";
    private static final int INITIAL_DEPARTURE_TIME = SecondsPastMidnight.fromHMS(6, 8, 0);
    private static final Date INITIAL_DATE = new Date(1, Month.OCTOBER, 2013);
    private static final int WALKING_TIME = 5 * 60;
    private static final double WALKING_SPEED = 1.25;
    private static final double OPACITY = 0.5;

    private Stop startingStop;
    private Date departureDate;
    private int departureTime;
    private TimeTable timeTable;
    public TimeTableReader timeTableReader;
    private Graph graph;
    private FastestPathTree fastestPathTree;
    private Set<Stop> stopSet;

    private final TiledMapComponent tiledMapComponent;
    private TileProvider cachedIsochroneTileProvider;

    private Point mousePositionBeforeMove = new Point();
    private Point viewPositionBeforeMove = new Point();

    public IsochroneTL() throws IOException {
        startingStop = new Stop(INITIAL_STARTING_STOP_NAME, INITIAL_POSITION);
        departureDate = INITIAL_DATE;
        departureTime = INITIAL_DEPARTURE_TIME;
        timeTableReader = new TimeTableReader("/time-table/");
        timeTable = timeTableReader.readTimeTable();


        TileProvider bgTileProvider = new CachedTileProvider(new OSMTileProvider(new URL(OSM_TILE_URL)));
        tiledMapComponent = new TiledMapComponent(INITIAL_ZOOM);

        cachedIsochroneTileProvider = new CachedTileProvider(new TransparentTileProvider(makeIsochroneTileProvider(), OPACITY));
        tiledMapComponent.addProvider(bgTileProvider);
        tiledMapComponent.addProvider(cachedIsochroneTileProvider);
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

        layeredPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePositionBeforeMove.setLocation(e.getLocationOnScreen());
                viewPositionBeforeMove.setLocation(viewPort.getViewPosition());
            }
        });

        layeredPane.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point newPosition = new Point();
                newPosition.setLocation(viewPositionBeforeMove.getX() - (e.getLocationOnScreen().getX() - mousePositionBeforeMove.getX()), viewPositionBeforeMove.getY() - (e.getLocationOnScreen().getY() - mousePositionBeforeMove.getY()));
                viewPort.setViewPosition(newPosition);
            }

            @Override
            public void mouseMoved(MouseEvent e){}
        });

        layeredPane.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                Point cursorPosition = e.getPoint();
                int oldZoom = tiledMapComponent.zoom();
                int newZoom = oldZoom - e.getWheelRotation();

                tiledMapComponent.setZoom(newZoom);

                PointOSM unzoomedPointOSM = new PointOSM(oldZoom, cursorPosition.getX() + viewPort.getViewPosition().getX(), cursorPosition.getY() + viewPort.getViewPosition().getY());
                PointOSM zoomedPointOSM = unzoomedPointOSM.atZoom(newZoom);
                viewPort.setViewPosition(new Point(zoomedPointOSM.roundedX() - cursorPosition.x, zoomedPointOSM.roundedY() - cursorPosition.y));
            }
        });

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(layeredPane, BorderLayout.CENTER);
        return centerPanel;
    }

    private JPanel createCopyrightPanel() {
        Icon tlIcon = new ImageIcon(getClass().getResource("/images/tl-logo.png"));
        String copyrightText = "Données horaires 2013. Source : Transports publics de la région lausannoise / Carte : © contributeurs d'OpenStreetMap";
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
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(createCenterPanel(), BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new IsochroneTL().start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setDepartureDate(Date newDepartureDate) throws IOException {
        if (departureDate != newDepartureDate) {
            Date oldDate = departureDate;
            departureDate = newDepartureDate;

            if (!timeTable.servicesForDate(oldDate).equals(timeTable.servicesForDate(newDepartureDate))) {
                cachedIsochroneTileProvider = new CachedTileProvider(new TransparentTileProvider(makeIsochroneTileProvider(), OPACITY));
            }
        }
    }

    public void setDepartureTime(int newDepartureTime) throws IOException {
        if (newDepartureTime != departureTime) {
            departureTime = newDepartureTime;
            cachedIsochroneTileProvider = new CachedTileProvider(new TransparentTileProvider(makeIsochroneTileProvider(), OPACITY));
        }
    }

    private IsochroneTileProvider makeIsochroneTileProvider() throws IOException {
        LinkedList<Color> colorLinkedList = new LinkedList<>();
        colorLinkedList.add(new Color(0, 0, 0));
        colorLinkedList.add(new Color(0, 0, 255));
        colorLinkedList.add(new Color(0, 255, 0));
        colorLinkedList.add(new Color(255, 255, 0));
        colorLinkedList.add(new Color(255, 0, 0));
        ColorTable myColorTable = new ColorTable(5, colorLinkedList);

        //NOM YYYY-MM-DD HH:MM:SS WT WS
        String[] arg = new String[5];
        arg[0] = startingStop.name();
        arg[1] = departureDate.toString();
        arg[2] = SecondsPastMidnight.toString(departureTime);
        arg[3] = Integer.toString(WALKING_TIME);
        arg[4] = Double.toString(WALKING_SPEED);

        return new IsochroneTileProvider(updateFastestPathTree(arg), myColorTable, WALKING_SPEED);
    }

    private Graph updateGraph(String[] arg) throws IOException {
        String[] argDateArray = arg[1].split("-");
        Integer[] dateArray = new Integer[3];

        for (int i = 0; i < argDateArray.length; i++) {
            dateArray[i] = Integer.parseInt(argDateArray[i]);
        }

        stopSet = new HashSet<>(timeTable.stops());

        return timeTableReader.readGraphForServices(stopSet, new HashSet<>(timeTable.servicesForDate(new Date(dateArray[2], dateArray[1], dateArray[0]))), Integer.parseInt(arg[3]), Double.parseDouble(arg[4]));
    }

    private FastestPathTree updateFastestPathTree(String[] arg) throws IOException {
        graph = updateGraph(arg);
        List<Stop> stopList = new LinkedList<>();
        stopList.addAll(stopSet);
        Collections.sort(stopList, new Comparator<Stop>() {
            @Override
            public int compare(Stop s1, Stop s2) {
                return s1.name().compareTo(s2.name());
            }
        });
        Stop firstStop = new Stop("NULL", null);
        for (Stop aStop : stopList) {
            if (aStop.name().equals(arg[0])) {
                firstStop = aStop;
            }
        }
        String[] hourArray = arg[2].split(":");
        return graph.fastestPaths(firstStop, SecondsPastMidnight.fromHMS(Integer.parseInt(hourArray[0]), Integer.parseInt(hourArray[1]), Integer.parseInt(hourArray[2])));
    }
}

