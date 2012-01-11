package cqf.status;

import cqf.status.Status;

/**
 *
 * @author birksworks
 */
public class AppStatus extends Status {
    // The sole instance or AppStatus
    private static AppStatus instance = new AppStatus();

    /**
     * Create an instance of Status, private since this is a Singleton.
     */
    private AppStatus() {}

    /**
     * Returns the instance for this AppStatus.
     *
     * @return	the one and only instance for this singleton.
     */
    public static AppStatus instance() {
        return instance;
    }
}
