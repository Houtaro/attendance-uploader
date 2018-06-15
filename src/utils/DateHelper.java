
package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javafx.scene.control.Alert;

public class DateHelper {
    
    public static boolean isWithinRange(String testDate, LocalDate startDate, LocalDate endDate) throws ParseException {
        
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatter3 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        
        Date d = formatter3.parse(testDate);
        LocalDate ld = LocalDate.parse(formatter2.format(d), formatter1);
        
        Date date_to_check = java.sql.Date.valueOf(ld);
        Date date_range_from = java.sql.Date.valueOf(startDate);
        Date date_range_to = java.sql.Date.valueOf(endDate);
        
        return !(date_to_check.before(date_range_from) || date_to_check.after(date_range_to));
    }
    
    
    public static String dateFormatter(String date, String oldPattern, String newPattern) {
        SimpleDateFormat sdf1 = new SimpleDateFormat(oldPattern);
        SimpleDateFormat sdf2 = new SimpleDateFormat(newPattern);
        try {
            Date d1 = sdf1.parse(date);
            date = sdf2.format(d1);
        } catch(ParseException e) {
            AlertHelper.show(Alert.AlertType.ERROR, "Error", "", e.getMessage());
        }
        return date;
    }
}
