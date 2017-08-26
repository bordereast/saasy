package com.bordereast.saasy.dal;

import com.arangodb.ArangoDB;
import com.arangodb.velocypack.module.jdk8.VPackJdk8Module;

public class ArangoBuilderService {
	
	private static ArangoBuilderService _instance = new ArangoBuilderService();
	private ArangoDB arangoDB = null;
	
	private ArangoBuilderService(){}
	
	public static ArangoBuilderService getInstance(){
		
		
		return _instance;
	}
	
	public ArangoDB getArangoDB(){
		if(arangoDB == null){
			arangoDB = new ArangoDB
					.Builder()
					.user("saasy")
					.password("saasy")
					.maxConnections(20)
					.registerModule(new VPackJdk8Module())
					.build();
		}
		return this.arangoDB;
	}
}
