package com.bordereast.saasy.pebble;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import com.bordereast.saasy.cache.ResourceCache;
import com.bordereast.saasy.file.UTF8Control;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.template.EvaluationContext;



public class LabelFunction implements Function {

	private final List<String> argumentNames = new ArrayList<>();
	
	public LabelFunction() {
        argumentNames.add("bundle");
        argumentNames.add("key");
        argumentNames.add("params");
    }
	
	@Override
	public List<String> getArgumentNames() {
		return argumentNames;
	}

	@Override
	public Object execute(Map<String, Object> args) {
		String basename = (String) args.get("bundle");
        String key = (String) args.get("key");
        Object params = args.get("params");

        EvaluationContext context = (EvaluationContext) args.get("_context");
        Locale locale = context.getLocale();

        ResourceBundle bundle = null;
        
        if(ResourceCache.Resources.containsKey(toKey(basename, locale))){
        	bundle = ResourceCache.Resources.get(toKey(basename, locale));
        } else {
        	bundle = ResourceBundle.getBundle(basename, locale, new UTF8Control());
        	ResourceCache.Resources.put(toKey(basename, locale), bundle);
        }
        
        Object phraseObject = bundle.getObject(key);

        if (phraseObject != null && params != null) {
            if (params instanceof List) {
                List<?> list = (List<?>) params;
                return MessageFormat.format(phraseObject.toString(), list.toArray());
            } else {
                return MessageFormat.format(phraseObject.toString(), params);
            }
        }

        return phraseObject;
	}
	
	private String toKey(String basename, Locale locale){
		if(locale == null || locale.equals(Locale.getDefault())) return basename.toLowerCase();
		return basename.toLowerCase() + "_" + locale.toString().toLowerCase();
	}

}
