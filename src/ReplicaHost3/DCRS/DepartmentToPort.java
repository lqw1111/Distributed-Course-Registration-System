package ReplicaHost3.DCRS;

import java.util.HashMap;
import java.util.Map;

public class DepartmentToPort {
    static public Map<String, Integer> map = new HashMap<>();
    static {
        map.put("comp", 6667);
        map.put("soen", 6668);
        map.put("inse", 6669);
    }
}
