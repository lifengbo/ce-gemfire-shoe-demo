package io.pivotal.ce.gemfire.fastfootshoes.serverside;

import com.gemstone.gemfire.pdx.PdxInstance;

public class ReferenceHelper {
	
	@SuppressWarnings("unchecked")
	public static final <T> T toObject(final Object object, final Class<T> clazz) {
		if (object instanceof PdxInstance) {
			return (T) ((PdxInstance) object).getObject();
		}
		return (T) object;
	}

}
