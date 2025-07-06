package com.github.salandora.sophisticatedlibrary.model.loading;

import com.google.gson.JsonObject;

public interface IGeometryLoader<T extends IUnbakedGeometry> {
	T read(JsonObject modelContents);
}