package com.bordereast.saasy.pebble;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.extension.Function;

public class ReplaceFunction  implements Function {

	private final List<String> argumentNames = new ArrayList<>();
	
	public ReplaceFunction() {
        argumentNames.add("search");
        argumentNames.add("params");
    }
	
	@Override
	public List<String> getArgumentNames() {
		return argumentNames;
	}

	@Override
	public Object execute(Map<String, Object> args) {
		String search = (String) args.get("search");
		Object params = args.get("params");
		
		Object phraseObject = search;
		
		try {
			phraseObject = URLDecoder.decode(search, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
	
}
