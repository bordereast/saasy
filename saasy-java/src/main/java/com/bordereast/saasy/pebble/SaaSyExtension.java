package com.bordereast.saasy.pebble;

import java.util.HashMap;
import java.util.Map;

import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Function;

public class SaaSyExtension extends AbstractExtension {
	
    @Override
    public Map<String, Function> getFunctions() {
        Map<String, Function> functions = new HashMap<>();
        functions.put("label", new LabelFunction());
        functions.put("replace", new ReplaceFunction());
        return functions;
    }
}
