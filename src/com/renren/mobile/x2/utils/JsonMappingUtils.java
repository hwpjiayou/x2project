package com.renren.mobile.x2.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonMappingUtils {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void map(Object o, JSONObject jo){
		Field[] fields = o.getClass().getFields();
		
		for(Field f : fields){
			JsonMapping jm = f.getAnnotation(JsonMapping.class);
			f.setAccessible(true);
			if(jm == null){
				return;
			}
			JSONObject tempJO = jo;
			for(int i = 0; i < jm.Tag().length - 1; ++i){
				try {
					tempJO = tempJO.getJSONObject(jm.Tag()[i]);
				} catch (JSONException e) {
					continue;
				}
			}
			String lastValue = jm.Tag()[jm.Tag().length - 1];
			if(jm.IsList()){
				try {
					if(f.get(o) == null){
						f.set(o, getNewObject(f.getType()));
					}
					assert(Collection.class.isInstance(f.get(o)));
					Collection list = (Collection) f.get(o);
					JSONArray ja = tempJO.getJSONArray(lastValue);
					int length = ja.length();
					for(int i = 0; i < length; ++i){
						Object item = getNewObject(jm.ItemClass());
						if(item != null){
							map(item, ja.getJSONObject(i));
							list.add(item);
						}
					}
				} catch (Throwable t) {}
			} else{
				try {
					f.set(o, tempJO.get(lastValue));
				} catch (Exception e) {
					try {
						f.set(o, null);
					} catch (Exception ignored) {}
				}
			}
		}
	}
	
	public final static Class<?>[] NullClassArrars = new Class[]{};
	public final static Object[] NullObjectArrars = new Object[]{};
	
	public static Object getNewObject(Class<?> clazz){
		Object o = null;
		try {
			o = clazz.getConstructor(NullClassArrars).newInstance(NullObjectArrars);
		} catch (Exception ignored) {}
		return o;
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target( { ElementType.FIELD })
	public static @interface JsonMapping {
		String[] Tag();
		boolean IsList() default false;
		Class<?> ItemClass() default Object.class;
	}
}
