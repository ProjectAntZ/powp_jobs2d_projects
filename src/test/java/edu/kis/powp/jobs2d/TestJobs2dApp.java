package edu.kis.powp.jobs2d;

import edu.kis.legacy.drawer.panel.DrawPanelController;
import edu.kis.legacy.drawer.shape.LineFactory;
import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.command.gui.CommandManagerWindow;
import edu.kis.powp.jobs2d.command.gui.CommandManagerWindowCommandChangeObserver;
import edu.kis.powp.jobs2d.command.visitor.Canvas;
import edu.kis.powp.jobs2d.command.visitor.CanvasFactory;
import edu.kis.powp.jobs2d.drivers.DriverInfoUpdater;
import edu.kis.powp.jobs2d.drivers.TransformationDriver;
import edu.kis.powp.jobs2d.drivers.adapter.LineDriverAdapter;
import edu.kis.powp.jobs2d.drivers.composite.DriverComposite;
import edu.kis.powp.jobs2d.drivers.composite.IDriverComposite;
import edu.kis.powp.jobs2d.drivers.transformation.Rotate;
import edu.kis.powp.jobs2d.drivers.transformation.Scale;
import edu.kis.powp.jobs2d.drivers.usageMonitor.MonitorDriverDecorator;
import edu.kis.powp.jobs2d.drivers.usageMonitor.UsageMonitorManager;
import edu.kis.powp.jobs2d.events.*;
import edu.kis.powp.jobs2d.features.*;
import edu.kis.powp.jobs2d.observer.CheckboxAction;
import edu.kis.powp.jobs2d.observer.MouseControlLoggerObserver;
import edu.kis.powp.jobs2d.observer.MouseControlObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestJobs2dApp {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);


    /**
     * Setup test concerning preset figures in context.
     *
     * @param application Application context.
     */
    private static void setupPresetTests(Application application) {
        SelectTestFigureOptionListener selectTestFigureOptionListener = new SelectTestFigureOptionListener(
                DriverFeature.getDriverManager());
        SelectTestFigure2OptionListener selectTestFigure2OptionListener = new SelectTestFigure2OptionListener(
                DriverFeature.getDriverManager());

        application.addTest("Figure Joe 1", selectTestFigureOptionListener);
        application.addTest("Figure Joe 2", selectTestFigure2OptionListener);
    }

    /**
     * Setup test using driver commands in context.
     *
     * @param application Application context.
     */
    private static void setupCommandTests(Application application) {
        Canvas paperA4 = CanvasFactory.getCanvasA4();
        Canvas paperA5 = CanvasFactory.getCanvasA5();
        application.addTest("Canvas checker A4", new SelectCommandCanvasVisitorListener(DriverFeature.getDriverManager(), paperA4));
        application.addTest("Canvas checker A5", new SelectCommandCanvasVisitorListener(DriverFeature.getDriverManager(), paperA5));
    }

    /**
     * Setup driver manager, and set default Job2dDriver for application.
     *
     * @param application Application context.
     */
    private static void setupDrivers(Application application) {

        Job2dDriver loggerDriver = new LoggerDriver();
        DriverFeature.addDriver("Logger driver", loggerDriver);

        DrawPanelController drawerController = DrawerFeature.getDrawerController();
        Job2dDriver driver = new LineDriverAdapter(drawerController, LineFactory.getBasicLine(), "basic");
        DriverFeature.addDriver("Line Simulator", driver);
        DriverFeature.getDriverManager().setCurrentDriver(driver);

        driver = new LineDriverAdapter(drawerController, LineFactory.getSpecialLine(), "special");
        DriverFeature.addDriver("Special line Simulator", driver);

        TransformationDriver scaleDriver = new TransformationDriver(new Scale(0.5d, 0.5d), new LineDriverAdapter(drawerController, LineFactory.getBasicLine(), "basic line"));
        DriverFeature.addDriver("Scale (0.5x)", scaleDriver);
        TransformationDriver scaleDriver2 = new TransformationDriver(new Scale(1.5d, 1.5d), new LineDriverAdapter(drawerController, LineFactory.getBasicLine(), "basic line"));
        DriverFeature.addDriver("Scale (1.5x)", scaleDriver2);


        TransformationDriver rotateDriver = new TransformationDriver(new Rotate(Math.PI / 2), new LineDriverAdapter(drawerController, LineFactory.getBasicLine(), "basic line"));
        DriverFeature.addDriver("Rotate (PI/2)", rotateDriver);
        TransformationDriver rotateDriver2 = new TransformationDriver(new Rotate(Math.PI / 3), new LineDriverAdapter(drawerController, LineFactory.getBasicLine(), "basic line"));
        DriverFeature.addDriver("Rotate (PI/3)", rotateDriver2);

        TransformationDriver rotateDriver3 = new TransformationDriver(new Scale(1d, -1d), new LineDriverAdapter(drawerController, LineFactory.getBasicLine(), "basic line"));
        DriverFeature.addDriver("Flip (vertically)", rotateDriver3);
        TransformationDriver rotateDriver4 = new TransformationDriver(new Scale(-1d, 1d), new LineDriverAdapter(drawerController, LineFactory.getBasicLine(), "basic line"));
        DriverFeature.addDriver("Flip (horizontally)", rotateDriver4);

        DriverInfoUpdater subscriber = new DriverInfoUpdater();
        DriverFeature.getDriverManager().getPublisher().addSubscriber(subscriber);

        IDriverComposite compositeDriver = new DriverComposite();
        compositeDriver.add(driver);
        compositeDriver.add(loggerDriver);

        DriverFeature.addDriver("Composite Driver", compositeDriver);

        UsageMonitorManager.setDriver(driver);
        DriverFeature.addDriver("Monitored Driver", UsageMonitorManager.getDriver());
    }

    private static void setupExtensions(Application application) {
    	ExtensionFeature.addDriver("Macro", MacroFeature.getDriver());
    	Job2dDriver loggerDriver = new LoggerDriver();
        ExtensionFeature.addDriver("Logger", loggerDriver);
    	
    }
    
    private static void setupWindows(Application application) {


        CommandManagerWindow commandManager = new CommandManagerWindow(CommandsFeature.getDriverCommandManager());
        application.addWindowComponent("Command Manager", commandManager);

        CommandManagerWindowCommandChangeObserver windowObserver = new CommandManagerWindowCommandChangeObserver(
                commandManager);
        CommandsFeature.getDriverCommandManager().getChangePublisher().addSubscriber(windowObserver);
    }

    /**
     * Setup menu for adjusting logging settings.
     *
     * @param application Application context.
     */
    private static void setupLogger(Application application) {

        application.addComponentMenu(Logger.class, "Logger", 0);
        application.addComponentMenuElement(Logger.class, "Clear log",
                (ActionEvent e) -> application.flushLoggerOutput());
        application.addComponentMenuElement(Logger.class, "Fine level", (ActionEvent e) -> logger.setLevel(Level.FINE));
        application.addComponentMenuElement(Logger.class, "Info level", (ActionEvent e) -> logger.setLevel(Level.INFO));
        application.addComponentMenuElement(Logger.class, "Warning level",
                (ActionEvent e) -> logger.setLevel(Level.WARNING));
        application.addComponentMenuElement(Logger.class, "Severe level",
                (ActionEvent e) -> logger.setLevel(Level.SEVERE));
        application.addComponentMenuElement(Logger.class, "OFF logging", (ActionEvent e) -> logger.setLevel(Level.OFF));
    }

    private static void setupFeaturesMenu(Application application) {
        SelectTestMouseListener selectTestMouseListener = new SelectTestMouseListener(
                DriverFeature.getDriverManager(), application.getFreePanel());

        application.addComponentMenu(Feature.class, "Features");
        application.addComponentMenuElement(Feature.class, "Load secret command", new SelectLoadSecretCommandOptionListener());
        application.addComponentMenuElement(Feature.class, "Load triangle command", new SelectLoadTriangleCommandOptionListener());
        application.addComponentMenuElement(Feature.class, "Rotate command", new SelectRotateCurrentCommandOptionListener(CommandsFeature.getDriverCommandManager()));
        application.addComponentMenuElement(Feature.class, "Run command", new SelectRunCurrentCommandOptionListener(DriverFeature.getDriverManager()));
        application.addComponentMenuElement(Feature.class, "Count command", new SelectCurrentCommandCounter(CommandsFeature.getDriverCommandManager()));
        application.addComponentMenuElement(Feature.class, "Load Macro", new SelectLoadMacroOptionListener(MacroFeature.getDriver(), CommandsFeature.getDriverCommandManager()));
        application.addComponentMenuElement(Feature.class, "Clear Macro", new SelectClearMacroOptionListener(MacroFeature.getDriver(), CommandsFeature.getDriverCommandManager()));
    }

    private static void setupDriverMonitor(Application application) {
        application.addComponentMenu(MonitorDriverDecorator.class, "Driver Monitor", 5);
        application.addComponentMenuElement(MonitorDriverDecorator.class, "Print report", (ActionEvent e) -> UsageMonitorManager.printReport());
    }

    private static void setupMouseCheckbox(Application application) {
        JPanel panel = application.getFreePanel();

        JCheckBox mouseCheckbox = new JCheckBox("Enable mouse");
        mouseCheckbox.setToolTipText("Enable manual drawing with mouse.");
        mouseCheckbox.setBounds(0, 0, 100, 30);
        mouseCheckbox.setCursor(new Cursor(12));

        panel.add(mouseCheckbox, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NORTHWEST, new Insets(0, 0, 0, 0), 0, 0));

        CheckboxAction enableMouseAction = new CheckboxAction("Enable mouse");
        mouseCheckbox.setAction(enableMouseAction);
        enableMouseAction.addObserver(new MouseControlObserver(DriverFeature.getDriverManager(), application.getFreePanel()));
        enableMouseAction.addObserver(new MouseControlLoggerObserver());
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                Application app = new Application("Jobs 2D");
                ApplicationManager manager = new ApplicationManager();
                manager.addMany(new DriverFeature(app), new CommandsFeature(), new DrawerFeature(app), new MacroFeature());
                manager.add(new ExtensionFeature(app, DriverFeature.getDriverManager()));
                manager.executeAll();

                setupDrivers(app);
                setupPresetTests(app);
                setupCommandTests(app);
                setupLogger(app);
                setupWindows(app);
                setupDriverMonitor(app);
                setupFeaturesMenu(app);
                setupMouseCheckbox(app);
                setupExtensions(app);

                app.setVisibility(true);
            }
        });
    }

}
