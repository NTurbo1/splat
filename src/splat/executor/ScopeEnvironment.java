package splat.executor;

import java.util.Map;
import java.util.HashMap;

public class ScopeEnvironment {
    private Map<String, Value> localVarAndParamMap;

    public ScopeEnvironment(Map<String, Value> localVarAndParamMap)
    {
        this.localVarAndParamMap = localVarAndParamMap;
    }

    public Map<String, Value> getLocalVarAndParamMap() { return this.localVarAndParamMap; }
}
