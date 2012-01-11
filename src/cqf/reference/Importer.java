package cqf.reference;

import au.com.bytecode.opencsv.CSVReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class Importer {

    private static final Logger LOGGER = Logger.getLogger(Importer.class.getName());
    private static final String DATA_FILE_PATH = "/data/sp500.csv";

    public void importData() throws IOException, Exception {
        CSVReader reader = new CSVReader(new InputStreamReader(getClass().getResourceAsStream(DATA_FILE_PATH)));
        String[] headers = reader.readNext();
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            if (nextLine[0].startsWith("]")) break;
            if (nextLine[0].startsWith("#")) continue;
            Sector sector;
            try {
                sector = Sector.lookup(nextLine[2]);
            } catch (IllegalArgumentException ex) {
                sector = new Sector(nextLine[2]);
            }
            Entity entity = new Entity(nextLine[0], nextLine[1], sector);
            sector.add(entity);
        }
    }
}
