package ReplicaHost4;

public class Message {
    public String seqId;
    public String FEHostAddress;
    public String department;
    public String message;

    public Message() {
    }

    public Message(String seqId, String FEHostAddress, String department, String message) {
        this.seqId = seqId;
        this.FEHostAddress = FEHostAddress;
        this.department = department;
        this.message = message;
    }

    public String getSeqId() {
        return seqId;
    }

    public void setSeqId(String seqId) {
        this.seqId = seqId;
    }

    public String getFEHostAddress() {
        return FEHostAddress;
    }

    public void setFEHostAddress(String FEHostAddress) {
        this.FEHostAddress = FEHostAddress;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
