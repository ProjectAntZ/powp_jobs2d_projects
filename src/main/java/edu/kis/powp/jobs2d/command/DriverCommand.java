package edu.kis.powp.jobs2d.command;

import edu.kis.powp.jobs2d.Job2dDriver;
import edu.kis.powp.jobs2d.command.visitor.Visitor;

/**
 * DriverCommand interface.
 */
public interface DriverCommand {
    /**
     * Execute command on driver.
     *
     * @param driver driver.
     */
    public void execute(Job2dDriver driver);

    public DriverCommand clone();

    void accept(Visitor visitor);
}
