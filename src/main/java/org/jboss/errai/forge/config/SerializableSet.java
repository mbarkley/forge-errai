package org.jboss.errai.forge.config;

import java.util.HashSet;
import java.util.Iterator;

public class SerializableSet extends HashSet<String> {

  private static final long serialVersionUID = -4968825975745239833L;
  private static final String delimeter = "|";
  
  public String serialize() {
    final StringBuilder builder = new StringBuilder();
    
    final Iterator<String> iterator = iterator();
    if (iterator.hasNext())
      builder.append(iterator.next());
    
    while (iterator.hasNext()) {
      builder.append(delimeter).append(iterator.next());
    }
    
    return builder.toString();
  }
  
  public static SerializableSet deserialize(final String serialized) {
    final SerializableSet set = new SerializableSet();
    final String[] items = serialized.split(delimeter);
    
    for (int i = 0; i < items.length; i++) {
      set.add(items[i]);
    }
    
    return set;
  }

}
