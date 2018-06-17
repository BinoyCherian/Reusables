package com.epita.fr.reusables;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * Enterprise class to allow some automations
 * 
 * @author raaool
 *
 * @param <T>
 */
public class Reflecto<T> {
	
	
	/**
	 * Method generates a list of attributes for a class using reflections.
	 * 
	 * @param entity The class to work on
	 * @return Map The list with all attributes
	 */
	public Map linkedListBuilder(T entity) {

		final BeanWrapper sourceBean = new BeanWrapperImpl(entity.getClass());
		final PropertyDescriptor[] propertyDescriptors = sourceBean.getPropertyDescriptors();
		final Map<String, Object> parameters = new LinkedHashMap<>();

		for (final PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			Method method = propertyDescriptor.getReadMethod();

			try {

				Object o = method.invoke(entity);

				if (o != null && !method.getName().contains("Class")) {
					parameters.put(propertyDescriptor.getDisplayName(), o);
					System.out.println("Inserting Key: " + propertyDescriptor.getName() + " with Value: " + o);
				}

			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return parameters;
	}


	/**
	 * The method reads a class and generates a hql query based on the attributes
	 * of the class except for the class attribute.
	 * 
	 * @param entity The generic class entity
	 */
	public void generateHqlString(T entity) {

		final BeanWrapper sourceBean = new BeanWrapperImpl(entity.getClass());
		final PropertyDescriptor[] propertyDescriptors = sourceBean.getPropertyDescriptors();

		final Map<String, Object> parameters = new LinkedHashMap<>();

		int i = 0;

		String simpleName = entity.getClass().getSimpleName();

		String baseName = "from " + simpleName + " as " + simpleName.toLowerCase() + " where ";
		for (final PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			if (propertyDescriptor.getName() != null && !propertyDescriptor.getName().contains("class")) {
				baseName += whereClauseBuilder(simpleName, propertyDescriptor.getName(), i);
				i++;
			}
		}
	}

	/**
	 * The string creates the where clause for the generateHqlString
	 * 
	 * @param simpleName The simple name
	 * @param attributeName The attribute name
	 * @param i The count
	 * @return String The where clause
	 */
	private String whereClauseBuilder(String simpleName, String attributeName, int i) {

		String str = null;

		if (i == 0) {
			str = simpleName.toLowerCase() + "." + attributeName + " = :" + attributeName;
		}

		else {
			str = " and " + simpleName.toLowerCase() + "." + attributeName + " = :" + attributeName;
		}
		return str;
	}
}
