//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ReplicaHost4.DCRS;

public enum DepartEnum {
    COMP(7777, "comp"),
    SOEN(7778, "soen"),
    INSE(7779, "inse");

    private int port;
    private String name;

    private DepartEnum(int port, String name) {
        this.port = port;
        this.name = name;
    }

    public int getPort() {
        return this.port;
    }

    public String getName() {
        return this.name;
    }

    public static DepartEnum getInstance(String input) {
        if (input.equals("comp")) {
            return COMP;
        } else {
            return input.equals("soen") ? SOEN : INSE;
        }
    }
}
