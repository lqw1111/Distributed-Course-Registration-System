package ReplicaHost3.DCRS;

import java.util.HashMap;
import java.util.Map;

public class DepartmentToPort {
    static public Map<String, Integer> map = new HashMap<>();
    static {
        map.put("COMP", 6666);
        map.put("SOEN", 7777);
        map.put("INSE", 8888);
    }
}
