package cz.rj.zigbee;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class XMLGregorianCalendarConversionUtil {
    private static final DatatypeFactory df;

    static {
        try {
            df = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException(
                    "Error while trying to obtain a new instance of DatatypeFactory", e);
        }
    }

    public static OffsetDateTime unmarshalOffsetDateTime(String xmlDate) {
        if (xmlDate == null) {
            return null;
        }
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(xmlDate, OffsetDateTime::from);
    }

    public static String marshalOffsetDateTime(OffsetDateTime in) {
        if (in == null) {
            return null;
        }
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(in);
    }

    public static LocalDate unmarshalLocalDate(String xmlDate) {
        if (xmlDate == null) {
            return null;
        }
        return DateTimeFormatter.ISO_LOCAL_DATE.parse(xmlDate, LocalDate::from);
    }

    public static String marshalLocalDate(LocalDate in) {
        if (in == null) {
            return null;
        }
        return DateTimeFormatter.ISO_LOCAL_DATE.format(in);
    }

    public static XMLGregorianCalendar asGregorian(OffsetDateTime date) {
        if (date == null) {
            return null;
        } else {
//            GregorianCalendar gc = new GregorianCalendar();
//            gc.setTimeInMillis(date.getTime());
            return df.newXMLGregorianCalendar(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(date));
        }
    }

    public static OffsetDateTime asDate(XMLGregorianCalendar in) {
        if (in == null) {
            return null;
        }
        final String xmlString = in.toXMLFormat();
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(xmlString, OffsetDateTime::from);
    }
}
