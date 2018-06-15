
package models;

public class Attendance {
    
    private String employee_id = "";
    private String employee_name = "";
    private String reference_id = "";
    private String time_in = "";
    private String time_out = "";
    private String punch_type = "";
    private String punch_stamp = "";
    private String device_type = "";
    private String device_id = "";
    private String remarks = "";
    
    /**
     * @return the reference_id
     */
    public String getReference_id() {
        return reference_id;
    }

    /**
     * @param reference_id the reference_id to set
     */
    public void setReference_id(String reference_id) {
        this.reference_id = reference_id;
    }

    /**
     * @return the employee_id
     */
    public String getEmployee_id() {
        return employee_id;
    }

    /**
     * @param employee_id the employee_id to set
     */
    public void setEmployee_id(String employee_id) {
        this.employee_id = employee_id;
    }

    /**
     * @return the punch_type
     */
    public String getPunch_type() {
        return punch_type;
    }

    /**
     * @param punch_type the punch_type to set
     */
    public void setPunch_type(String punch_type) {
        this.punch_type = punch_type;
    }

    /**
     * @return the punch_stamp
     */
    public String getPunch_stamp() {
        return punch_stamp;
    }

    /**
     * @param punch_stamp the punch_stamp to set
     */
    public void setPunch_stamp(String punch_stamp) {
        this.punch_stamp = punch_stamp;
    }

    /**
     * @return the device_type
     */
    public String getDevice_type() {
        return device_type;
    }

    /**
     * @param device_type the device_type to set
     */
    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    /**
     * @return the device_id
     */
    public String getDevice_id() {
        return device_id;
    }

    /**
     * @param device_id the device_id to set
     */
    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    /**
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * @return the time_in
     */
    public String getTime_in() {
        return time_in;
    }

    /**
     * @param time_in the time_in to set
     */
    public void setTime_in(String time_in) {
        this.time_in = time_in;
    }

    /**
     * @return the time_out
     */
    public String getTime_out() {
        return time_out;
    }

    /**
     * @param time_out the time_out to set
     */
    public void setTime_out(String time_out) {
        this.time_out = time_out;
    }

    /**
     * @return the employee_name
     */
    public String getEmployee_name() {
        return employee_name;
    }

    /**
     * @param employee_name the employee_name to set
     */
    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }
    
}
