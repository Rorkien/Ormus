package com.ormus.server.service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.ormus.server.model.handlers.Handler;

/**
 * Servi�o respons�vel por carregar os recursos
 * que ser�o utilizados pelo servidor.
 * 
 * Estes recursos incluem criaturas, itens, efeitos
 * mapas e mundos.
 * 
 * Este servi�o utiliza os handlers. Handlers s�o as
 * classes respons�veis por prover os objetos � outras
 * partes do servidor.
 */
public class ResourceLoadingService extends AbstractService<Void> {
	private Map<Handler<?, ?>, String> resourceFile = new HashMap<Handler<?, ?>, String>();
	private Map<Handler<?, ?>, Class<?>> resourceMappings = new HashMap<Handler<?, ?>, Class<?>>();
	
	public void registerResource(Handler<?, ?> handledObject, Class<?> targetClass, String filename) {
		resourceFile.put(handledObject, filename);
		resourceMappings.put(handledObject, targetClass);
	}

	@Override
	public void execute() {	
		for (Entry<Handler<?, ?>, String> handle : resourceFile.entrySet()) {
			File file = new File("assets/" + handle.getValue());
			try {
				FileReader reader = new FileReader(file);
				Gson jsonDeserializer = new Gson();

				handle.getKey().offer(jsonDeserializer.fromJson(new JsonReader(reader), resourceMappings.get(handle.getKey())));
			} catch (IOException e) {
				System.out.printf("Error loading asset: %s (%s)", handle.getValue(), e.getMessage());
			}		
		}
	}
}
