package ReplicaHost2.DCRS;

public class Certification {
    private String id;
    private String major;
    private String identity;
    private String number;

    public Certification(String id) {
        this.id = id.toLowerCase();
    }

    public String getId() {
        return id;
    }

    public String getMajor() {
        major = id.substring(0, 4);
        return major;
    }

    public String getIdentity() {
        identity = id.substring(4, 5);
        return identity;
    }

    public String getNumber() {
        number = id.substring(5);
        return number;
    }
}
