
package utils;

public class GlobalHelper {
   
    public static String date_period_from = "";
    public static String date_period_to = "";
    
    public static String employeeIdFormatter(String employeeId) {
        return employeeId.substring(employeeId.length() - 4);
    }
}
