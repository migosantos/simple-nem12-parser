package simplenem12;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

import static simplenem12.SimpleNemConstants.COMMA_DELIMITER;
import static simplenem12.SimpleNemConstants.END_OF_FILE;
import static simplenem12.SimpleNemConstants.LOCAL_DATE_FORMAT;
import static simplenem12.SimpleNemConstants.METER_READ;
import static simplenem12.SimpleNemConstants.START_METER_READ_BLOCK;
import static simplenem12.SimpleNemConstants.START_OF_FILE;

public class SimpleNem12ParserImpl implements SimpleNem12Parser {
    @Override
    public Collection<MeterRead> parseSimpleNem12(File simpleNem12File) throws IOException {
        Collection<MeterRead> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(simpleNem12File))) {
            String row = null;
            MeterRead meterRead = null;

            while ((row = br.readLine()) != null) {
                String[] columns = row.split(COMMA_DELIMITER);

                switch (columns[0]) {
                    case START_OF_FILE:
                        break;
                    case START_METER_READ_BLOCK:
                        meterRead = createNewNmiMeterRead(columns[1], columns[2]);
                        result.add(meterRead);
                        break;
                    case METER_READ:
                        MeterVolume meterVolume = createNewMeterVolume(columns[2], columns[3]);
                        meterRead.appendVolume(stringToLocalDate(columns[1]), meterVolume);
                        break;
                    case END_OF_FILE:
                    default:
                }
            }
        }

        return result;
    }

    private LocalDate stringToLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LOCAL_DATE_FORMAT);
        return LocalDate.parse(date, formatter);
    }

    private MeterVolume createNewMeterVolume(String volume, String quality) {
        return new MeterVolume(new BigDecimal(volume), Quality.valueOf(quality));
    }

    private MeterRead createNewNmiMeterRead(String nmi, String energyUnit) {
        return new MeterRead(nmi, EnergyUnit.valueOf(energyUnit));
    }
}
